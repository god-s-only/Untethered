package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.SnippetRepository
import javax.inject.Inject

class DeleteSnippetUseCase @Inject constructor(
    private val snippetRepository: SnippetRepository
) {
    suspend operator fun invoke(id: Long) =
        snippetRepository.deleteSnippet(id)
}