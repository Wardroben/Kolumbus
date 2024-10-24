package ru.smalljinn.kolumbus.data.util

import kotlinx.coroutines.flow.Flow

interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync()
}