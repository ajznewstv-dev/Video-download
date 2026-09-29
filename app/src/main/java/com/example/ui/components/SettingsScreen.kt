package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TubeMatePrimary
import com.example.ui.theme.TubeMateTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TubeMate",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TubeMatePrimary)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .testTag("settings_screen")
        ) {
            // Basics Section
            item {
                SettingsSectionHeader(title = "Basics")
                SettingsFolderItem(
                    title = "Video folder",
                    subtitle = "/storage/emulated/0/Movies"
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsFolderItem(
                    title = "Audio folder",
                    subtitle = "/storage/emulated/0/Music"
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            // Advanced Section
            item {
                SettingsSectionHeader(title = "Advanced")
                SettingsClickableItem(title = "Download")
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "Convert")
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "User interface")
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "Troubleshooting")
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            // Information Section
            item {
                SettingsSectionHeader(title = "Information")
                SettingsFolderItem(
                    title = "Check for updates",
                    subtitle = "3.4.23.1571"
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "Release Notes")
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "Feedback")
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "Privacy Policy")
                HorizontalDivider(color = Color(0xFFEEEEEE))
                SettingsClickableItem(title = "View/Update User Consent")
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = TubeMateTeal,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsFolderItem(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Edit folder */ }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun SettingsClickableItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* action */ }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF212121)
        )
    }
}
