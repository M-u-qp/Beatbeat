package com.muqp.core_ui.common

import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.core.content.ContextCompat

object ToggleFavorite {
    enum class FavoriteResultType {
        ADDED, REMOVED
    }

    enum class FavoriteType {
        TRACK, ALBUM, ARTIST
    }

    data class FavoriteResult(
        val favoriteResultType: FavoriteResultType,
        val favoriteType: FavoriteType
    )

    suspend fun <T> toggleFavorite(
        item: T,
        isFavorite: suspend (T) -> Boolean,
        removeFromFavorites: suspend (T) -> Unit,
        addToFavorites: suspend (T) -> Unit,
        onResultClicked: (Boolean) -> Unit,
        favoriteType: FavoriteType? = null
    ): FavoriteResult? {
        return if (isFavorite(item)) {
            removeFromFavorites(item)
            onResultClicked(false)
            favoriteType?.let {
                FavoriteResult(FavoriteResultType.REMOVED, favoriteType)
            }
        } else {
            addToFavorites(item)
            onResultClicked(true)
            favoriteType?.let {
                FavoriteResult(FavoriteResultType.ADDED, favoriteType)
            }
        }
    }

    fun updateColorFavoriteIcon(
        imageView: ImageView,
        isFavorite: Boolean,
        trueColor: Int,
        falseColor: Int
    ) {
        imageView.setColorFilter(
            ContextCompat.getColor(
                imageView.context,
                if (isFavorite) {
                    trueColor
                } else {
                    falseColor
                }
            ),
            PorterDuff.Mode.SRC_IN
        )
    }
}