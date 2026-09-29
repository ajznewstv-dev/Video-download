package com.example.model

enum class MediaType {
    VIDEO, AUDIO
}

enum class DownloadStatus {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED
}

data class VideoFormatOption(
    val id: String,
    val label: String,
    val resolution: String,
    val container: String,
    val approximateSizeMb: Double,
    val sizeText: String,
    val isAudio: Boolean = false,
    val bitrate: String? = null
)

data class SiteBookmark(
    val name: String,
    val url: String,
    val iconKey: String,
    val domain: String,
    val category: String = "Video"
)

data class PlaylistItem(
    val id: String,
    val name: String,
    val count: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
