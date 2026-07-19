package com.solink.solinkifymvpnode.service

import android.util.Log
import com.solink.solinkifymvpnode.model.Job
import android.util.Base64
import com.solink.solinkifymvpnode.rpc.RpcService
import com.solink.solinkifymvpnode.state.NodeState

class RelayService {
    private val rpcService = RpcService()

    fun relay(job: Job): String? {

        Log.d(
            "LiteNode",
            "Preparing relay..."
        )

        Log.d(
            "LiteNode",
            "Job ID: ${job.job_id}"
        )

        NodeState.lastLog =
            "Relayed job ${job.job_id}"

        Log.d(
            "LiteNode",
            "Transaction length: ${job.tx.length}")
            val txBytes = Base64.decode(
                job.tx,
        Base64.DEFAULT
        )

        Log.d(
            "LiteNode",
            "Decoded transaction: ${txBytes.size} bytes"
        )
        val signature = rpcService.submitTransaction(txBytes)
        if (signature != null && signature.isNotEmpty()) {
            NodeState.lastLog = "Relay successful: ${job.job_id}"
        } else {
            NodeState.lastLog = "Relay failed: ${job.job_id}"
        }
        Log.d(
            "LiteNode",
            "Transaction Signature: $signature"
        )

        Log.d(
            "LiteNode",
            "RPC Result: $signature"
        )
        return signature
    }
}