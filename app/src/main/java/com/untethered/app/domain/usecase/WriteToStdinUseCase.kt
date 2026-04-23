package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.TerminalRepository
import javax.inject.Inject

class WriteToStdinUseCase @Inject constructor(
    private val terminalRepository: TerminalRepository
) {
    suspend operator fun invoke(input: String) =
        terminalRepository.writeToStdin(input)
}