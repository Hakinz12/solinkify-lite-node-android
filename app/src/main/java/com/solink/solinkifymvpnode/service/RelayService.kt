package com.solink.solinkifymvpnode.service

import android.util.Log
import com.solink.solinkifymvpnode.model.Job
import android.util.Base64
import com.solink.solinkifymvpnode.rpc.RpcService

class RelayService {
    private val rpcService = RpcService()

    fun relay(job: Job) {

        Log.d(
            "LiteNode",
            "Preparing relay..."
        )

        Log.d(
            "LiteNode",
            "Job ID: ${job.job_id}"
        )

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

        Log.d(
            "LiteNode",
            "RPC Result: $signature"
        )
    }
}