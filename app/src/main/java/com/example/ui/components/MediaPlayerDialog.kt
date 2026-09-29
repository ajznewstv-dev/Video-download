package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.data.DownloadEntity
import com.example.ui.theme.TubeMateAccent
import com.example.ui.theme.TubeMateTeal
import kotlinx.coroutines.delay

@Composable
fun MediaPlayerDialog(
    media: DownloadEntity,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf(1000) }
    var isVideoReady by remember { mutableStateOf(false) }

    val videoViewRef = remember { mutableStateOf<VideoView?>(null) }
    val mediaPlayerRef = remember { mutableStateOf<MediaPlayer?>(null) }

    val isAudio = media.mediaType == "AUDIO"
    val playableUrl = remember(media) {
        if (media.streamUrl.isNotBlank()) {
            media.streamUrl
        } else if (isAudio) {
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        } else {
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        }
    }

    // Cleanup when dismissed
    DisposableEffect(Unit) {
        onDispose {
            try {
                videoViewRef.value?.stopPlayback()
                mediaPlayerRef.value?.stop()
                mediaPlayerRef.value?.release()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    // Sync position timer
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(500)
            if (isAudio) {
                mediaPlayerRef.value?.let { mp ->
                    if (mp.isPlaying) {
                        currentPositionMs = mp.currentPosition
                        durationMs = mp.duration.coerceAtLeast(1000)
                    }
                }
            } else {
                videoViewRef.value?.let { vv ->
                    if (vv.isPlaying) {
                        currentPositionMs = vv.currentPosition
                        durationMs = vv.duration.coerceAtLeast(1000)
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141E24)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("media_player_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = media.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${media.mediaType} • ${media.formatLabel}",
                            color = Color(0xFF90A4AE),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close player",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Media Playback Area: VideoView for video, Animated visualizer for audio
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (isAudio) {
                        // Native Audio Player via MediaPlayer
                        DisposableEffect(playableUrl) {
                            val mp = MediaPlayer().apply {
                                setDataSource(context, Uri.parse(playableUrl))
                                setVolume(1.0f, 1.0f)
                                setOnPreparedListener {
                                    isVideoReady = true
                                    start()
                                    durationMs = duration
                                    this@apply.isLooping = true
                                }
                                prepareAsync()
                            }
                            mediaPlayerRef.value = mp
                            onDispose {
                                mp.stop()
                                mp.release()
                            }
                        }

                        // Audio Visualizer
                        AudioVisualizerScreen(isPlaying = isPlaying, title = media.title)
                    } else {
                        // Native Video Player via VideoView
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoURI(Uri.parse(playableUrl))
                                    setOnPreparedListener { mp ->
                                        isVideoReady = true
                                        mp.isLooping = true
                                        mp.setVolume(1.0f, 1.0f)
                                        start()
                                        durationMs = duration.coerceAtLeast(1000)
                                    }
                                    setOnCompletionListener {
                                        isPlaying = false
                                    }
                                    setOnErrorListener { _, _, _ ->
                                        true
                                    }
                                    videoViewRef.value = this
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (!isVideoReady) {
                            CircularProgressIndicator(
                                color = TubeMateAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Seekbar Slider
                val progressFloat = (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                Slider(
                    value = progressFloat,
                    onValueChange = { newProgress ->
                        val targetMs = (newProgress * durationMs).toInt()
                        currentPositionMs = targetMs
                        if (isAudio) {
                            mediaPlayerRef.value?.seekTo(targetMs)
                        } else {
                            videoViewRef.value?.seekTo(targetMs)
                        }
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = TubeMateAccent,
                        activeTrackColor = TubeMateAccent,
                        inactiveTrackColor = Color(0xFF455A64)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time labels: 00:00 / 05:15
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatMillis(currentPositionMs),
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatMillis(durationMs),
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Playback Control Buttons (Replay 10s, Play/Pause, Forward 10s)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // -10s
                    IconButton(
                        onClick = {
                            val newPos = (currentPositionMs - 10000).coerceAtLeast(0)
                            currentPositionMs = newPos
                            if (isAudio) {
                                mediaPlayerRef.value?.seekTo(newPos)
                            } else {
                                videoViewRef.value?.seekTo(newPos)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Replay 10s",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Play / Pause Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(TubeMateAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    if (isAudio) mediaPlayerRef.value?.pause() else videoViewRef.value?.pause()
                                    isPlaying = false
                                } else {
                                    if (isAudio) mediaPlayerRef.value?.start() else videoViewRef.value?.start()
                                    isPlaying = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // +10s
                    IconButton(
                        onClick = {
                            val newPos = (currentPositionMs + 10000).coerceAtMost(durationMs)
                            currentPositionMs = newPos
                            if (isAudio) {
                                mediaPlayerRef.value?.seekTo(newPos)
                            } else {
                                videoViewRef.value?.seekTo(newPos)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioVisualizerScreen(isPlaying: Boolean, title: String) {
    val transition = rememberInfiniteTransition(label = "audio_bars")
    val bar1 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.4f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse), label = "b3"
    )
    val bar4 by transition.animateFloat(
        initialValue = 0.9f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(380), RepeatMode.Reverse), label = "b4"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(TubeMateTeal.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Audio track",
                tint = TubeMateTeal,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Audio Equalizer bars
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.height(30.dp)
        ) {
            listOf(bar1, bar2, bar3, bar4, bar2, bar1).forEach { scale ->
                val h = if (isPlaying) (30 * scale).dp else 6.dp
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(h)
                        .clip(RoundedCornerShape(3.dp))
                        .background(TubeMateAccent)
                )
            }
        }
    }
}

private fun formatMillis(ms: Int): String {
    val totalSeconds = ms / 1000
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}
