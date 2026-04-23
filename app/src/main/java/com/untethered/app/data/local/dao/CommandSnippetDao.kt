package com.untethered.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.untethered.app.data.local.entity.CommandSnippetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandSnippetDao {

    @Query("SELECT * FROM command_snippets ORDER BY timestamp DESC")
    fun getAllSnippets(): Flow<List<CommandSnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(entity: CommandSnippetEntity)

    @Query("DELETE FROM command_snippets WHERE id = :id")
    suspend fun deleteSnippet(id: Long)
}