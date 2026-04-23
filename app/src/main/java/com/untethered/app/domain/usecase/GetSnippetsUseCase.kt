package com.untethered.app.domain.usecase

import com.untethered.app.domain.repository.SnippetRepository
import javax.inject.Inject

class GetSnippetsUseCase @Inject constructor(
    private val snippetRepository: SnippetRepository
) {
    operator fun invoke() = snippetRepository.getAllSnippets()
}