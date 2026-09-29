package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.PlayableVideoInfo
import com.example.ui.theme.TubeMateAccent
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserView(
    url: String,
    isDesktopMode: Boolean,
    isBlockAutoplay: Boolean,
    ytSearchQuery: String,
    ytSearchResults: List<PlayableVideoInfo>,
    currentlyPlayingVideo: PlayableVideoInfo?,
    onSearchQueryChange: (String) -> Unit,
    onSelectVideo: (PlayableVideoInfo) -> Unit,
    onCloseActiveVideo: () -> Unit,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?) -> Unit,
    onNavStateChanged: (Boolean, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isWebViewMode by remember { mutableStateOf(false) }
    var isSearchingActive by remember { mutableStateOf(false) }
    var isIncognitoMode by remember { mutableStateOf(true) }

    LaunchedEffect(url) {
        isWebViewMode = !url.contains("youtube.com") && !url.contains("m.youtube.com")
    }

    LaunchedEffect(ytSearchQuery) {
        if (ytSearchQuery.isNotBlank()) {
            isSearchingActive = true
        }
    }

    val searchSuggestions = remember {
        listOf(
            "Sadabahar Gaane",
            "Chunnari Chunnari",
            "Hindi Songs",
            "Bangla Folk",
            "Arijit Singh",
            "Bollywood Hits",
            "Shorts"
        )
    }

    Box(modifier = modifier.fillMaxSize().testTag("browser_view_container")) {
        if (isWebViewMode) {
            // General Web view for other websites
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = isBlockAutoplay
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            userAgentString = if (isDesktopMode) {
                                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                            } else {
                                WebSettings.getDefaultUserAgent(context)
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                url?.let { onPageStarted(it) }
                                onNavStateChanged(canGoBack(), canGoForward())
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                url?.let { onPageFinished(it, view?.title) }
                                onNavStateChanged(canGoBack(), canGoForward())
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        webChromeClient = WebChromeClient()
                        loadUrl(url)
                    }
                },
                update = { webView ->
                    if (webView.url != url) {
                        webView.loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // YouTube Interface
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // If a video is playing, show the fully functional player with audio & video!
                currentlyPlayingVideo?.let { activeVideo ->
                    ActivePlayableVideoHeader(
                        video = activeVideo,
                        onClose = onCloseActiveVideo
                    )
                }

                // YouTube Top Bar (matching Screenshot_20260929-230754)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Red YouTube Logo + "YouTube" text
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            isSearchingActive = false
                            onSearchQueryChange("")
                            onCloseActiveVideo()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 32.dp, height = 22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF0000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YouTube",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF212121),
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Right: Notifications Bell & Search Icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { /* Notifications */ },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Notifications",
                                tint = Color(0xFF212121),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = { isSearchingActive = true },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF212121),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // If currently searching or search query is active, show the search interface & results
                if (isSearchingActive || ytSearchQuery.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        // Search Input Bar
                        OutlinedTextField(
                            value = ytSearchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search YouTube...", fontSize = 14.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (ytSearchQuery.isNotBlank()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TubeMateAccent,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = Color(0xFFF2F2F2),
                                unfocusedContainerColor = Color(0xFFF2F2F2)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("yt_search_active_input"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { /* search submitted */ })
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search suggestions pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            searchSuggestions.forEach { suggestion ->
                                val isSelected = ytSearchQuery.equals(suggestion, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) Color(0xFF0F0F0F) else Color(0xFFF0F0F0))
                                        .clickable { onSearchQueryChange(suggestion) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = suggestion,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else Color(0xFF212121),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Video Search Results List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFF9F9F9))
                            .testTag("youtube_search_results_list")
                    ) {
                        items(ytSearchResults, key = { it.id }) { video ->
                            PlayableVideoCard(
                                video = video,
                                isCurrentlyPlaying = currentlyPlayingVideo?.id == video.id,
                                onPlayClick = {
                                    onSelectVideo(video)
                                }
                            )
                        }
                    }
                } else {
                    // EXACT YouTube Incognito Screen matching Screenshot_20260929-230754!
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(28.dp))

                            // Big Red YouTube Play Button Icon
                            Box(
                                modifier = Modifier
                                    .size(width = 110.dp, height = 74.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Color(0xFFFF0000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(26.dp))

                            // Search bar row (Explore icon + "Search YouTube" pill + Mic icon)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Explore Compass Button
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF2F2F2))
                                        .clickable { isSearchingActive = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = "Explore",
                                        tint = Color(0xFF212121),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Search YouTube pill
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(Color(0xFFF2F2F2))
                                        .clickable { isSearchingActive = true }
                                        .padding(horizontal = 18.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "Search YouTube",
                                        color = Color(0xFF757575),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Mic Button
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF2F2F2))
                                        .clickable { isSearchingActive = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Search",
                                        tint = Color(0xFF212121),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // "You're incognito" Card
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("incognito_info_card")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Incognito Spy Hat & Glasses Icon
                                    IncognitoSpyIcon(
                                        modifier = Modifier.size(68.dp),
                                        tint = Color(0xFF212121)
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Text(
                                        text = "You're incognito",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111111)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Your activity in this session will not be linked with your account. Learn more",
                                        fontSize = 14.sp,
                                        color = Color(0xFF606060),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // "Turn off Incognito" pill button
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .clip(RoundedCornerShape(23.dp))
                                            .background(Color(0xFFF2F2F2))
                                            .clickable {
                                                isIncognitoMode = !isIncognitoMode
                                                isSearchingActive = true
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Turn off Incognito",
                                            color = Color(0xFF0F0F0F),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Navigation Bar with Home, Shorts, Subscriptions, You
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                YouTubeNavTab(
                                    icon = Icons.Default.Home,
                                    label = "Home",
                                    isSelected = true,
                                    onClick = {
                                        isSearchingActive = false
                                        onSearchQueryChange("")
                                    }
                                )

                                YouTubeNavTab(
                                    icon = Icons.Default.PlayCircleOutline,
                                    label = "Shorts",
                                    isSelected = false,
                                    onClick = {
                                        onSearchQueryChange("Shorts")
                                    }
                                )

                                YouTubeNavTab(
                                    icon = Icons.Default.Subscriptions,
                                    label = "Subscriptions",
                                    isSelected = false,
                                    onClick = { /* Subscriptions */ }
                                )

                                // You (Incognito Spy) Tab
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { isSearchingActive = false }
                                ) {
                                    IncognitoSpyIcon(
                                        modifier = Modifier.size(24.dp),
                                        tint = Color(0xFF212121)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "You",
                                        fontSize = 10.sp,
                                        color = Color(0xFF212121),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Bottom banner: "You're incognito"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF141414))
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "You're incognito",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YouTubeNavTab(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF0F0F0F) else Color(0xFF606060),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) Color(0xFF0F0F0F) else Color(0xFF606060),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ActivePlayableVideoHeader(
    video: PlayableVideoInfo,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isReady by remember { mutableStateOf(false) }
    var currentMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf(315000) } // ~05:15

    val videoViewRef = remember { mutableStateOf<VideoView?>(null) }
    val audioPlayerRef = remember { mutableStateOf<MediaPlayer?>(null) }

    // Start background audio player to guarantee sound even if container video sink has delay
    DisposableEffect(video.id) {
        val audioUrl = if (video.streamUrl.contains(".mp3")) {
            video.streamUrl
        } else {
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        }
        val mp = MediaPlayer().apply {
            try {
                setDataSource(context, Uri.parse(audioUrl))
                setVolume(1.0f, 1.0f)
                isLooping = true
                setOnPreparedListener {
                    start()
                }
                prepareAsync()
            } catch (e: Exception) {
                // ignore
            }
        }
        audioPlayerRef.value = mp

        onDispose {
            try {
                videoViewRef.value?.stopPlayback()
                mp.stop()
                mp.release()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(500)
            videoViewRef.value?.let { vv ->
                if (vv.isPlaying) {
                    currentMs = vv.currentPosition
                    durationMs = vv.duration.coerceAtLeast(1000)
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_video_player_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Video Playback Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Real Video View
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoURI(Uri.parse(video.streamUrl))
                            setOnPreparedListener { mp ->
                                isReady = true
                                mp.isLooping = true
                                mp.setVolume(1.0f, 1.0f)
                                start()
                                durationMs = duration.coerceAtLeast(1000)
                            }
                            setOnCompletionListener {
                                isPlaying = false
                            }
                            setOnErrorListener { _, _, _ ->
                                isReady = true
                                true
                            }
                            videoViewRef.value = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (!isReady) {
                    CircularProgressIndicator(
                        color = TubeMateAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Close Button Top-Right
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close player",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Bottom Video Controls Bar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                val vv = videoViewRef.value ?: return@IconButton
                                vv.seekTo((vv.currentPosition - 10000).coerceAtLeast(0))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "-10s",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val vv = videoViewRef.value
                                val ap = audioPlayerRef.value
                                if (isPlaying) {
                                    vv?.pause()
                                    ap?.pause()
                                    isPlaying = false
                                } else {
                                    vv?.start()
                                    ap?.start()
                                    isPlaying = true
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val vv = videoViewRef.value ?: return@IconButton
                                vv.seekTo((vv.currentPosition + 10000).coerceAtMost(vv.duration))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "+10s",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Sound on",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Playing with sound",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Video Meta details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                Text(
                    text = video.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.channel} • ${video.views} • ${video.time}",
                    fontSize = 12.sp,
                    color = Color(0xFF606060)
                )
            }
        }
    }
}

@Composable
fun PlayableVideoCard(
    video: PlayableVideoInfo,
    isCurrentlyPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() }
            .background(if (isCurrentlyPlaying) Color(0xFFFCE4EC) else Color.White)
            .padding(bottom = 12.dp)
            .testTag("playable_video_card_${video.id}")
    ) {
        // Thumbnail with duration chip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF212121))
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Centered Play button
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Duration badge at bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = video.duration,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Details row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(TubeMateAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.channel.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F0F0F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${video.channel} • ${video.views} • ${video.time}",
                    fontSize = 12.sp,
                    color = Color(0xFF606060)
                )
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = Color(0xFF757575),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
