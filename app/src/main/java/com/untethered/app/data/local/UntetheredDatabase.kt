package com.untethered.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.untethered.app.data.local.dao.CommandHistoryDao
import com.untethered.app.data.local.dao.CommandSnippetDao
import com.untethered.app.data.local.entity.CommandHistoryEntity
import com.untethered.app.data.local.entity.CommandSnippetEntity

@Database(
    entities = [CommandHistoryEntity::class, CommandSnippetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UntetheredDatabase : RoomDatabase() {
    abstract fun historyDao(): CommandHistoryDao
    abstract fun snippetDao(): CommandSnippetDao
}