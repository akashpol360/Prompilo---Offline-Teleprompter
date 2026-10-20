package com.prompilo.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "scripts", indices = [Index(value = ["updatedAt"]), Index(value = ["folderId"])])
data class Script(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // Retained for compatibility with earlier Prompilo database installations.
    val isFavorite: Boolean = false,
    val folderId: Long? = null
)

@Entity(tableName = "script_folders")
data class ScriptFolder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#B96E00",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "video_drafts", indices = [Index(value = ["recordedAt"])])
data class VideoDraft(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val uri: String,
    val recordedAt: Long = System.currentTimeMillis(),
    val durationMillis: Long = 0,
    val localPath: String? = null
)
