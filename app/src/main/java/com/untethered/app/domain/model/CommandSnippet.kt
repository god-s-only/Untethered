package com.untethered.app.domain.model

data class CommandSnippet(
    val id: Long = 0,
    val command: String,
    val label: String = "",
    val timestamp: Long = System.currentTimeMillis()
)