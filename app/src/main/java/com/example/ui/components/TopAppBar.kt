package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TubeMateAccent
import com.example.ui.theme.TubeMatePrimary

@Composable
fun TubeMateTopBar(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onSubmitUrl: (String) -> Unit,
    onOpenLeftDrawer: () -> Unit,
    onOpenPlaylist: () -> Unit,
    onOpenSettings: () -> Unit,
    isDesktopMode: Boolean,
    onToggleDesktopMode: () -> Unit,
    isBlockAutoplay: Boolean,
    onToggleBlockAutoplay: () -> Unit,
    onClearCache: () -> Unit,
    onOpenApkExport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(TubeMatePrimary)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .testTag("tube_mate_top_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hamburger icon to open left drawer
            IconButton(
                onClick = onOpenLeftDrawer,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("left_drawer_hamburger_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Bookmarks and History",
                    tint = Color.White
                )
            }

            // URL bar with Lock icon
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure Connection",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    BasicTextField(
                        value = urlInput,
                        onValueChange = onUrlChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("browser_url_input"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color(0xFF212121),
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(TubeMateAccent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = { onSubmitUrl(urlInput) })
                    )

                    if (urlInput.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear URL",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onUrlChange("") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Cast icon (as seen in screenshots 00:03 - 00:10)
            IconButton(
                onClick = { /* Cast feature */ },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = "Cast to screen",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Tabs button "1" (as in screenshots)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .border(1.5.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                    .clickable { /* Tab manager */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3-dots overflow menu (as in screenshots 00:17 - 00:19 & 00:35 - 00:38)
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("overflow_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(Color.White)
                        .width(220.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Clear local cache", fontSize = 15.sp) },
                        onClick = {
                            showMenu = false
                            onClearCache()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Block Autoplay", fontSize = 15.sp, modifier = Modifier.weight(1f))
                                Switch(
                                    checked = isBlockAutoplay,
                                    onCheckedChange = { onToggleBlockAutoplay() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = TubeMateAccent)
                                )
                            }
                        },
                        onClick = { onToggleBlockAutoplay() }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Desktop", fontSize = 15.sp, modifier = Modifier.weight(1f))
                                Switch(
                                    checked = isDesktopMode,
                                    onCheckedChange = { onToggleDesktopMode() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = TubeMateAccent)
                                )
                            }
                        },
                        onClick = { onToggleDesktopMode() }
                    )

                    DropdownMenuItem(
                        text = { Text("Playlist", fontSize = 15.sp) },
                        onClick = {
                            showMenu = false
                            onOpenPlaylist()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Settings", fontSize = 15.sp) },
                        onClick = {
                            showMenu = false
                            onOpenSettings()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📦 Get App APK", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TubeMateAccent)
                            }
                        },
                        onClick = {
                            showMenu = false
                            onOpenApkExport()
                        }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Exit", fontSize = 15.sp, color = Color.Red) },
                        onClick = { showMenu = false }
                    )
                }
            }
        }
    }
}
