package com.untethered.app.domain.model

data class CommandHistory(
    val id: Long = 0,
    val command: String,
    val timestamp: Long = System.currentTimeMillis()
)