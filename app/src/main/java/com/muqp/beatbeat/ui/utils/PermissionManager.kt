package com.muqp.beatbeat.ui.utils

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muqp.core_ui.app_permission.AppPermission
import com.muqp.core_ui.app_permission.hasPermission
import java.lang.ref.WeakReference
import com.muqp.beatbeat.ui.R as CoreUi

class PermissionManager(
    activity: FragmentActivity,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>
) {
    private val activityRef = WeakReference(activity)
    private val activity: FragmentActivity?
        get() = activityRef.get()

    fun checkAndRequestNeededPermissions() {
        activity?.let { safeActivity ->
            val neededPermissions = getRequiredPermissions()
                .filter { !safeActivity.hasPermission(it) }
                .map { it.permission }

            if (neededPermissions.isNotEmpty()) {
                requestPermissions(neededPermissions)
            }
        }
    }

    private fun getRequiredPermissions(): List<AppPermission> {
        return buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(AppPermission.MediaAudio)
                add(AppPermission.Notification)
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                add(AppPermission.Storage)
            }
        }
    }

    private fun requestPermissions(permissions: List<String>) {
        activity?.let { safeActivity ->
            val permissionsWithRationale = permissions.filter { permission ->
                safeActivity.shouldShowRequestPermissionRationale(permission)
            }

            if (permissionsWithRationale.isNotEmpty()) {
                val appPermission = getAppPermissionFor(permissionsWithRationale.first())
                showPermissionRationaleDialog(appPermission) {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            } else {
                permissionLauncher.launch(permissions.toTypedArray())
            }
        }
    }

    private fun getAppPermissionFor(permissionString: String): AppPermission {
        return when (permissionString) {
            Manifest.permission.POST_NOTIFICATIONS -> AppPermission.Notification
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> AppPermission.Storage
            Manifest.permission.READ_MEDIA_AUDIO -> AppPermission.MediaAudio
            else -> throw IllegalArgumentException("Unknown permission: $permissionString")
        }
    }

    private fun showPermissionRationaleDialog(
        permission: AppPermission,
        onConfirm: () -> Unit
    ) {
        activity?.let { safeActivity ->
            MaterialAlertDialogBuilder(safeActivity)
                .setTitle(safeActivity.getString(permission.rationaleTitle))
                .setMessage(safeActivity.getString(permission.rationaleMessage))
                .setPositiveButton(safeActivity.getString(CoreUi.string.allow)) { _, _ -> onConfirm() }
                .setNegativeButton(safeActivity.getString(CoreUi.string.cancel), null)
                .show()
        }
    }
}