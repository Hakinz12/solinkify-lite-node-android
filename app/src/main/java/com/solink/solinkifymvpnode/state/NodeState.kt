package com.solink.solinkifymvpnode.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NodeState {

    var connected by mutableStateOf(false)

    var jobsRelayed by mutableStateOf(0)

    var lastLog by mutableStateOf("Starting node...")
    var running = false


}