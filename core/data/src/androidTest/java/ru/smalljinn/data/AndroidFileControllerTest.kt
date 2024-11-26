package ru.smalljinn.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.smalljinn.domain.saving.FileController
import ru.smalljinn.kolumbus.data.saving.AndroidFileController

@RunWith(AndroidJUnit4::class)
class AndroidFileControllerTest {
    private lateinit var context: Context
    private lateinit var fileController: FileController

    @Before
    fun setContext() {
        context = ApplicationProvider.getApplicationContext()
        fileController = AndroidFileController(context)
    }

    @Test
    fun fileManager_deletesFile() {

    }
}