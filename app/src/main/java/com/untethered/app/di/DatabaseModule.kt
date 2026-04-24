package com.untethered.app.di

import android.content.Context
import androidx.room.Room
import com.untethered.app.data.local.UntetheredDatabase
import com.untethered.app.data.local.dao.CommandHistoryDao
import com.untethered.app.data.local.dao.CommandSnippetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): UntetheredDatabase =
        Room.databaseBuilder(
            context,
            UntetheredDatabase::class.java,
            "termidroid.db"
        ).build()

    @Provides
    @Singleton
    fun provideHistoryDao(db: UntetheredDatabase): CommandHistoryDao =
        db.historyDao()

    @Provides
    @Singleton
    fun provideSnippetDao(db: UntetheredDatabase): CommandSnippetDao =
        db.snippetDao()
}