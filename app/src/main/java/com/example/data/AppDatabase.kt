package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DownloadEntity::class, HistoryEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tubemate_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.downloadDao(), database.historyDao())
                    }
                }
            }

            suspend fun populateInitialData(downloadDao: DownloadDao, historyDao: HistoryDao) {
                // Initial completed & active downloads from the screenshots with real stream URLs
                downloadDao.insertOrUpdate(
                    DownloadEntity(
                        id = "dl_1",
                        title = "Chunnari Chunnari - Biwi No.1 (1999) _ Abhijeet _ Anuradha",
                        sourceUrl = "https://m.youtube.com/watch?v=7SLGxEDyqWo",
                        thumbnailUrl = "https://img.youtube.com/vi/7SLGxEDyqWo/hqdefault.jpg",
                        formatLabel = "1280x720 (MP4)",
                        mediaType = "VIDEO",
                        totalBytes = 99433 * 1024L,
                        downloadedBytes = 99433 * 1024L,
                        status = "COMPLETED",
                        speedText = "0 KB/s",
                        durationText = "05:15",
                        filePath = "/storage/emulated/0/Movies/Chunnari_Chunnari.mp4",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        timestamp = System.currentTimeMillis() - 120000
                    )
                )
                downloadDao.insertOrUpdate(
                    DownloadEntity(
                        id = "dl_2",
                        title = "screen-20260929-220655.mp4",
                        sourceUrl = "https://m.youtube.com/watch?v=screen_rec_1",
                        thumbnailUrl = "https://picsum.photos/seed/screen220655/400/225",
                        formatLabel = "854x480 (MP4)",
                        mediaType = "VIDEO",
                        totalBytes = 60917 * 1024L,
                        downloadedBytes = 60917 * 1024L,
                        status = "COMPLETED",
                        speedText = "0 KB/s",
                        durationText = "01:44",
                        filePath = "/storage/emulated/0/Movies/screen-20260929-220655.mp4",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                        timestamp = System.currentTimeMillis() - 800000
                    )
                )
                downloadDao.insertOrUpdate(
                    DownloadEntity(
                        id = "dl_3",
                        title = "screen-20260929-133201.mp4",
                        sourceUrl = "https://m.youtube.com/watch?v=screen_rec_2",
                        thumbnailUrl = "https://picsum.photos/seed/screen133201/400/225",
                        formatLabel = "640x360 (MP4)",
                        mediaType = "VIDEO",
                        totalBytes = 20194 * 1024L,
                        downloadedBytes = 20194 * 1024L,
                        status = "COMPLETED",
                        speedText = "0 KB/s",
                        durationText = "00:12",
                        filePath = "/storage/emulated/0/Movies/screen-20260929-133201.mp4",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                        timestamp = System.currentTimeMillis() - 1500000
                    )
                )
                downloadDao.insertOrUpdate(
                    DownloadEntity(
                        id = "dl_4",
                        title = "murgi dim",
                        sourceUrl = "https://m.youtube.com/watch?v=murgi_dim",
                        thumbnailUrl = "",
                        formatLabel = "Audio (MP3, 128k)",
                        mediaType = "AUDIO",
                        totalBytes = 250 * 1024L,
                        downloadedBytes = 250 * 1024L,
                        status = "COMPLETED",
                        speedText = "0 KB/s",
                        durationText = "00:16",
                        filePath = "/storage/emulated/0/Music/murgi_dim.mp3",
                        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                        timestamp = System.currentTimeMillis() - 2500000
                    )
                )
                downloadDao.insertOrUpdate(
                    DownloadEntity(
                        id = "dl_5",
                        title = "AUD-20260928-WA0003",
                        sourceUrl = "https://m.youtube.com/watch?v=wa_audio",
                        thumbnailUrl = "",
                        formatLabel = "Audio (M4A/AAC, 128k)",
                        mediaType = "AUDIO",
                        totalBytes = 250 * 1024L,
                        downloadedBytes = 250 * 1024L,
                        status = "COMPLETED",
                        speedText = "0 KB/s",
                        durationText = "00:17",
                        filePath = "/storage/emulated/0/Music/AUD-20260928-WA0003.m4a",
                        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                        timestamp = System.currentTimeMillis() - 3600000
                    )
                )
            }
        }
    }
}
