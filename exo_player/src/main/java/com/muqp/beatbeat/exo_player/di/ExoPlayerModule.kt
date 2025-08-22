package com.muqp.beatbeat.exo_player.di

import android.app.Application
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.beatbeat.exo_player.background.BackgroundPlay
import com.muqp.beatbeat.exo_player.background.BackgroundPlayImpl
import com.muqp.beatbeat.exo_player.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ExoPlayerModule {
    @Provides
    @Singleton
    fun provideNotificationHelper(context: Application): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideBackgroundPlay(context: Application): BackgroundPlay = BackgroundPlayImpl(context)

    @Provides
    @Singleton
    fun providePlayerManager(
        backgroundPlay: BackgroundPlay,
        context: Application
    ): PlayerManager = PlayerManager(backgroundPlay, context)
}