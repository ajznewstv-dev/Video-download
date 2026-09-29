package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.VideoFormatOption
import com.example.ui.DetectedVideoInfo
import com.example.ui.theme.TubeMateAccent

@Composable
fun FormatSelectionDialog(
    videoInfo: DetectedVideoInfo,
    formats: List<VideoFormatOption>,
    selectedFormat: VideoFormatOption?,
    enqueueLater: Boolean,
    onSelectFormat: (VideoFormatOption) -> Unit,
    onToggleEnqueue: () -> Unit,
    onConfirmDownload: () -> Unit,
    onClose: () -> Unit,
    onPlayPreview: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("format_selection_dialog")
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left main content: video summary + formats list + enqueue checkbox
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = 12.dp, top = 12.dp, end = 6.dp, bottom = 12.dp)
                    ) {
                        // Video Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (videoInfo.thumbnailUrl.isNotBlank()) {
                                AsyncImage(
                                    model = videoInfo.thumbnailUrl,
                                    contentDescription = "Video Thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(width = 54.dp, height = 40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.DarkGray)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(width = 54.dp, height = 40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF37474F)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = videoInfo.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = videoInfo.url,
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E88E5),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))

                        // Formats list
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("format_options_list")
                        ) {
                            items(formats) { format ->
                                val isSelected = format.id == selectedFormat?.id
                                val bg = if (isSelected) Color(0xFFE91E63) else Color.Transparent
                                val textColor = if (isSelected) Color.White else Color(0xFF333333)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bg, RoundedCornerShape(4.dp))
                                        .clickable { onSelectFormat(format) }
                                        .padding(horizontal = 8.dp, vertical = 7.dp)
                                        .testTag("format_item_${format.id}"),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = format.label,
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )

                                    Text(
                                        text = format.sizeText,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF757575),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFEEEEEE))

                        // Enqueue to download later checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleEnqueue() }
                                .padding(top = 4.dp)
                        ) {
                            Checkbox(
                                checked = enqueueLater,
                                onCheckedChange = { onToggleEnqueue() },
                                colors = CheckboxDefaults.colors(checkedColor = TubeMateAccent)
                            )
                            Text(
                                text = "Enqueue to download later",
                                fontSize = 13.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }

                    // Right action buttons column (as in screenshots 00:55 - 00:58)
                    Column(
                        modifier = Modifier
                            .width(64.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFF9F9F9))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Action buttons top
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Share
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFB74D))
                                    .clickable { onShare() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Reaction
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD54F))
                                    .clickable { /* Reaction */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SentimentSatisfiedAlt,
                                    contentDescription = "React",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Play Preview
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFCA28))
                                    .clickable { onPlayPreview() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Preview Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Action buttons bottom: Download FAB + Close
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Download FAB
                            FloatingActionButton(
                                onClick = onConfirmDownload,
                                shape = CircleShape,
                                containerColor = TubeMateAccent,
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("confirm_download_fab")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Start Download",
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Close button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFB0BEC5))
                                    .clickable { onClose() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Format Picker",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
