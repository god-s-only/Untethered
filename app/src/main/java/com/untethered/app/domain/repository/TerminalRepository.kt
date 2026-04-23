package com.untethered.app.domain.repository

import com.untethered.app.domain.model.CommandResult
import kotlinx.coroutines.flow.Flow

interface TerminalRepository {

    fun executeCommand(command: String): Flow<CommandResult>
    suspend fun writeToStdin(input: String)
    suspend fun killCurrentProcess()
}