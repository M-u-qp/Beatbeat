package com.muqp.core_ui.downloader.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.muqp.core_ui.downloader.CreateFolderIntent.createViewFilePendingIntent
import com.muqp.core_ui.downloader.CreateFolderIntent.createViewFolderIntent
import java.lang.ref.WeakReference

class NotificationDownloadHelper(context: Context) {
    private val contextRef = WeakReference(context)
    val notificationManager by lazy {
        contextRef.get()?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    companion object {
        const val DOWNLOAD_CHANNEL_ID = "download_channel"
        const val DOWNLOAD_CHANNEL_NAME = "channelName"
        const val DOWNLOAD_CHANNEL_DESCRIPTION = "channelDescription"
        const val DOWNLOAD_NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                DOWNLOAD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = DOWNLOAD_CHANNEL_DESCRIPTION
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        @DrawableRes icon: Int,
        title: String,
        content: String,
        notificationId: Int = DOWNLOAD_NOTIFICATION_ID,
        autoCancel: Boolean = false,
        fileUri: Uri? = null
    ) {
        val context = contextRef.get() ?: return
        val builder = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(autoCancel)

        fileUri?.let { uri ->
            val folderIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createViewFolderIntent(context, uri)
            } else {
                createViewFilePendingIntent(context, uri)
            }

            folderIntent?.let { pendingIntent ->
                builder.setContentIntent(pendingIntent)
            }
        }
        notificationManager?.notify(notificationId, builder.build())
    }
}