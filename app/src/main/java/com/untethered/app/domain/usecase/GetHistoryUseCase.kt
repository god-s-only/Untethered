package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.HistoryRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke() = historyRepository.getAllHistory()
}