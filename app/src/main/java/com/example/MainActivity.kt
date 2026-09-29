package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.BrowserView
import com.example.ui.components.FormatSelectionDialog
import com.example.ui.components.LeftDrawerContent
import com.example.ui.components.MediaPlayerDialog
import com.example.ui.components.ParsingVideoDialog
import com.example.ui.components.RightDrawerContent
import com.example.ui.components.SettingsScreen
import com.example.ui.components.SplashScreen
import com.example.ui.components.TubeMateBottomBar
import com.example.ui.components.TubeMateTopBar
import com.example.ui.components.VideoDownloadFab
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TubeMateApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TubeMateApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
        return
    }

    // Observe ViewModel State
    val currentUrl by viewModel.currentUrl.collectAsStateWithLifecycle()
    val inputUrl by viewModel.inputUrl.collectAsStateWithLifecycle()
    val canGoBack by viewModel.canGoBack.collectAsStateWithLifecycle()
    val canGoForward by viewModel.canGoForward.collectAsStateWithLifecycle()
    val isDesktopMode by viewModel.isDesktopMode.collectAsStateWithLifecycle()
    val isBlockAutoplay by viewModel.isBlockAutoplay.collectAsStateWithLifecycle()

    val ytSearchQuery by viewModel.ytSearchQuery.collectAsStateWithLifecycle()
    val ytSearchResults by viewModel.ytSearchResults.collectAsStateWithLifecycle()
    val currentlyPlayingVideo by viewModel.currentlyPlayingVideo.collectAsStateWithLifecycle()

    val isLeftDrawerOpen by viewModel.isLeftDrawerOpen.collectAsStateWithLifecycle()
    val leftDrawerTab by viewModel.leftDrawerTab.collectAsStateWithLifecycle()
    val isRightDrawerOpen by viewModel.isRightDrawerOpen.collectAsStateWithLifecycle()
    val rightDrawerTab by viewModel.rightDrawerTab.collectAsStateWithLifecycle()

    val detectedVideo by viewModel.detectedVideo.collectAsStateWithLifecycle()
    val isParsing by viewModel.isParsing.collectAsStateWithLifecycle()
    val parsingProgress by viewModel.parsingProgress.collectAsStateWithLifecycle()
    val showFormatDialog by viewModel.showFormatDialog.collectAsStateWithLifecycle()
    val availableFormats by viewModel.availableFormats.collectAsStateWithLifecycle()
    val selectedFormat by viewModel.selectedFormat.collectAsStateWithLifecycle()
    val enqueueLater by viewModel.enqueueLater.collectAsStateWithLifecycle()

    val allDownloads by viewModel.allDownloads.collectAsStateWithLifecycle()
    val activeDownloads by viewModel.activeDownloads.collectAsStateWithLifecycle()
    val completedVideos by viewModel.completedVideos.collectAsStateWithLifecycle()
    val completedAudios by viewModel.completedAudios.collectAsStateWithLifecycle()
    val historyItems by viewModel.historyItems.collectAsStateWithLifecycle()

    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val activePlayerMedia by viewModel.activePlayerMedia.collectAsStateWithLifecycle()
    val isSearchVisible by viewModel.isSearchVisible.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Handle Android system back button cleanly
    BackHandler(
        enabled = activePlayerMedia != null ||
                showFormatDialog ||
                isParsing ||
                currentlyPlayingVideo != null ||
                isLeftDrawerOpen ||
                isRightDrawerOpen ||
                isSettingsOpen ||
                canGoBack
    ) {
        when {
            activePlayerMedia != null -> viewModel.closePlayer()
            showFormatDialog -> viewModel.closeFormatDialog()
            isParsing -> viewModel.cancelParsing()
            currentlyPlayingVideo != null -> viewModel.closeActiveVideo()
            isLeftDrawerOpen -> viewModel.closeLeftDrawer()
            isRightDrawerOpen -> viewModel.closeRightDrawer()
            isSettingsOpen -> viewModel.closeSettings()
            canGoBack -> { /* Go back */ }
        }
    }

    if (isSettingsOpen) {
        SettingsScreen(onBack = { viewModel.closeSettings() })
        return
    }

    Box(modifier = Modifier.fillMaxSize().testTag("tubemate_main_container")) {
        Scaffold(
            topBar = {
                TubeMateTopBar(
                    urlInput = inputUrl,
                    onUrlChange = { viewModel.setInputUrl(it) },
                    onSubmitUrl = { viewModel.navigateToUrl(it) },
                    onOpenLeftDrawer = { viewModel.openLeftDrawer() },
                    onOpenPlaylist = { viewModel.openRightDrawer(1) },
                    onOpenSettings = { viewModel.openSettings() },
                    isDesktopMode = isDesktopMode,
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                    isBlockAutoplay = isBlockAutoplay,
                    onToggleBlockAutoplay = { viewModel.toggleBlockAutoplay() },
                    onClearCache = {
                        Toast.makeText(context, "Local cache cleared", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            bottomBar = {
                TubeMateBottomBar(
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    activeDownloadsCount = activeDownloads.size,
                    onGoBack = { /* Web back */ },
                    onGoForward = { /* Web forward */ },
                    onOpenDownloads = { viewModel.openRightDrawer(0) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // In-App Browser & Video Feed View
                BrowserView(
                    url = currentUrl,
                    isDesktopMode = isDesktopMode,
                    isBlockAutoplay = isBlockAutoplay,
                    ytSearchQuery = ytSearchQuery,
                    ytSearchResults = ytSearchResults,
                    currentlyPlayingVideo = currentlyPlayingVideo,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSelectVideo = { video ->
                        viewModel.playSelectedVideo(video)
                        Toast.makeText(context, "Playing: ${video.title.take(28)}...", Toast.LENGTH_SHORT).show()
                    },
                    onCloseActiveVideo = { viewModel.closeActiveVideo() },
                    onPageStarted = { viewModel.onPageStarted(it) },
                    onPageFinished = { url, title -> viewModel.onPageFinished(url, title) },
                    onNavStateChanged = { canBack, canForward -> viewModel.updateNavState(canBack, canForward) },
                    modifier = Modifier.fillMaxSize()
                )

                // The iconic Pink Download FAB
                VideoDownloadFab(
                    visible = detectedVideo != null && !isLeftDrawerOpen && !isRightDrawerOpen,
                    onClick = { viewModel.startParsingVideo() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                )
            }
        }

        // Left Drawer Overlay (Bookmarks & History)
        if (isLeftDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.closeLeftDrawer() }
            )
        }

        AnimatedVisibility(
            visible = isLeftDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            LeftDrawerContent(
                selectedTab = leftDrawerTab,
                onTabSelected = { viewModel.setLeftDrawerTab(it) },
                bookmarks = viewModel.bookmarks,
                history = historyItems,
                onSelectSite = { siteUrl ->
                    viewModel.closeLeftDrawer()
                    viewModel.navigateToUrl(siteUrl)
                },
                onDeleteHistory = { viewModel.deleteHistoryItem(it) },
                onClearAllHistory = { viewModel.clearAllHistory() }
            )
        }

        // Right Drawer Overlay (Downloads, Playlists, Videos, Music)
        if (isRightDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.closeRightDrawer() }
            )
        }

        AnimatedVisibility(
            visible = isRightDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            RightDrawerContent(
                selectedTab = rightDrawerTab,
                onTabSelected = { viewModel.setRightDrawerTab(it) },
                allDownloads = allDownloads,
                completedVideos = completedVideos,
                completedAudios = completedAudios,
                searchQuery = searchQuery,
                isSearchVisible = isSearchVisible,
                onToggleSearch = { viewModel.toggleSearch() },
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onPauseDownload = { viewModel.pauseDownload(it) },
                onResumeDownload = { id, total -> viewModel.resumeDownload(id, total) },
                onDeleteDownload = { viewModel.deleteDownload(it) },
                onPlayMedia = { viewModel.playMedia(it) },
                onPauseAll = { viewModel.pauseAll() },
                onResumeAll = { viewModel.resumeAll() }
            )
        }

        // Parsing Video Dialog (matching screenshot 00:54)
        if (isParsing) {
            ParsingVideoDialog(
                progress = parsingProgress,
                onCancel = { viewModel.cancelParsing() }
            )
        }

        // Format Selection Dialog (matching screenshot 00:55 - 00:58)
        if (showFormatDialog && detectedVideo != null) {
            FormatSelectionDialog(
                videoInfo = detectedVideo!!,
                formats = availableFormats,
                selectedFormat = selectedFormat,
                enqueueLater = enqueueLater,
                onSelectFormat = { viewModel.selectFormat(it) },
                onToggleEnqueue = { viewModel.toggleEnqueueLater() },
                onConfirmDownload = {
                    viewModel.confirmDownload()
                    Toast.makeText(context, "Download started: ${selectedFormat?.label}", Toast.LENGTH_SHORT).show()
                },
                onClose = { viewModel.closeFormatDialog() },
                onPlayPreview = {
                    Toast.makeText(context, "Playing preview...", Toast.LENGTH_SHORT).show()
                },
                onShare = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, detectedVideo!!.url)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Video URL"))
                }
            )
        }

        // In-App Media Player Dialog
        activePlayerMedia?.let { media ->
            MediaPlayerDialog(
                media = media,
                onClose = { viewModel.closePlayer() }
            )
        }
    }
}
