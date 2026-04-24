package com.untethered.app.presentation.terminal

import androidx.compose.ui.text.AnnotatedString

data class TerminalUiState(
    val lines: List<TerminalLineUi> = emptyList(),
    val inputText: String = "",
    val isRunning: Boolean = false,
    val shizukuAvailable: Boolean = false,
    val shizukuPermissionGranted: Boolean = false
)

data class TerminalLineUi(
    val annotated: AnnotatedString,
    val isError: Boolean = false,
    val isInput: Boolean = false
)