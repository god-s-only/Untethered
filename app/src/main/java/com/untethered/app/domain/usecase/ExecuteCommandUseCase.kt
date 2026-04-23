package com.untethered.app.domain.usecase

import com.untethered.app.domain.model.CommandResult
import com.untethered.app.domain.repository.TerminalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteCommandUseCase @Inject constructor(
    private val terminalRepository: TerminalRepository
) {
    operator fun invoke(command: String): Flow<CommandResult> =
        terminalRepository.executeCommand(command)
}