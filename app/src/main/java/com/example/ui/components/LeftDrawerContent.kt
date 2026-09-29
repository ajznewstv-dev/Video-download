package com.example.ui.components

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HistoryEntity
import com.example.model.SiteBookmark
import com.example.ui.theme.TubeMateAccent
import com.example.ui.theme.TubeMatePrimary

@Composable
fun LeftDrawerContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    bookmarks: List<SiteBookmark>,
    history: List<HistoryEntity>,
    onSelectSite: (String) -> Unit,
    onDeleteHistory: (Long) -> Unit,
    onClearAllHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(Color.White)
            .testTag("left_drawer_content")
    ) {
        // Tab Header: Clock icon (History) vs Bookmark Ribbon icon
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = TubeMatePrimary,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = TubeMateAccent,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.testTag("left_drawer_tab_history")
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Bookmarks",
                        tint = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.testTag("left_drawer_tab_bookmarks")
            )
        }

        // Tab Content
        if (selectedTab == 0) {
            // History Tab
            Column(modifier = Modifier.fillMaxSize()) {
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No browsing history",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("history_list")
                    ) {
                        items(history, key = { it.id }) { item ->
                            HistoryRowItem(
                                item = item,
                                onClick = { onSelectSite(item.url) },
                                onDelete = { onDeleteHistory(item.id) }
                            )
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }

                    // Bottom "DELETE ALL" Bar (as in screenshot 00:13)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        TextButton(
                            onClick = onClearAllHistory,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF424242)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delete_all_history_button")
                        ) {
                            Text(
                                text = "DELETE ALL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Bookmarks / Supported Sites Tab (as in screenshot 00:12)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("bookmarks_list")
            ) {
                items(bookmarks) { site ->
                    SiteBookmarkRowItem(
                        site = site,
                        onClick = { onSelectSite(site.url) }
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}

@Composable
fun HistoryRowItem(
    item: HistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.timeFormatted,
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.width(62.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.url,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SiteBookmarkRowItem(
    site: SiteBookmark,
    onClick: () -> Unit
) {
    val iconColor = when (site.iconKey) {
        "youtube" -> Color(0xFFFF0000)
        "facebook" -> Color(0xFF1877F2)
        "instagram" -> Color(0xFFE4405F)
        "twitter" -> Color(0xFF1DA1F2)
        "threads" -> Color(0xFF000000)
        "dailymotion" -> Color(0xFF0066DC)
        "youku" -> Color(0xFF0093E9)
        "vimeo" -> Color(0xFF1AB7EA)
        "google" -> Color(0xFF4285F4)
        "naver" -> Color(0xFF03C75A)
        "kakao" -> Color(0xFFFFE812)
        "mango" -> Color(0xFFFF5722)
        "soundcloud" -> Color(0xFFFF5500)
        else -> TubeMatePrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon badge
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (site.iconKey == "youtube") Icons.Default.VideoLibrary else Icons.Default.Public,
                contentDescription = site.name,
                tint = if (site.iconKey == "kakao") Color.Black else Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = site.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
            Text(
                text = site.url,
                fontSize = 12.sp,
                color = Color(0xFF757575),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = { /* site bookmark actions */ },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Options",
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
