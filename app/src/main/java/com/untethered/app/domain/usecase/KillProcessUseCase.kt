package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.TerminalRepository
import javax.inject.Inject

class KillProcessUseCase @Inject constructor(
    private val terminalRepository: TerminalRepository
) {
    suspend operator fun invoke() = terminalRepository.killCurrentProcess()
}