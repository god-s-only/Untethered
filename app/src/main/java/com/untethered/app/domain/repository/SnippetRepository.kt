package com.untethered.app.domain.repository

import com.untethered.app.domain.model.CommandSnippet
import kotlinx.coroutines.flow.Flow

interface SnippetRepository {
    fun getAllSnippets(): Flow<List<CommandSnippet>>
    suspend fun saveSnippet(command: String, label: String = "")
    suspend fun deleteSnippet(id: Long)
}