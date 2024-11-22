package ru.smalljinn.domain.resource

import androidx.annotation.StringRes

interface ResourceManager {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg formatArgs: Any): String
}