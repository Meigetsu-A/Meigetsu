package com.meigetsu.core.common

import android.util.Log

object DiscordRPC {
    fun updatePresence(details: String, state: String) {
        // Simulated RPC update
        Log.d("DiscordRPC", "Updating presence: $details - $state")
    }
}
