package com.solink.solinkifymvpnode.rpc

import android.util.Log

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

        // Placeholder until we integrate Solana SDK
        return "PENDING_SIGNATURE"
    }
}