package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.HistoryRepository
import javax.inject.Inject

class DeleteHistoryItemUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(id: Long) =
        historyRepository.deleteCommand(id)
}