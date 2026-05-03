package com.untethered.app.domain.model

import androidx.compose.runtime.Stable

@Stable
data class CommandHistory(
    val id: Long = 0,
    val command: String,
    val timestamp: Long = System.currentTimeMillis()
)