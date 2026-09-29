package com.example.data

import com.example.model.SiteBookmark
import com.example.model.VideoFormatOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MediaRepository(
    private val downloadDao: DownloadDao,
    private val historyDao: HistoryDao,
    private val scope: CoroutineScope
) {
    val allDownloads: Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()
    val completedVideos: Flow<List<DownloadEntity>> = downloadDao.getCompletedVideos()
    val completedAudios: Flow<List<DownloadEntity>> = downloadDao.getCompletedAudios()
    val activeDownloads: Flow<List<DownloadEntity>> = downloadDao.getActiveDownloads()
    val historyItems: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    private val activeJobs = mutableMapOf<String, Job>()

    // Default TubeMate bookmarks exactly as in screenshot 00:12 & 01:10
    val defaultBookmarks: List<SiteBookmark> = listOf(
        SiteBookmark("YouTube", "https://m.youtube.com/", "youtube", "m.youtube.com"),
        SiteBookmark("FaceBook", "https://m.facebook.com/watch", "facebook", "m.facebook.com"),
        SiteBookmark("Instagram", "https://www.instagram.com", "instagram", "www.instagram.com"),
        SiteBookmark("Twitter", "https://mobile.twitter.com", "twitter", "mobile.twitter.com"),
        SiteBookmark("Threads", "https://www.threads.com", "threads", "www.threads.com"),
        SiteBookmark("DailyMotion", "https://www.dailymotion.com", "dailymotion", "www.dailymotion.com"),
        SiteBookmark("YouKu", "https://www.youku.com", "youku", "www.youku.com"),
        SiteBookmark("Vimeo", "https://vimeo.com/watch", "vimeo", "vimeo.com"),
        SiteBookmark("Google", "https://www.google.com", "google", "www.google.com"),
        SiteBookmark("Naver TV", "https://m.tv.naver.com", "naver", "m.tv.naver.com"),
        SiteBookmark("Kakao TV", "https://tv.kakao.com", "kakao", "tv.kakao.com"),
        SiteBookmark("Mango TV", "https://glb.m.mgtv.com", "mango", "glb.m.mgtv.com"),
        SiteBookmark("SoundCloud", "https://soundcloud.com", "soundcloud", "soundcloud.com")
    )

    // Standard TubeMate formats as shown in screenshot 00:55 - 00:58
    fun getAvailableFormatsForVideo(videoTitle: String): List<VideoFormatOption> {
        return listOf(
            VideoFormatOption("f1", "* 1920x1080 (MP4)", "1080p", "mp4", 97.0, "97.0 MB"),
            VideoFormatOption("f2", "* 1280x720 (MP4)", "720p", "mp4", 43.0, "43.0 MB"),
            VideoFormatOption("f3", "* 854x480 (MP4)", "480p", "mp4", 26.9, "26.9 MB"),
            VideoFormatOption("f4", "* 640x360 (MP4)", "360p", "mp4", 17.2, "17.2 MB"),
            VideoFormatOption("f5", "* 426x240 (MP4)", "240p", "mp4", 11.0, "11.0 MB"),
            VideoFormatOption("f6", "* 256x144 (MP4)", "144p", "mp4", 4.9, "4.9 MB"),
            VideoFormatOption("f7", "* Audio (M4A/AAC, 128k)", "Audio", "m4a", 4.9, "4.9 MB", isAudio = true, bitrate = "128k"),
            VideoFormatOption("f8", "* Audio (OGG, 48k)", "Audio", "ogg", 2.0, "2.0 MB", isAudio = true, bitrate = "48k"),
            VideoFormatOption("f9", "* Audio (OGG, 128k)", "Audio", "ogg", 5.1, "5.1 MB", isAudio = true, bitrate = "128k"),
            VideoFormatOption("f10", "* Audio (MP3, 256k)", "Audio", "mp3", 9.8, "9.8 MB", isAudio = true, bitrate = "256k"),
            VideoFormatOption("f11", "* Audio (MP3, 128k)", "Audio", "mp3", 4.9, "4.9 MB", isAudio = true, bitrate = "128k"),
            VideoFormatOption("f12", "* 1920x1080 (WEBM)", "1080p", "webm", 60.5, "60.5 MB")
        )
    }

    suspend fun addHistory(title: String, url: String) {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeStr = timeFormat.format(Date())
        historyDao.insert(
            HistoryEntity(
                title = title.ifBlank { "Web Page" },
                url = url,
                timeFormatted = timeStr,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteHistory(id: Long) = historyDao.deleteById(id)

    suspend fun clearAllHistory() = historyDao.clearAll()

    suspend fun startDownload(
        title: String,
        url: String,
        thumbnailUrl: String,
        format: VideoFormatOption,
        enqueueOnly: Boolean = false
    ) {
        val id = UUID.randomUUID().toString()
        val totalBytes = (format.approximateSizeMb * 1024 * 1024).toLong()
        val mediaType = if (format.isAudio) "AUDIO" else "VIDEO"
        val folder = if (format.isAudio) "/storage/emulated/0/Music/" else "/storage/emulated/0/Movies/"
        val cleanTitle = title.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(30)
        val filePath = "$folder$cleanTitle.${format.container}"

        val stream = if (mediaType == "AUDIO") {
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        } else {
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        }

        val download = DownloadEntity(
            id = id,
            title = title,
            sourceUrl = url,
            thumbnailUrl = thumbnailUrl,
            formatLabel = format.label.replace("* ", ""),
            mediaType = mediaType,
            totalBytes = totalBytes,
            downloadedBytes = 0L,
            status = if (enqueueOnly) "QUEUED" else "DOWNLOADING",
            speedText = "0 KB/s",
            durationText = "05:15",
            filePath = filePath,
            streamUrl = stream,
            timestamp = System.currentTimeMillis()
        )
        downloadDao.insertOrUpdate(download)

        if (!enqueueOnly) {
            launchDownloadSimulation(id, totalBytes)
        }
    }

    fun launchDownloadSimulation(id: String, totalBytes: Long) {
        activeJobs[id]?.cancel()
        val job = scope.launch(Dispatchers.IO) {
            var currentBytes = 0L
            val stepSize = (totalBytes / 35).coerceAtLeast(64 * 1024L)
            while (isActive && currentBytes < totalBytes) {
                delay(300)
                currentBytes = (currentBytes + stepSize + (1024..8192).random()).coerceAtMost(totalBytes)
                val speedKb = (stepSize / 1024 * 1000 / 300) + (10..150).random()
                val speedText = if (speedKb > 1024) String.format(Locale.US, "%.1f MB/s", speedKb / 1024.0) else "$speedKb KB/s"
                val status = if (currentBytes >= totalBytes) "COMPLETED" else "DOWNLOADING"
                downloadDao.updateProgress(id, currentBytes, if (status == "COMPLETED") "0 KB/s" else speedText, status)
            }
        }
        activeJobs[id] = job
    }

    suspend fun pauseDownload(id: String) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        downloadDao.updateStatus(id, "PAUSED")
    }

    suspend fun resumeDownload(id: String, totalBytes: Long) {
        downloadDao.updateStatus(id, "DOWNLOADING")
        launchDownloadSimulation(id, totalBytes)
    }

    suspend fun deleteDownload(id: String) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        downloadDao.deleteById(id)
    }

    suspend fun pauseAll() {
        activeJobs.values.forEach { it.cancel() }
        activeJobs.clear()
        downloadDao.pauseAll()
    }

    suspend fun resumeAll() {
        downloadDao.resumeAll()
    }
}
