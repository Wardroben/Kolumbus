package ru.smalljinn.permissions

interface PermissionTextProvider {
    fun getDescriptionResId(isPermanentlyDeclined: Boolean): Int //TODO is permanently declined parameter
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescriptionResId(isPermanentlyDeclined: Boolean): Int =
        if (isPermanentlyDeclined) R.string.camera_permission_permanently_declined_description
        else R.string.camera_permission_required_description

}

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescriptionResId(isPermanentlyDeclined: Boolean): Int =
        if (isPermanentlyDeclined) R.string.location_permission_permanently_declined_description
        else R.string.location_permission_required_description

}