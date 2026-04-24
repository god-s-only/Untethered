package com.untethered.app.presentation.drawer

import com.untethered.app.domain.model.CommandHistory
import com.untethered.app.domain.model.CommandSnippet

data class DrawerUiState(
    val history: List<CommandHistory> = emptyList(),
    val snippets: List<CommandSnippet> = emptyList()
)