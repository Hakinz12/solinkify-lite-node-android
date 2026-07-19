package com.solink.solinkifymvpnode.wallet

import android.content.Context

class WalletManager(context: Context) {

    private val storage = SecureStorage(context)

    fun importWallet(privateKey: String): Boolean {
        storage.savePrivateKey(privateKey)
        return true
    }

    fun hasWallet(): Boolean {
        return storage.getPrivateKey() != null
    }

    fun getPrivateKey(): String? {
        return storage.getPrivateKey()
    }

    fun removeWallet() {
        storage.clearWallet()
    }
    fun getWalletBs58(): String? {
        return storage.getPrivateKey()
    }
}