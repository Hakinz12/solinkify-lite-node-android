package com.solink.solinkifymvpnode.model

data class AuthMessage (
    val type: String,
            val wallet: String,
                    val signature: String
    )