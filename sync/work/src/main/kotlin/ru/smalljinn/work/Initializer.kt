package ru.smalljinn.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import ru.smalljinn.work.workers.SyncWorker

object Sync {
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork()
            )
        }
    }
}

internal const val SYNC_WORK_NAME = "SyncWorkName"