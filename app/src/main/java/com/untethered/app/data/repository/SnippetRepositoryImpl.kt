package com.untethered.app.data.repository

import com.untethered.app.data.local.dao.CommandSnippetDao
import com.untethered.app.data.local.entity.CommandSnippetEntity
import com.untethered.app.data.local.mapper.toDomain
import com.untethered.app.domain.model.CommandSnippet
import com.untethered.app.domain.repository.SnippetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SnippetRepositoryImpl @Inject constructor(
    private val dao: CommandSnippetDao
) : SnippetRepository {

    override fun getAllSnippets(): Flow<List<CommandSnippet>> =
        dao.getAllSnippets().map { list -> list.map { it.toDomain() } }

    override suspend fun saveSnippet(command: String, label: String) =
        dao.insertSnippet(CommandSnippetEntity(command = command, label = label))

    override suspend fun deleteSnippet(id: Long) =
        dao.deleteSnippet(id)
}