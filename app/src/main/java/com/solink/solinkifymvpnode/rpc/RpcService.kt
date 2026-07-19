package com.solink.solinkifymvpnode.rpc

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class RpcService {

    fun submitTransaction(txBytes: ByteArray): String {

        Log.d(
            "LiteNode",
            "Submitting transaction to Solana RPC..."
        )

        Log.d(
            "LiteNode",
            "Transaction size: ${txBytes.size} bytes"
        )

        val client = OkHttpClient()

        val body = JSONObject()
        body.put("jsonrpc", "2.0")
        body.put("id", 1)
        body.put("method", "sendTransaction")
        body.put(
            "params",
            org.json.JSONArray()
                .put(android.util.Base64.encodeToString(
                    txBytes,
                    android.util.Base64.NO_WRAP
                ))
                .put(
                    JSONObject()
                        .put("encoding", "base64")
                )
        )

        val request = Request.Builder()
            .url("https://api.devnet.solana.com")
            .post(
                body.toString().toRequestBody(
                    "application/json".toMediaType()
                )

            )
            .build()

        return try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body!!.string())
            json.optString("result", "")
        } catch (e: IOException) {
            Log.e("LiteNode", "RPC Error", e)
            ""
        }
    }
}