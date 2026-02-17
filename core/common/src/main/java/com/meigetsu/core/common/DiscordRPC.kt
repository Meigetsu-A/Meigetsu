package com.meigetsu.core.common

import android.util.Log

object DiscordRPC {
    private var currentDetails: String? = null
    private var currentState: String? = null

    fun updatePresence(details: String, state: String) {
        currentDetails = details
        currentState = state

        // Broadcast to potential RPC listeners or companion apps
        Log.i("DiscordRPC", "RPC Update: $details ($state)")
    }

    fun clearPresence() {
        currentDetails = null
        currentState = null
        Log.i("DiscordRPC", "RPC Cleared")
    }
}
