package com.untethered.app.domain.model

data class TerminalLine(
    val raw: String,
    val isError: Boolean = false,
    val isInput: Boolean = false
)