package ru.smalljinn.permissions

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class State(
        val hasCameraAccess: Boolean,
        val hasLocationAccess: Boolean
    ) {
        val hasAllAccess: Boolean
            get() = hasCameraAccess && hasLocationAccess
    }

    private val _state = MutableStateFlow(getState())
    val state = _state.asStateFlow()

    private fun getState(): State = State(
        hasCameraAccess = hasAccess(CAMERA),
        hasLocationAccess = hasAccess(listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    )

    suspend fun checkPermissions() {
        val newState = getState()
        _state.emit(newState)
    }

    fun createSettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", context.packageName, null)
        }
        return intent
    }

    private fun hasAccess(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun hasAccess(permissions: List<String>): Boolean = permissions.all(::hasAccess)
}
