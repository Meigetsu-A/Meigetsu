package com.meigetsu.feature.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meigetsu.core.ui.theme.*
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val primaryColor by viewModel.primaryColor.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val cornerRadius by viewModel.cornerRadius.collectAsState()
    val colors = listOf(0xFF00BFFF, 0xFFFF4500, 0xFF32CD32, 0xFFFFD700, 0xFFDA70D6, 0xFFFFFFFF)
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background).padding(24.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MEIGETSU", style = Typography.titleLarge, fontSize = 32.sp)
                Text("Version 2.0.0", style = Typography.labelSmall)
            }
        }
        item { Spacer(Modifier.height(48.dp)) }
        item { SectionHeader("Theme Builder") }
        item {
            Text("Primary Color", style = Typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(colors) { color ->
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(color)).clickable { viewModel.setPrimaryColor(color) }.let {
                            if (primaryColor == color) it.background(Color.White.copy(0.3f), CircleShape).padding(4.dp).background(Color(color), CircleShape) else it
                        }
                    )
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Theme Mode", style = Typography.bodyMedium)
                Row {
                    ThemeButton("DARK", themeMode == "DARK") { viewModel.setThemeMode("DARK") }
                    ThemeButton("LIGHT", themeMode == "LIGHT") { viewModel.setThemeMode("LIGHT") }
                    ThemeButton("SYSTEM", themeMode == "SYSTEM") { viewModel.setThemeMode("SYSTEM") }
                }
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
        item { SectionHeader("Legal & Safety") }
        item {
            ProfileItem("DMCA Policy", "View")
            ProfileItem("Terms of Use", "View")
            ProfileItem("Open Source License", "MIT")
        }
        item { Spacer(Modifier.height(32.dp)) }
        item {
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(primaryColor))
            ) {
                Text("CLEAR ALL CACHE", style = Typography.labelLarge)
            }
        }
    }
}
@Composable
fun ThemeButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        label,
        modifier = Modifier.padding(horizontal = 8.dp).clickable { onClick() },
        style = Typography.labelSmall,
        color = if (selected) Color.White else MutedText
    )
}
@Composable
fun SectionHeader(title: String) {
    Text(title.uppercase(), style = Typography.titleLarge, fontSize = 18.sp, modifier = Modifier.padding(vertical = 12.dp))
}
@Composable
fun ProfileItem(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = Typography.bodyLarge)
        Text(value, style = Typography.labelLarge)
    }
}
