package com.meigetsu.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Theme Builder", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { /* Pick color */ }) {
            Text("Select Primary Color")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "About Meigetsu", style = MaterialTheme.typography.titleMedium)
        Text(text = "Meigetsu is a content aggregator. We do not host content.", style = MaterialTheme.typography.bodySmall)
    }
}
