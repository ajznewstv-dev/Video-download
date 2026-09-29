package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE mediaType = 'VIDEO' AND status = 'COMPLETED' ORDER BY timestamp DESC")
    fun getCompletedVideos(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE mediaType = 'AUDIO' AND status = 'COMPLETED' ORDER BY timestamp DESC")
    fun getCompletedAudios(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN ('DOWNLOADING', 'QUEUED', 'PAUSED') ORDER BY timestamp DESC")
    fun getActiveDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(download: DownloadEntity)

    @Update
    suspend fun update(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE downloads SET downloadedBytes = :downloadedBytes, speedText = :speedText, status = :status WHERE id = :id")
    suspend fun updateProgress(id: String, downloadedBytes: Long, speedText: String, status: String)

    @Query("UPDATE downloads SET status = 'PAUSED' WHERE status = 'DOWNLOADING'")
    suspend fun pauseAll()

    @Query("UPDATE downloads SET status = 'DOWNLOADING' WHERE status = 'PAUSED'")
    suspend fun resumeAll()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}
