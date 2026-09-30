package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DownloadEntity
import com.example.ui.theme.TubeMateAccent
import com.example.ui.theme.TubeMateCyan
import com.example.ui.theme.TubeMateGreen
import com.example.ui.theme.TubeMatePrimary

@Composable
fun RightDrawerContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    allDownloads: List<DownloadEntity>,
    completedVideos: List<DownloadEntity>,
    completedAudios: List<DownloadEntity>,
    searchQuery: String,
    isSearchVisible: Boolean,
    onToggleSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (String, Long) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onPlayMedia: (DownloadEntity) -> Unit,
    onPauseAll: () -> Unit,
    onResumeAll: () -> Unit,
    onOpenApkExport: () -> Unit = {}
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(340.dp)
            .background(Color.White)
            .testTag("right_drawer_content")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with 4 Tabs and 3-dot menu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TubeMatePrimary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = TubeMatePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = TubeMateAccent,
                                height = 3.dp
                            )
                        }
                    ) {
                        // Tab 0: Downloads tray
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { onTabSelected(0) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Downloads",
                                    tint = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.testTag("right_tab_downloads")
                        )

                        // Tab 1: Playlists
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { onTabSelected(1) },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                    contentDescription = "Playlist",
                                    tint = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.testTag("right_tab_playlist")
                        )

                        // Tab 2: Videos
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { onTabSelected(2) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = "Videos",
                                    tint = if (selectedTab == 2) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.testTag("right_tab_videos")
                        )

                        // Tab 3: Music / Audio
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { onTabSelected(3) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Music",
                                    tint = if (selectedTab == 3) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier.testTag("right_tab_audio")
                        )
                    }

                    // 3 dots overflow menu in right drawer
                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Downloads Options",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Resume all") },
                                onClick = {
                                    showOverflowMenu = false
                                    onResumeAll()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pause all") },
                                onClick = {
                                    showOverflowMenu = false
                                    onPauseAll()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add folder") },
                                onClick = { showOverflowMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Exit") },
                                onClick = { showOverflowMenu = false }
                            )
                        }
                    }
                }
            }

            // Search Bar (if activated)
            AnimatedVisibility(visible = isSearchVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFECEFF1))
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search files...", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Content according to tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> DownloadsListTab(
                        downloads = filterDownloads(allDownloads, searchQuery),
                        onPause = onPauseDownload,
                        onResume = onResumeDownload,
                        onDelete = onDeleteDownload,
                        onPlay = onPlayMedia,
                        onOpenApkExport = onOpenApkExport
                    )
                    1 -> PlaylistTab()
                    2 -> VideosListTab(
                        videos = filterDownloads(completedVideos, searchQuery),
                        onPlay = onPlayMedia,
                        onDelete = onDeleteDownload
                    )
                    3 -> AudioListTab(
                        audios = filterDownloads(completedAudios, searchQuery),
                        onPlay = onPlayMedia,
                        onDelete = onDeleteDownload
                    )
                }
            }
        }

        // Floating Cyan Search FAB at bottom right (as in screenshots 01:16 - 01:26)
        FloatingActionButton(
            onClick = onToggleSearch,
            shape = CircleShape,
            containerColor = TubeMateCyan,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(54.dp)
                .testTag("right_drawer_search_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search downloads",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun filterDownloads(list: List<DownloadEntity>, query: String): List<DownloadEntity> {
    if (query.isBlank()) return list
    return list.filter { it.title.contains(query, ignoreCase = true) }
}

@Composable
fun DownloadsListTab(
    downloads: List<DownloadEntity>,
    onPause: (String) -> Unit,
    onResume: (String, Long) -> Unit,
    onDelete: (String) -> Unit,
    onPlay: (DownloadEntity) -> Unit,
    onOpenApkExport: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("downloads_manager_list")
    ) {
        // Prominent APK Card at the top
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable { onOpenApkExport() }
                    .testTag("tubemate_apk_card")
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TubeMate APK (v3.4.23)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Package: com.example • 23.4 MB",
                            fontSize = 11.sp,
                            color = Color(0xFF388E3C)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "GET APK",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))
        }

        if (downloads.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No other active downloads", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(downloads, key = { it.id }) { item ->
                DownloadItemRow(
                    item = item,
                    onPause = { onPause(item.id) },
                    onResume = { onResume(item.id, item.totalBytes) },
                    onDelete = { onDelete(item.id) },
                    onPlay = { onPlay(item) }
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

@Composable
fun DownloadItemRow(
    item: DownloadEntity,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    val isDownloading = item.status == "DOWNLOADING"
    val isCompleted = item.status == "COMPLETED"
    val percent = if (item.totalBytes > 0) {
        ((item.downloadedBytes * 100) / item.totalBytes).toInt().coerceIn(0, 100)
    } else 0
    val totalKb = item.totalBytes / 1024
    val downloadedKb = item.downloadedBytes / 1024

    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isCompleted) onPlay()
            }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail or file preview box with duration badge (as in screenshot 01:16)
        Box(
            modifier = Modifier
                .size(width = 68.dp, height = 48.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF263238)),
            contentAlignment = Alignment.Center
        ) {
            if (item.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = if (item.mediaType == "AUDIO") Icons.Default.MusicNote else Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Duration badge at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(
                    text = item.durationText,
                    color = Color.White,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Center: Title, progress & stats
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(3.dp))

            if (!isCompleted) {
                LinearProgressIndicator(
                    progress = { percent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = TubeMateGreen,
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Green progress text: "56,831 KB (57%)" (as in screenshot 01:16)
                    Text(
                        text = "%,d KB(%d%%)".format(downloadedKb, percent),
                        fontSize = 11.sp,
                        color = TubeMateGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isDownloading) item.speedText else item.status,
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.formatLabel,
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "%,d KB".format(totalKb),
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }

        // Action icon: Play/Pause or options menu
        if (!isCompleted) {
            IconButton(
                onClick = { if (isDownloading) onPause() else onResume() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isDownloading) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isDownloading) "Pause" else "Resume",
                    tint = TubeMatePrimary
                )
            }
        }

        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color(0xFF9E9E9E)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (isCompleted) {
                    DropdownMenuItem(
                        text = { Text("Play") },
                        onClick = {
                            showMenu = false
                            onPlay()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
fun VideosListTab(
    videos: List<DownloadEntity>,
    onPlay: (DownloadEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    if (videos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No videos found", color = Color.Gray, fontSize = 14.sp)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(videos, key = { it.id }) { video ->
            var showMenu by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlay(video) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 68.dp, height = 48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF37474F)),
                    contentAlignment = Alignment.Center
                ) {
                    if (video.thumbnailUrl.isNotBlank()) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = video.durationText,
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "%,d KB".format(video.totalBytes / 1024),
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF9E9E9E)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Play") },
                            onClick = {
                                showMenu = false
                                onPlay(video)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete(video.id)
                            }
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))
        }
    }
}

@Composable
fun AudioListTab(
    audios: List<DownloadEntity>,
    onPlay: (DownloadEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    if (audios.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No audio files", color = Color.Gray, fontSize = 14.sp)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(audios, key = { it.id }) { audio ->
            var showMenu by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlay(audio) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio note item box (as in screenshot 01:22)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFCFD8DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = TubeMatePrimary,
                        modifier = Modifier.size(24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = audio.durationText,
                            color = Color.White,
                            fontSize = 8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audio.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "<unknown> | %,d KB".format(audio.totalBytes / 1024),
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF9E9E9E)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Play") },
                            onClick = {
                                showMenu = false
                                onPlay(audio)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete(audio.id)
                            }
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))
        }
    }
}

@Composable
fun PlaylistTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current playlist is empty", color = Color.Gray, fontSize = 14.sp)
        }

        // Floating add button in playlist
        FloatingActionButton(
            onClick = { /* Add playlist */ },
            shape = CircleShape,
            containerColor = TubeMateCyan,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Playlist")
        }
    }
}
