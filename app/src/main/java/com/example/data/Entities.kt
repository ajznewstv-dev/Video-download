package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val sourceUrl: String,
    val thumbnailUrl: String,
    val formatLabel: String,
    val mediaType: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: String,
    val speedText: String = "",
    val durationText: String = "03:45",
    val filePath: String = "",
    val streamUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val timeFormatted: String,
    val timestamp: Long = System.currentTimeMillis()
)
