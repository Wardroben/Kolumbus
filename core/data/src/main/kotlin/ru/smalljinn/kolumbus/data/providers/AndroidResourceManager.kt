package ru.smalljinn.kolumbus.data.providers

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.smalljinn.domain.resource.ResourceManager
import javax.inject.Inject

class AndroidResourceManager @Inject constructor(
    @ApplicationContext private val context: Context
): ResourceManager{
    override fun getString(id: Int): String {
        return context.getString(id)
    }
}