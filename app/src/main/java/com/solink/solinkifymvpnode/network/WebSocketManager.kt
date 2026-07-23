package com.solink.solinkifymvpnode.network

import android.util.Log
import com.google.gson.Gson
import com.solink.solinkifymvpnode.model.Challenge
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import com.solink.solinkifymvpnode.wallet.WalletManager
import com.solink.solinkifymvpnode.model.AuthMessage
import android.util.Base64
import com.solink.solinkifymvpnode.constants.Constants
import com.solink.solinkifymvpnode.model.Job
import com.solink.solinkifymvpnode.service.RelayService
import com.solink.solinkifymvpnode.model.ResultMessage
import com.solink.solinkifymvpnode.state.NodeState
import com.solink.solinkifymvpnode.wallet.SolanaSigner
import android.content.Context

class WebSocketManager(
    private val context: Context
) {

    private val client = OkHttpClient()
    private val gson = Gson()
    //private val walletManager = WalletManager()
    private val relayService = RelayService()
    private var shouldReconnect = true
    private var coordinatorUrl = ""
    private var reconnectAttempts = 0
    private var webSocket: WebSocket? = null

    fun connect(url: String) {
        coordinatorUrl = url

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("LiteNode", "Connected to coordinator")
                    reconnectAttempts = 0
                    NodeState.connected = true
                    NodeState.lastLog = "Connected to coordinator"
                }

                override fun onMessage(webSocket: WebSocket, text: String) {

                    Log.d("LiteNode", "Received: $text")

                    val json = JSONObject(text)

                    when (json.getString("type")) {

                        "challenge" -> {

                            Log.d("LiteNode", "Challenge received")

                            NodeState.lastLog = "Signing challenge..."

                            try {

                                val signer = SolanaSigner(context)

                                val auth = signer.signChallenge(
                                    json.getString("nonce")
                                )

                                val authJson = gson.toJson(auth)

                                webSocket.send(authJson)

                                Log.d(
                                    "LiteNode",
                                    "Authentication sent"
                                )

                                NodeState.lastLog =
                                    "Authentication sent"

                            } catch (e: Exception) {

                                Log.e(
                                    "LiteNode",
                                    "Authentication failed",
                                    e
                                )

                                NodeState.lastLog =
                                    "Authentication failed"

                            }

                        }
                        "ready" -> {

                            Log.d("LiteNode", "✅ Node authenticated")

                            Log.d(
                                "LiteNode",
                                "Waiting for relay jobs..."
                            )
                        }

                        "job" -> {

                            Log.d("LiteNode", "📦 Relay job received")

                            val jobId = json.getString("job_id")
                            val job = gson.fromJson(text, Job::class.java)

                            Log.d("LiteNode", "Transaction received")

                            Log.d("LiteNode", "TX size: ${job.tx.length} bytes")

                            val signature = relayService.relay(job)

                            val result = ResultMessage(
                                type = "result",
                                job_id = job.job_id,
                                signature = signature ?: ""
                            )

                            Log.d(
                                "LiteNode",
                                "Prepared result message"
                            )
                            val resultJson = gson.toJson(result)

                            webSocket.send(resultJson)

                            Log.d(
                                "LiteNode",
                                "Result sent to coordinator"
                            )
                            Log.d("LiteNode", "Preparing transaction relay...")

                            Log.d(
                                "LiteNode",
                                "Job ID: $jobId"
                            )
                        }

                        else -> {
                            Log.d("LiteNode", "Unknown message: $text")
                        }
                    }
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                )
                {
                    Log.d("LiteNode", "Closing: $reason")
                    NodeState.connected = false
                    NodeState.lastLog = "Connection closed"
                    if (shouldReconnect) {

                        Thread {
                            val delay = minOf(5000L * (reconnectAttempts + 1), 30000L)
                            Log.d(
                                "LiteNode",
                                "Reconnect attempt ${reconnectAttempts + 1} in ${delay / 1000} seconds..."
                            )

                            Thread.sleep(delay)

                            reconnectAttempts++
                            connect(coordinatorUrl)
                            Log.d(
                                "LiteNode",
                                "Attempting reconnection..."
                            )
                        }.start()
                    }
                    webSocket.close(code, reason)
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    Log.e("LiteNode", "Connection failed", t)

                    NodeState.connected = false
                    NodeState.lastLog = t.message ?: "Unknown error"

                    response?.let {
                        Log.e("LiteNode", "HTTP ${it.code}: ${it.message}")
                    }
                }
            }
        )
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        shouldReconnect = false
        webSocket?.close(1000, "Disconnected")
    }
}