package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.HistoryRepository
import javax.inject.Inject

class SaveCommandToHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(command: String) =
        historyRepository.insertCommand(command)
}