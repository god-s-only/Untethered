package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.SnippetRepository
import javax.inject.Inject

class SaveSnippetUseCase @Inject constructor(
    private val snippetRepository: SnippetRepository
) {
    suspend operator fun invoke(command: String, label: String = "") =
        snippetRepository.saveSnippet(command, label)
}