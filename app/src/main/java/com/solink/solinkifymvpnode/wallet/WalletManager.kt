package com.solink.solinkifymvpnode.wallet

class WalletManager {

    fun getPublicKey(): String {
        return "TEST_PUBLIC_KEY"
    }

    fun sign(message: ByteArray): ByteArray {

        println("Signing message: ${String(message)}")

        return ByteArray(64)
    }
}