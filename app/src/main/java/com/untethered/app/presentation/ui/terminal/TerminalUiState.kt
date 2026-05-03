package com.untethered.app.presentation.ui.terminal

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString

@Stable
data class TerminalUiState(
    val lines: List<TerminalLineUi> = emptyList(),
    val inputText: String = "",
    val isRunning: Boolean = false,
    val shizukuAvailable: Boolean = false,
    val shizukuPermissionGranted: Boolean = false
)

@Stable
data class TerminalLineUi(
    val id: Long,
    val annotated: AnnotatedString,
    val isError: Boolean = false,
    val isInput: Boolean = false
)
