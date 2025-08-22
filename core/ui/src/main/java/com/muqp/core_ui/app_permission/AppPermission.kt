package com.muqp.core_ui.app_permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.muqp.beatbeat.ui.R as CoreUi

sealed class AppPermission(
    val permission: String,
    val rationaleTitle: Int,
    val rationaleMessage: Int
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data object Notification : AppPermission(
        Manifest.permission.POST_NOTIFICATIONS,
        CoreUi.string.permission_notifications,
        CoreUi.string.notification_player_management
    )

    data object Storage : AppPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        },
        CoreUi.string.permission_storage,
        CoreUi.string.storage_permission_rationale
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data object MediaAudio : AppPermission(
        Manifest.permission.READ_MEDIA_AUDIO,
        CoreUi.string.permission_media_audio,
        CoreUi.string.media_audio_permission_rationale
    )
}

fun Context.hasPermission(permission: AppPermission): Boolean {
    return when {
        permission == AppPermission.Storage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            Environment.isExternalStorageManager()
        }

        permission == AppPermission.Storage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        else -> {
            ContextCompat.checkSelfPermission(
                this,
                permission.permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}