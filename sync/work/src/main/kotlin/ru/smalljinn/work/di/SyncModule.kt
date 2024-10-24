package ru.smalljinn.work.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smalljinn.kolumbus.data.util.SyncManager
import ru.smalljinn.work.status.WorkManagerSyncManager

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    internal abstract fun bindSyncManager(
        syncStatusMonitor: WorkManagerSyncManager
    ): SyncManager
}