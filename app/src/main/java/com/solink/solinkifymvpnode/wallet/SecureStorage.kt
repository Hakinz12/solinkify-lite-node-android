package com.solink.solinkifymvpnode.wallet

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "wallet_storage",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePrivateKey(key: String) {
        prefs.edit().putString("private_key", key).apply()
    }

    fun getPrivateKey(): String? {
        return prefs.getString("private_key", null)
    }

    fun clearWallet() {
        prefs.edit().clear().apply()
    }
}