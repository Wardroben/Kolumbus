package ru.smalljinn.kolumbus.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.domain.backup.BackupMessageProvider
import ru.smalljinn.kolumbus.data.backup.AndroidBackupMessageProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BackupModule {
    @Binds
    @Singleton
    fun bindBackupMessageProvider(
        provider: AndroidBackupMessageProvider
    ): BackupMessageProvider
}