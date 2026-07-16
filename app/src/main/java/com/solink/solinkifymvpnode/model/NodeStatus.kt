package com.solink.solinkifymvpnode.model

enum class NodeStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    AUTHENTICATING,
    READY,
    RELAYING,
    ERROR
}