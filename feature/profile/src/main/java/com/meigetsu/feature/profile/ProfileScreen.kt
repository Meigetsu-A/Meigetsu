package com.meigetsu.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meigetsu.core.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MEIGETSU", style = Typography.titleLarge, fontSize = 32.sp)
                Text("Version 2.0.0", style = Typography.labelSmall)
            }
        }

        item { Spacer(Modifier.height(48.dp)) }

        item { SectionHeader("Appearance") }
        item {
            ProfileItem("Default Reader Mode", "Paged")
            ProfileItem("Video Quality", "1080p")
        }

        item { Spacer(Modifier.height(32.dp)) }

        item { SectionHeader("Sources") }
        item {
            ProfileItem("GogoAnime", "Active")
            ProfileItem("MangaFire", "Active")
        }

        item { Spacer(Modifier.height(32.dp)) }

        item { SectionHeader("Storage") }
        item {
            ProfileItem("Database Size", "1.2 MB")
            ProfileItem("Cache Size", "45 MB")
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText)
            ) {
                Text("CLEAR CACHE", style = Typography.labelLarge)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title.uppercase(),
        style = Typography.titleLarge,
        fontSize = 18.sp,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun ProfileItem(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = Typography.bodyLarge)
        Text(value, style = Typography.labelLarge)
    }
}
