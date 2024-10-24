package ru.smalljinn.work.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import ru.smalljinn.kolumbus.data.util.SyncManager
import ru.smalljinn.work.SYNC_WORK_NAME
import ru.smalljinn.work.workers.SyncWorker
import javax.inject.Inject

internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map { it.anyRunning() }
            .conflate()

    override fun requestSync() {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            SyncWorker.startUpSyncWork()
        )
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }