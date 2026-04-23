package com.untethered.app.domain.model

sealed class CommandResult {
    data class Output(val line: String, val isError: Boolean = false) : CommandResult()
    data class Exit(val code: Int) : CommandResult()
    data object Running : CommandResult()
}