package com.untethered.app.presentation.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.untethered.app.domain.usecase.ClearHistoryUseCase
import com.untethered.app.domain.usecase.DeleteHistoryItemUseCase
import com.untethered.app.domain.usecase.DeleteSnippetUseCase
import com.untethered.app.domain.usecase.GetHistoryUseCase
import com.untethered.app.domain.usecase.GetSnippetsUseCase
import com.untethered.app.domain.usecase.SaveSnippetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val getHistory: GetHistoryUseCase,
    private val deleteHistoryItem: DeleteHistoryItemUseCase,
    private val clearHistory: ClearHistoryUseCase,
    private val getSnippets: GetSnippetsUseCase,
    private val saveSnippet: SaveSnippetUseCase,
    private val deleteSnippet: DeleteSnippetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()

    init {
        observeDrawerData()
    }

    private fun observeDrawerData() {
        viewModelScope.launch {
            combine(
                getHistory(),
                getSnippets()
            ) { history, snippets ->
                DrawerUiState(
                    history = history,
                    snippets = snippets
                )
            }.collect { combined ->
                _uiState.update { combined }
            }
        }
    }


    fun onDeleteHistoryItem(id: Long) {
        viewModelScope.launch { deleteHistoryItem(id) }
    }

    fun onClearHistory() {
        viewModelScope.launch { clearHistory() }
    }

    fun onSaveSnippet(command: String, label: String = "") {
        viewModelScope.launch { saveSnippet(command, label) }
    }

    fun onDeleteSnippet(id: Long) {
        viewModelScope.launch { deleteSnippet(id) }
    }
}