package ru.smalljinn.kolumbus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.smalljinn.work.Sync

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Sync.initialize(context = this)
    }
}