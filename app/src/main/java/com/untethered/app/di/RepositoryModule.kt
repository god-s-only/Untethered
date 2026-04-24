package com.untethered.app.di

import com.untethered.app.data.repository.HistoryRepositoryImpl
import com.untethered.app.data.repository.SnippetRepositoryImpl
import com.untethered.app.data.repository.TerminalRepositoryImpl
import com.untethered.app.domain.repository.HistoryRepository
import com.untethered.app.domain.repository.SnippetRepository
import com.untethered.app.domain.repository.TerminalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTerminalRepository(
        impl: TerminalRepositoryImpl
    ): TerminalRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSnippetRepository(
        impl: SnippetRepositoryImpl
    ): SnippetRepository
}
