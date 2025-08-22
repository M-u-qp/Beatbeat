package com.muqp.beatbeat.exo_player.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.muqp.beatbeat.exo_player.R
import com.muqp.beatbeat.exo_player.background.BackgroundService
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.ACTION_NEXT
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.ACTION_PAUSE
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.ACTION_PREVIOUS
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.ACTION_RESUME
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.ACTION_STOP
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    private val context: Application
) {
    private var currentTrackUrl: String? = null
    private var notificationManager: NotificationManager? = null
    private var currentTrackTitle: String = ""
    private var currentImageUrl: String? = null

    fun setCurrentTrackUrl(
        url: String?,
        title: String = "",
        imageUrl: String? = null
    ) {
        currentTrackUrl = url
        currentTrackTitle = title
        currentImageUrl = imageUrl
    }

    private val emptyIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            context,
            0,
            Intent(),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getNotificationManager(): NotificationManager {
        return notificationManager ?: context.getSystemService(Context.NOTIFICATION_SERVICE).let {
            it as NotificationManager
        }.also { notificationManager = it }
    }

    init {
        createNotificationChannel()
    }

    fun getNotification(isPlaying: Boolean): Notification {
        val notificationLayout =
            RemoteViews(context.packageName, R.layout.notification_player_controller)

        notificationLayout.setTextViewText(
            R.id.tv_title,
            currentTrackTitle.ifEmpty { context.getString(R.string.unknown_track) }
        )

        notificationLayout.setImageViewResource(
            R.id.btn_play_pause,
            if (isPlaying) {
                R.drawable.icons8_pause
            } else {
                R.drawable.icons8_play_v2
            }
        )

        notificationLayout.setOnClickPendingIntent(
            R.id.btn_previous,
            createPendingIntent(ACTION_PREVIOUS)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btn_play_pause,
            createPendingIntent(if (isPlaying) ACTION_PAUSE else ACTION_RESUME)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btn_next,
            createPendingIntent(ACTION_NEXT)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btn_stop,
            createPendingIntent(ACTION_STOP)
        )

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.icons8_beatbeat)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle(currentTrackTitle.ifEmpty { context.getString(R.string.unknown_track) })
            .setContentIntent(emptyIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCustomContentView(notificationLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, BackgroundService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun updateNotification(isPlaying: Boolean) {
        getNotificationManager().notify(
            NOTIFICATION_ID,
            getNotification(isPlaying)
        )
    }

    fun removeNotification() {
        getNotificationManager().cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = NOTIFICATION_CHANNEL_DESCRIPTION
                setShowBadge(false)
                setSound(null, null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            getNotificationManager().createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "musicChannel"
        private const val NOTIFICATION_CHANNEL_NAME = "musicPlayback"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "musicPlaybackControls"
        const val NOTIFICATION_ID = 1001
    }
}