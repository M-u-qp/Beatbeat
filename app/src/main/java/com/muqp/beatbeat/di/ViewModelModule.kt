package com.muqp.beatbeat.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muqp.beatbeat.details.presentation.screen.album_details.AlbumDetailViewModel
import com.muqp.beatbeat.details.presentation.screen.all_tracks.AllTracksViewModel
import com.muqp.beatbeat.details.presentation.screen.artist_details.ArtistDetailViewModel
import com.muqp.feature_auth.presentation.screen.AuthViewModel
import com.muqp.feature_favorites.presentation.screen.favorite_albums.FavoriteAlbumsViewModel
import com.muqp.feature_favorites.presentation.screen.favorite_artists.FavoriteArtistsViewModel
import com.muqp.feature_favorites.presentation.screen.favorite_tracks.FavoriteTracksViewModel
import com.muqp.feature_favorites.presentation.screen.favorites.FavoritesViewModel
import com.muqp.feature_home.presentation.screen.HomeViewModel
import com.muqp.feature_listen.presentation.screen.ListenViewModel
import com.muqp.feature_search.presentation.screen.SearchViewModel
import com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_albums.FoundAlbumsViewModel
import com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_artists.FoundArtistsViewModel
import com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_tracks.FoundTracksViewModel
import com.muqp.feature_search.presentation.screen.genre_search.GenreSearchViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(view: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(view: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(view: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoundTracksViewModel::class)
    abstract fun bindFoundTracksViewModel(view: FoundTracksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoundAlbumsViewModel::class)
    abstract fun bindFoundAlbumsViewModel(view: FoundAlbumsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FoundArtistsViewModel::class)
    abstract fun bindFoundArtistsViewModel(view: FoundArtistsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GenreSearchViewModel::class)
    abstract fun bindGenreSearchViewModel(view: GenreSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AlbumDetailViewModel::class)
    abstract fun bindAlbumDetailsViewModel(view: AlbumDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistDetailViewModel::class)
    abstract fun bindArtistDetailsViewModel(view: ArtistDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AllTracksViewModel::class)
    abstract fun bindAllTracksViewModel(view: AllTracksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteTracksViewModel::class)
    abstract fun bindFavoriteTracksViewModel(view: FavoriteTracksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteAlbumsViewModel::class)
    abstract fun bindFavoriteAlbumsViewModel(view: FavoriteAlbumsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteArtistsViewModel::class)
    abstract fun bindFavoriteArtistsViewModel(view: FavoriteArtistsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListenViewModel::class)
    abstract fun bindListenViewModel(view: ListenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    abstract fun bindFavoritesViewModel(view: FavoritesViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Singleton
class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull()
            ?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        return creator.get() as T
    }
}