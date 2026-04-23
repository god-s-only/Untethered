package com.untethered.app.domain.repository

import com.untethered.app.domain.model.CommandHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<CommandHistory>>
    suspend fun insertCommand(command: String)
    suspend fun deleteCommand(id: Long)
    suspend fun clearAllHistory()
}