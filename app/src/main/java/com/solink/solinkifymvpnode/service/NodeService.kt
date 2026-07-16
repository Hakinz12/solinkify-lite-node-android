package com.solink.solinkifymvpnode.service

import com.solink.solinkifymvpnode.network.WebSocketManager
import com.solink.solinkifymvpnode.constants.Constants

class NodeService {

    private val webSocketManager = WebSocketManager()

    fun startNode() {
        android.util.Log.d("LiteNode", "Starting node...")
        webSocketManager.connect(Constants.WS_URL)
    }

    fun stopNode() {
        webSocketManager.disconnect()
    }
}