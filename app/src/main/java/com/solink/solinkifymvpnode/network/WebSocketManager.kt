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

class WebSocketManager {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val walletManager = WalletManager()
    private val relayService = RelayService()
    private var shouldReconnect = true
    private var coordinatorUrl = ""
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
                }

                override fun onMessage(webSocket: WebSocket, text: String) {

                    Log.d("LiteNode", "Received: $text")

                    val json = JSONObject(text)

                    when (json.getString("type")) {

                        "challenge" -> {
                            val challenge = gson.fromJson(text, Challenge::class.java)

                            Log.d("LiteNode", "Nonce received: ${challenge.nonce}")
                            val message =
                                (Constants.AUTH_DOMAIN + challenge.nonce).toByteArray()

                            val signature = walletManager.sign(message)

                            val authMessage = AuthMessage(
                                type = "auth",
                                wallet = walletManager.getPublicKey(),
                                signature = Base64.encodeToString(signature, Base64.NO_WRAP)
                            )

                            Log.d("LiteNode", "Prepared auth message")
                            val authJson = gson.toJson(authMessage)
                            webSocket.send(authJson)

                            Log.d("LiteNode", "Auth message sent")
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
                            relayService.relay(job)

                            val result = ResultMessage(
                                type = "result",
                                job_id = job.job_id,
                                signature = "PENDING_SIGNATURE")

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
                ) {
                    Log.d("LiteNode", "Closing: $reason")
                    if (shouldReconnect) {
                        Log.d("LiteNode", "Reconnecting in 5 seconds...")

                        Thread {
                            Thread.sleep(5000)
                            connect(coordinatorUrl)
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