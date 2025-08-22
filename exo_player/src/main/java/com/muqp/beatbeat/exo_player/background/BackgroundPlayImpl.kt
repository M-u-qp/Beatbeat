package com.muqp.beatbeat.exo_player.background

import android.app.Application
import android.os.Build
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.ACTION_SEEK_TO
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.CURRENT_ID
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.CURRENT_IMAGE_URL
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.CURRENT_INDEX
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.CURRENT_TITLE
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.EXO_PLAYER_POSITION
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.SEEK_POSITION
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.TRACKS_DATA
import com.muqp.beatbeat.exo_player.background.BackgroundService.Companion.URL
import com.muqp.beatbeat.exo_player.model.Track
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class BackgroundPlayImpl @Inject constructor(
    private val context: Application
) : BackgroundPlay {
    override fun start(
        url: String,
        startPosition: Long,
        title: String,
        imageUrl: String?,
        trackId: String
    ) {
        val playIntent = BackgroundService.createIntent(
            context,
            BackgroundService.ACTION_PLAY,
            url
        ).apply {
            putExtra(EXO_PLAYER_POSITION, startPosition)
            putExtra(CURRENT_TITLE, title)
            putExtra(CURRENT_IMAGE_URL, imageUrl)
            putExtra(CURRENT_ID, trackId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(playIntent)
        } else {
            context.startService(playIntent)
        }
    }

    override fun startPlaylist(
        tracks: List<Track>,
        startIndex: Int
    ) {
        val playlistData = JSONArray().apply {
            tracks.forEach { track ->
                put(JSONObject().apply {
                    put(CURRENT_ID, track.id)
                    put(URL, track.audioUrl)
                    put(CURRENT_TITLE, track.title)
                    put(CURRENT_IMAGE_URL, track.imageUrl)
                })
            }
        }.toString()

        val playIntent = BackgroundService.createIntent(
            context,
            BackgroundService.ACTION_PLAY
        ).apply {
            putExtra(CURRENT_INDEX, startIndex)
            putExtra(TRACKS_DATA, playlistData)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(playIntent)
        } else {
            context.startService(playIntent)
        }
    }

    override fun stop() {
        context.startService(
            BackgroundService.createIntent(context, BackgroundService.ACTION_STOP)
        )
    }

    override fun pause() {
        context.startService(
            BackgroundService.createIntent(context, BackgroundService.ACTION_PAUSE)
        )
    }

    override fun resume(position: Long) {
        context.startService(
            BackgroundService.createIntent(context, BackgroundService.ACTION_RESUME)
                .apply {
                    putExtra(EXO_PLAYER_POSITION, position)
                }
        )
    }

    override fun next(): Boolean {
        context.startService(
            BackgroundService.createIntent(context, BackgroundService.ACTION_NEXT)
        )
        return true
    }

    override fun previous(): Boolean {
        context.startService(
            BackgroundService.createIntent(
                context,
                BackgroundService.ACTION_PREVIOUS
            )
        )
        return true
    }

    override fun seekTo(positionMs: Long) {
        val seekIntent = BackgroundService.createIntent(
            context,
            ACTION_SEEK_TO
        ).apply {
            putExtra(SEEK_POSITION, positionMs)
        }
        context.startService(seekIntent)
    }
}