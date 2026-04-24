package com.untethered.app.presentation.terminal

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.untethered.app.data.process.AnsiParser
import com.untethered.app.di.ShizukuHelper
import com.untethered.app.domain.model.CommandResult
import com.untethered.app.domain.usecase.ExecuteCommandUseCase
import com.untethered.app.domain.usecase.KillProcessUseCase
import com.untethered.app.domain.usecase.SaveCommandToHistoryUseCase
import com.untethered.app.domain.usecase.WriteToStdinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val executeCommand: ExecuteCommandUseCase,
    private val killProcess: KillProcessUseCase,
    private val writeToStdin: WriteToStdinUseCase,
    private val saveToHistory: SaveCommandToHistoryUseCase,
    private val shizukuHelper: ShizukuHelper
) : ViewModel() {

    companion object {
        private const val SHIZUKU_REQUEST_CODE = 1001
        private const val MAX_LINES = 2000
    }

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    private val sessionHistory = mutableListOf<String>()
    private var historyIndex = -1

    private var commandJob: Job? = null

    private val permissionListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == SHIZUKU_REQUEST_CODE) {
                val granted = grantResult ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                _uiState.update { it.copy(shizukuPermissionGranted = granted) }
            }
        }

    init {
        shizukuHelper.addRequestPermissionResultListener(
            SHIZUKU_REQUEST_CODE,
            permissionListener
        )
        refreshShizukuState()
    }
    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
        historyIndex = -1
    }

    fun onSendCommand() {
        val command = _uiState.value.inputText.trim()
        if (command.isEmpty()) return

        sessionHistory.add(0, command)
        historyIndex = -1

        _uiState.update { it.copy(inputText = "") }

        appendLine(
            raw = "$ $command",
            isInput = true,
            isError = false
        )

        viewModelScope.launch { saveToHistory(command) }

        runCommand(command)
    }

    fun onHistoryUp() {
        if (sessionHistory.isEmpty()) return
        historyIndex = (historyIndex + 1).coerceAtMost(sessionHistory.lastIndex)
        _uiState.update {
            it.copy(inputText = sessionHistory[historyIndex])
        }
    }

    fun onHistoryDown() {
        historyIndex--
        val text = if (historyIndex < 0) {
            historyIndex = -1
            ""
        } else {
            sessionHistory[historyIndex]
        }
        _uiState.update { it.copy(inputText = text) }
    }

    fun onCtrlC() {
        viewModelScope.launch {
            killProcess()
            appendLine(raw = "^C", isError = true)
            _uiState.update { it.copy(isRunning = false) }
        }
    }

    fun onClearSession() {
        commandJob?.cancel()
        viewModelScope.launch { killProcess() }
        _uiState.update {
            it.copy(
                lines = emptyList(),
                isRunning = false,
                inputText = ""
            )
        }
        sessionHistory.clear()
        historyIndex = -1
    }

    fun onPasteCommand(command: String) {
        _uiState.update { it.copy(inputText = command) }
    }

    fun onRequestShizukuPermission() {
        shizukuHelper.requestPermission(SHIZUKU_REQUEST_CODE)
    }

    fun refreshShizukuState() {
        _uiState.update {
            it.copy(
                shizukuAvailable = shizukuHelper.isAvailable(),
                shizukuPermissionGranted = shizukuHelper.hasPermission()
            )
        }
    }


    private fun runCommand(command: String) {
        commandJob?.cancel()
        commandJob = viewModelScope.launch {
            executeCommand(command)
                .catch { e ->
                    appendLine(
                        raw = "Error: ${e.message ?: "Unknown error"}",
                        isError = true
                    )
                    _uiState.update { it.copy(isRunning = false) }
                }
                .collect { result ->
                    when (result) {
                        is CommandResult.Running -> {
                            _uiState.update { it.copy(isRunning = true) }
                        }

                        is CommandResult.Output -> {
                            appendLine(
                                raw = result.line,
                                isError = result.isError
                            )
                        }

                        is CommandResult.Exit -> {
                            if (result.code != 0) {
                                appendLine(
                                    raw = "Process exited with code ${result.code}",
                                    isError = true
                                )
                            }
                            _uiState.update { it.copy(isRunning = false) }
                        }
                    }
                }
        }
    }

    private fun appendLine(
        raw: String,
        isError: Boolean = false,
        isInput: Boolean = false
    ) {
        val annotated: AnnotatedString = if (isInput) {
            buildAnnotatedString { append(raw) }
        } else {
            AnsiParser.parse(raw)
        }

        val newLine = TerminalLineUi(
            annotated = annotated,
            isError = isError,
            isInput = isInput
        )

        _uiState.update { state ->
            val updatedLines = (state.lines + newLine).let { lines ->
                if (lines.size > MAX_LINES) {
                    lines.takeLast(MAX_LINES)
                } else {
                    lines
                }
            }
            state.copy(lines = updatedLines)
        }
    }

    override fun onCleared() {
        super.onCleared()
        shizukuHelper.removeRequestPermissionResultListener(permissionListener)
        commandJob?.cancel()
    }
}