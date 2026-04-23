package com.untethered.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_snippets")
data class CommandSnippetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val command: String,
    val label: String = "",
    val timestamp: Long = System.currentTimeMillis()
)