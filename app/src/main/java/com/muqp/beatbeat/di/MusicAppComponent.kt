package com.muqp.beatbeat.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.muqp.beatbeat.dependencies_impl.GetViewModelFactoryImpl
import com.muqp.beatbeat.details.di.LocalDetailsModule
import com.muqp.beatbeat.details.di.RemoteDetailsModule
import com.muqp.beatbeat.exo_player.di.ExoPlayerModule
import com.muqp.beatbeat.ui.MainActivity
import com.muqp.beatbeat.ui.MainFragment
import com.muqp.core_database.di.DatabaseModule
import com.muqp.core_network.di.NetworkModule
import com.muqp.core_utils.has_dependencies.GetViewModelFactory
import com.muqp.feature_auth.di.AuthModule
import com.muqp.feature_favorites.di.FavoritesModule
import com.muqp.feature_home.di.HomeModule
import com.muqp.feature_listen.di.LocalListenModule
import com.muqp.feature_listen.di.RemoteListenModule
import com.muqp.feature_search.di.LocalSearchModule
import com.muqp.feature_search.di.RemoteSearchModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        ViewModelModule::class,
        NetworkModule::class,
        ExoPlayerModule::class,
        AppModule::class
    ]
)
interface MusicAppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)

    @Component.Builder
    interface Builder {
        fun build(): MusicAppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

@Module(
    includes = [
        AuthModule::class,
        HomeModule::class,
        RemoteSearchModule::class,
        LocalSearchModule::class,
        RemoteDetailsModule::class,
        LocalDetailsModule::class,
        FavoritesModule::class,
        LocalListenModule::class,
        RemoteListenModule::class
    ]
)
class AppModule {
    @Provides
    @Singleton
    fun provideGetViewModelFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): GetViewModelFactory {
        return GetViewModelFactoryImpl(viewModelFactory)
    }
}


