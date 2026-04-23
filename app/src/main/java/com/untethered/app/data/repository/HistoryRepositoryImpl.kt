package com.untethered.app.data.repository

import com.untethered.app.data.local.dao.CommandHistoryDao
import com.untethered.app.data.local.entity.CommandHistoryEntity
import com.untethered.app.data.local.mapper.toDomain
import com.untethered.app.domain.model.CommandHistory
import com.untethered.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dao: CommandHistoryDao
) : HistoryRepository {

    override fun getAllHistory(): Flow<List<CommandHistory>> =
        dao.getAllHistory().map { list -> list.map { it.toDomain() } }

    override suspend fun insertCommand(command: String) =
        dao.insertCommand(CommandHistoryEntity(command = command))

    override suspend fun deleteCommand(id: Long) =
        dao.deleteCommand(id)

    override suspend fun clearAllHistory() =
        dao.clearAll()
}