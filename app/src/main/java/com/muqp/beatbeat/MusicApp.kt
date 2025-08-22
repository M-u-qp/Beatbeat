package com.muqp.beatbeat

import android.app.Application
import com.muqp.beatbeat.di.DaggerMusicAppComponent
import com.muqp.beatbeat.di.MusicAppComponent

class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        musicAppComponent = DaggerMusicAppComponent.builder()
            .application(this)
            .build()
    }

    companion object {
        lateinit var musicAppComponent: MusicAppComponent
            private set
    }
}