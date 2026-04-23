package com.untethered.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.untethered.app.data.local.entity.CommandHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandHistoryDao {

    @Query("SELECT * FROM command_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CommandHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCommand(entity: CommandHistoryEntity)

    @Query("DELETE FROM command_history WHERE id = :id")
    suspend fun deleteCommand(id: Long)

    @Query("DELETE FROM command_history")
    suspend fun clearAll()
}