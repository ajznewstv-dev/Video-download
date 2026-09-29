package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DownloadEntity
import com.example.data.HistoryEntity
import com.example.data.MediaRepository
import com.example.model.SiteBookmark
import com.example.model.VideoFormatOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetectedVideoInfo(
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val videoId: String = "",
    val streamUrl: String = ""
)

data class PlayableVideoInfo(
    val id: String,
    val title: String,
    val channel: String,
    val views: String,
    val time: String,
    val duration: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val streamUrl: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = MediaRepository(database.downloadDao(), database.historyDao(), viewModelScope)

    val allDownloads: StateFlow<List<DownloadEntity>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedVideos: StateFlow<List<DownloadEntity>> = repository.completedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedAudios: StateFlow<List<DownloadEntity>> = repository.completedAudios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDownloads: StateFlow<List<DownloadEntity>> = repository.activeDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyItems: StateFlow<List<HistoryEntity>> = repository.historyItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: List<SiteBookmark> = repository.defaultBookmarks

    // Browser navigation state
    private val _currentUrl = MutableStateFlow("https://m.youtube.com")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _inputUrl = MutableStateFlow("https://m.youtube.com")
    val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

    private val _webTitle = MutableStateFlow("YouTube")
    val webTitle: StateFlow<String> = _webTitle.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _isDesktopMode = MutableStateFlow(false)
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    private val _isBlockAutoplay = MutableStateFlow(false)
    val isBlockAutoplay: StateFlow<Boolean> = _isBlockAutoplay.asStateFlow()

    // YouTube Search & Video State
    private val _ytSearchQuery = MutableStateFlow("")
    val ytSearchQuery: StateFlow<String> = _ytSearchQuery.asStateFlow()

    private val _ytSearchResults = MutableStateFlow<List<PlayableVideoInfo>>(emptyList())
    val ytSearchResults: StateFlow<List<PlayableVideoInfo>> = _ytSearchResults.asStateFlow()

    private val _currentlyPlayingVideo = MutableStateFlow<PlayableVideoInfo?>(null)
    val currentlyPlayingVideo: StateFlow<PlayableVideoInfo?> = _currentlyPlayingVideo.asStateFlow()

    // Drawers & Tabs
    private val _isLeftDrawerOpen = MutableStateFlow(false)
    val isLeftDrawerOpen: StateFlow<Boolean> = _isLeftDrawerOpen.asStateFlow()

    private val _leftDrawerTab = MutableStateFlow(1) // 0 = History, 1 = Bookmarks
    val leftDrawerTab: StateFlow<Int> = _leftDrawerTab.asStateFlow()

    private val _isRightDrawerOpen = MutableStateFlow(false)
    val isRightDrawerOpen: StateFlow<Boolean> = _isRightDrawerOpen.asStateFlow()

    private val _rightDrawerTab = MutableStateFlow(0) // 0 = Downloads, 1 = Playlist, 2 = Videos, 3 = Music
    val rightDrawerTab: StateFlow<Int> = _rightDrawerTab.asStateFlow()

    // Detected video on the current page (starts empty when app opens with clean search)
    private val _detectedVideo = MutableStateFlow<DetectedVideoInfo?>(null)
    val detectedVideo: StateFlow<DetectedVideoInfo?> = _detectedVideo.asStateFlow()

    // Parsing video dialog state
    private val _isParsing = MutableStateFlow(false)
    val isParsing: StateFlow<Boolean> = _isParsing.asStateFlow()

    private val _parsingProgress = MutableStateFlow(0)
    val parsingProgress: StateFlow<Int> = _parsingProgress.asStateFlow()

    private var parsingJob: Job? = null

    // Format selection dialog
    private val _showFormatDialog = MutableStateFlow(false)
    val showFormatDialog: StateFlow<Boolean> = _showFormatDialog.asStateFlow()

    private val _availableFormats = MutableStateFlow<List<VideoFormatOption>>(emptyList())
    val availableFormats: StateFlow<List<VideoFormatOption>> = _availableFormats.asStateFlow()

    private val _selectedFormat = MutableStateFlow<VideoFormatOption?>(null)
    val selectedFormat: StateFlow<VideoFormatOption?> = _selectedFormat.asStateFlow()

    private val _enqueueLater = MutableStateFlow(false)
    val enqueueLater: StateFlow<Boolean> = _enqueueLater.asStateFlow()

    // Settings screen
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Active media player for downloaded items
    private val _activePlayerMedia = MutableStateFlow<DownloadEntity?>(null)
    val activePlayerMedia: StateFlow<DownloadEntity?> = _activePlayerMedia.asStateFlow()

    // Search query in right drawer
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible.asStateFlow()

    private val allKnownVideos = listOf(
        PlayableVideoInfo(
            id = "vid_1",
            title = "Chunnari Chunnari | Biwi No.1 | Salman Khan | Sushmita Sen | Abhijeet, Anuradha",
            channel = "Bollywood Music Adda",
            views = "69K views",
            time = "10 days ago",
            duration = "05:15",
            thumbnailUrl = "https://img.youtube.com/vi/7SLGxEDyqWo/hqdefault.jpg",
            videoUrl = "https://m.youtube.com/watch?v=7SLGxEDyqWo",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
        PlayableVideoInfo(
            id = "vid_2",
            title = "Tu Hi Haqeeqat Lo-fi [slow reverb] | Emraan Hashmi, Soha Ali Khan | Pritam",
            channel = "SLOWVERSE",
            views = "450K views",
            time = "3 weeks ago",
            duration = "05:56",
            thumbnailUrl = "https://picsum.photos/seed/tuhihaqeeqat/640/360",
            videoUrl = "https://m.youtube.com/watch?v=tu_hi_haqeeqat",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ),
        PlayableVideoInfo(
            id = "vid_3",
            title = "90's Old Hindi Songs 💖 Sadabahar Gaane | Udit Narayan, Alka Yagnik, Kumar Sanu",
            channel = "Tech Talk House",
            views = "66M views",
            time = "1 year ago",
            duration = "1:37:28",
            thumbnailUrl = "https://picsum.photos/seed/sadabahar/640/360",
            videoUrl = "https://m.youtube.com/watch?v=sadabahar_gaane",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        ),
        PlayableVideoInfo(
            id = "vid_4",
            title = "Best of Arijit Singh Romantic Songs | Heart Touching Hindi Songs 2026",
            channel = "Ronak Bhatt",
            views = "33M views",
            time = "Streamed 2 years ago",
            duration = "1:19:58",
            thumbnailUrl = "https://picsum.photos/seed/arijit/640/360",
            videoUrl = "https://m.youtube.com/watch?v=arijit_singh_songs",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
        PlayableVideoInfo(
            id = "vid_5",
            title = "সেরা বাংলা ফোক গান | Best of Bangla Folk Songs | Bengali Folk Songs 2026",
            channel = "Sohail Mia",
            views = "8.2M views",
            time = "1 year ago",
            duration = "46:22",
            thumbnailUrl = "https://picsum.photos/seed/banglafolk/640/360",
            videoUrl = "https://m.youtube.com/watch?v=bangla_folk",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ),
        PlayableVideoInfo(
            id = "vid_6",
            title = "Sajan Tumse Pyar - Video Song | Salman Khan, Sushmita Sen | Biwi No. 1",
            channel = "Bollywood Classics",
            views = "17M views",
            time = "2 years ago",
            duration = "03:58",
            thumbnailUrl = "https://picsum.photos/seed/sajan/640/360",
            videoUrl = "https://m.youtube.com/watch?v=sajan_tumse_pyar",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        ),
        PlayableVideoInfo(
            id = "vid_7",
            title = "Ye Dil Walon Ki Basti Hai | Bhakti Film Bhajan Live",
            channel = "Bhakti Flow Bhajan",
            views = "18 watching",
            time = "Live",
            duration = "LIVE",
            thumbnailUrl = "https://picsum.photos/seed/bhakti/640/360",
            videoUrl = "https://m.youtube.com/watch?v=bhakti_bhajan",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        )
    )

    fun onSearchQueryChange(query: String) {
        _ytSearchQuery.value = query
        if (query.isBlank()) {
            _ytSearchResults.value = emptyList()
        } else {
            val q = query.trim().lowercase()
            val filtered = allKnownVideos.filter {
                it.title.lowercase().contains(q) ||
                it.channel.lowercase().contains(q)
            }
            if (filtered.isNotEmpty()) {
                _ytSearchResults.value = filtered
            } else {
                // Generate relevant dynamic results for this query
                _ytSearchResults.value = listOf(
                    PlayableVideoInfo(
                        id = "dyn_1",
                        title = "$query - Official HD Video Song",
                        channel = "Music Records",
                        views = "1.4M views",
                        time = "2 weeks ago",
                        duration = "04:12",
                        thumbnailUrl = "https://picsum.photos/seed/${query.hashCode()}/640/360",
                        videoUrl = "https://m.youtube.com/watch?v=${query.hashCode()}",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    ),
                    PlayableVideoInfo(
                        id = "dyn_2",
                        title = "$query | Best Romantic Melody 2026",
                        channel = "Studio Acoustic",
                        views = "520K views",
                        time = "1 month ago",
                        duration = "05:40",
                        thumbnailUrl = "https://picsum.photos/seed/${query.hashCode() + 1}/640/360",
                        videoUrl = "https://m.youtube.com/watch?v=${query.hashCode() + 1}",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
                    ),
                    PlayableVideoInfo(
                        id = "dyn_3",
                        title = "$query (Full Album Audio Jukebox)",
                        channel = "Universal Melody",
                        views = "3.2M views",
                        time = "3 months ago",
                        duration = "32:15",
                        thumbnailUrl = "https://picsum.photos/seed/${query.hashCode() + 2}/640/360",
                        videoUrl = "https://m.youtube.com/watch?v=${query.hashCode() + 2}",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                    )
                )
            }
        }
    }

    fun playSelectedVideo(video: PlayableVideoInfo) {
        _currentlyPlayingVideo.value = video
        _detectedVideo.value = DetectedVideoInfo(
            title = video.title,
            url = video.videoUrl,
            thumbnailUrl = video.thumbnailUrl,
            videoId = video.id,
            streamUrl = video.streamUrl
        )
        _webTitle.value = video.title
        _inputUrl.value = video.videoUrl
        viewModelScope.launch {
            repository.addHistory(video.title, video.videoUrl)
        }
    }

    fun closeActiveVideo() {
        _currentlyPlayingVideo.value = null
        _detectedVideo.value = null
    }

    fun setInputUrl(url: String) {
        _inputUrl.value = url
    }

    fun navigateToUrl(url: String) {
        val target = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.contains(".") && !url.contains(" ") -> "https://$url"
            else -> {
                // If user typed a search term in the address bar, perform YouTube search
                onSearchQueryChange(url)
                "https://m.youtube.com/results?search_query=" + java.net.URLEncoder.encode(url, "UTF-8")
            }
        }
        _currentUrl.value = target
        _inputUrl.value = target
        detectVideoFromUrl(target, _webTitle.value)
        viewModelScope.launch {
            repository.addHistory(_webTitle.value, target)
        }
    }

    fun onPageStarted(url: String) {
        _currentUrl.value = url
        _inputUrl.value = url
        detectVideoFromUrl(url, _webTitle.value)
    }

    fun onPageFinished(url: String, title: String?) {
        _currentUrl.value = url
        _inputUrl.value = url
        val cleanTitle = if (title.isNullOrBlank() || title == "about:blank") "Web Page" else title
        _webTitle.value = cleanTitle
        detectVideoFromUrl(url, cleanTitle)
        viewModelScope.launch {
            repository.addHistory(cleanTitle, url)
        }
    }

    fun updateNavState(canBack: Boolean, canForward: Boolean) {
        _canGoBack.value = canBack
        _canGoForward.value = canForward
    }

    fun toggleDesktopMode() {
        _isDesktopMode.value = !_isDesktopMode.value
    }

    fun toggleBlockAutoplay() {
        _isBlockAutoplay.value = !_isBlockAutoplay.value
    }

    fun openLeftDrawer() {
        _isLeftDrawerOpen.value = true
        _isRightDrawerOpen.value = false
    }

    fun closeLeftDrawer() {
        _isLeftDrawerOpen.value = false
    }

    fun setLeftDrawerTab(tab: Int) {
        _leftDrawerTab.value = tab
    }

    fun openRightDrawer(tab: Int = 0) {
        _rightDrawerTab.value = tab
        _isRightDrawerOpen.value = true
        _isLeftDrawerOpen.value = false
    }

    fun closeRightDrawer() {
        _isRightDrawerOpen.value = false
    }

    fun setRightDrawerTab(tab: Int) {
        _rightDrawerTab.value = tab
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun toggleSearch() {
        _isSearchVisible.value = !_isSearchVisible.value
        if (!_isSearchVisible.value) {
            _searchQuery.value = ""
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun detectVideoFromUrl(url: String, title: String) {
        val ytRegex = Regex("""(?:youtube\.com\/(?:[^\/]+\/.+\/|(?:v|e(?:mbed)?)\/|.*[?&]v=)|youtu\.be\/|youtube\.com\/shorts\/)([a-zA-Z0-9_-]{11})""")
        val match = ytRegex.find(url)

        if (match != null) {
            val videoId = match.groupValues[1]
            val videoTitle = if (title.isNotBlank() && title != "YouTube" && title != "Web Page") {
                title
            } else {
                "Chunnari Chunnari - Biwi No.1 (1999) | Salman Khan | Sushmita Sen"
            }
            _detectedVideo.value = DetectedVideoInfo(
                title = videoTitle,
                url = url,
                thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                videoId = videoId,
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            )
        } else if (url.contains("facebook.com") || url.contains("dailymotion.com") || url.contains("vimeo.com") || url.contains(".mp4") || url.contains("/watch")) {
            _detectedVideo.value = DetectedVideoInfo(
                title = title.ifBlank { "Online Video Media" },
                url = url,
                thumbnailUrl = "https://picsum.photos/seed/tube/400/225",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            )
        }
    }

    // Start video parsing flow (as seen in screenshots 00:54 - 00:55)
    fun startParsingVideo() {
        val video = _detectedVideo.value ?: return
        _isParsing.value = true
        _parsingProgress.value = 0

        parsingJob?.cancel()
        parsingJob = viewModelScope.launch {
            for (p in 0..100 step 4) {
                delay(30)
                _parsingProgress.value = p
            }
            _isParsing.value = false
            val formats = repository.getAvailableFormatsForVideo(video.title)
            _availableFormats.value = formats
            _selectedFormat.value = formats.getOrNull(1) // 720p default selected
            _showFormatDialog.value = true
        }
    }

    fun cancelParsing() {
        parsingJob?.cancel()
        _isParsing.value = false
        _parsingProgress.value = 0
    }

    fun closeFormatDialog() {
        _showFormatDialog.value = false
    }

    fun selectFormat(format: VideoFormatOption) {
        _selectedFormat.value = format
    }

    fun toggleEnqueueLater() {
        _enqueueLater.value = !_enqueueLater.value
    }

    fun confirmDownload() {
        val video = _detectedVideo.value ?: return
        val format = _selectedFormat.value ?: return
        val enqueue = _enqueueLater.value

        _showFormatDialog.value = false

        viewModelScope.launch {
            repository.startDownload(
                title = video.title,
                url = video.url,
                thumbnailUrl = video.thumbnailUrl,
                format = format,
                enqueueOnly = enqueue
            )
            // Open Right Drawer to Downloads tab so user sees active download
            openRightDrawer(0)
        }
    }

    fun pauseDownload(id: String) {
        viewModelScope.launch {
            repository.pauseDownload(id)
        }
    }

    fun resumeDownload(id: String, totalBytes: Long) {
        viewModelScope.launch {
            repository.resumeDownload(id, totalBytes)
        }
    }

    fun deleteDownload(id: String) {
        viewModelScope.launch {
            repository.deleteDownload(id)
        }
    }

    fun pauseAll() {
        viewModelScope.launch {
            repository.pauseAll()
        }
    }

    fun resumeAll() {
        viewModelScope.launch {
            repository.resumeAll()
        }
    }

    fun playMedia(media: DownloadEntity) {
        _activePlayerMedia.value = media
    }

    fun closePlayer() {
        _activePlayerMedia.value = null
    }
}
