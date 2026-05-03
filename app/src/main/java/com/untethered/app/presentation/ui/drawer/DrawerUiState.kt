package com.untethered.app.presentation.ui.drawer

import androidx.compose.runtime.Stable
import com.untethered.app.domain.model.CommandHistory
import com.untethered.app.domain.model.CommandSnippet

@Stable
data class DrawerUiState(
    val history: List<CommandHistory> = emptyList(),
    val snippets: List<CommandSnippet> = emptyList()
)