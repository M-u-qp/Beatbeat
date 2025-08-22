package com.muqp.feature_favorites.presentation.screen.favorite_artists

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.feature_favorites.domain.use_cases.DeleteArtistUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllArtistsUseCase
import com.muqp.feature_favorites.model.ItemArtist
import com.muqp.feature_favorites.presentation.screen.adapter.FavoriteArtistsDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

class FavoriteArtistsViewModel @Inject constructor(
    getAllArtistsUseCase: GetAllArtistsUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase
) : ViewModel() {

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message ?: "Unknown error"
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    val getAllArtists = getAllArtistsUseCase()
        .stateIn(
            scope = viewModelScope + coroutineEH,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadFavoriteArtistsDataRecycler(
        listArtists: List<ItemArtist>,
        onArtistClick: (ItemArtist) -> Unit,
        onMenuClickListener: ((View, ItemArtist) -> Unit)
    ): List<FavoriteArtistsDataRecycler> {
        return listArtists.map { item ->
            FavoriteArtistsDataRecycler(
                item = item,
                onArtistClick = { onArtistClick(item) },
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    suspend fun deleteArtist(artist: ItemArtist) {
        deleteArtistUseCase.invoke(artist)
    }
}