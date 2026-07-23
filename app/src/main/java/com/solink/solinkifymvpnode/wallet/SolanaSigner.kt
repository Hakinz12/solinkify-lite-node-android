package com.solink.solinkifymvpnode.wallet

import android.content.Context
import com.solink.solinkifymvpnode.constants.Constants
import com.solink.solinkifymvpnode.model.AuthMessage

class SolanaSigner(
    private val context: Context
) {

    fun signChallenge(nonce: String): AuthMessage {

        val message = Constants.AUTH_DOMAIN + nonce

        TODO("Implement signing")

    }
}