package com.muqp.feature_favorites.presentation.screen.favorite_albums

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.feature_favorites.domain.use_cases.DeleteAlbumUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllAlbumsUseCase
import com.muqp.feature_favorites.model.ItemAlbum
import com.muqp.feature_favorites.presentation.screen.adapter.FavoriteAlbumsDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

class FavoriteAlbumsViewModel @Inject constructor(
    getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase
) : ViewModel() {

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message ?: "Unknown error"
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    val getAllAlbums = getAllAlbumsUseCase()
        .stateIn(
            scope = viewModelScope + coroutineEH,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadFavoriteAlbumsDataRecycler(
        listAlbums: List<ItemAlbum>,
        onAlbumClick: (Int) -> Unit,
        onMenuClickListener: ((View, ItemAlbum) -> Unit)
    ): List<FavoriteAlbumsDataRecycler> {
        return listAlbums.map { itemAlbum ->
            FavoriteAlbumsDataRecycler(
                item = itemAlbum,
                onAlbumClick = { onAlbumClick(itemAlbum.id.toInt()) },
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    suspend fun deleteAlbum(album: ItemAlbum) {
        deleteAlbumUseCase.invoke(album)
    }
}