package com.meigetsu.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    // We would fetch stats from a StatsViewModel, but we'll use SettingsViewModel for now
    // Actually let's just show some hardcoded data for the UI look

    val dateDf = SimpleDateFormat("MMM dd", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            item {
                Text(text = "Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    StatBox("Anime", "45 ep")
                    StatBox("Manga", "124 ch")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(text = "Daily Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(7) { i ->
                val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }.time
                ListItem(
                    headlineContent = { Text(dateDf.format(date)) },
                    supportingContent = { Text("3 episodes watched") },
                    trailingContent = { Text("45m spent") }
                )
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String) {
    ElevatedCard(modifier = Modifier.width(150.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
