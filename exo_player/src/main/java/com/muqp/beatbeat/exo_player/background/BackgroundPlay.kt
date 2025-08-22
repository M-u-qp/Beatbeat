package com.muqp.beatbeat.exo_player.background

import com.muqp.beatbeat.exo_player.model.Track

interface BackgroundPlay {
    fun start(
        url: String,
        startPosition: Long = 0L,
        title: String,
        imageUrl: String?,
        trackId: String
    )

    fun startPlaylist(tracks: List<Track>, startIndex: Int)
    fun stop()
    fun pause()
    fun resume(position: Long)
    fun next(): Boolean
    fun previous(): Boolean
    fun seekTo(positionMs: Long)
}