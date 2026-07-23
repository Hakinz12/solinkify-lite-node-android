package com.solink.solinkifymvpnode.service

import android.util.Log
import com.solink.solinkifymvpnode.constants.Constants
import com.solink.solinkifymvpnode.network.WebSocketManager
import com.solink.solinkifymvpnode.state.NodeState
import android.content.Context

class NodeService(
    private val context: Context
) {
    private val webSocketManager = WebSocketManager(context)

    private var running = false

    fun startNode() {

        if (running) return

        running = true

        Log.d("LiteNode", "Starting node...")

        NodeState.lastLog = "Starting node..."

        webSocketManager.connect(Constants.WS_URL)
        NodeState.running = true

    }

    fun stopNode() {

        if (!running) return

        running = false

        Log.d("LiteNode", "Stopping node...")

        NodeState.connected = false
        NodeState.lastLog = "Node stopped"

        webSocketManager.disconnect()
        NodeState.running = false
    }

    fun isRunning(): Boolean {
        return running
    }
}