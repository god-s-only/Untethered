package com.untethered.app.data.repository

import com.untethered.app.data.process.ShizukuShellExecutor
import com.untethered.app.domain.model.CommandResult
import com.untethered.app.domain.repository.TerminalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TerminalRepositoryImpl @Inject constructor(
    private val executor: ShizukuShellExecutor
) : TerminalRepository {

    override fun executeCommand(command: String): Flow<CommandResult> =
        executor.execute(command)

    override suspend fun writeToStdin(input: String) =
        executor.writeToStdin(input)

    override suspend fun killCurrentProcess() =
        executor.killCurrent()
}