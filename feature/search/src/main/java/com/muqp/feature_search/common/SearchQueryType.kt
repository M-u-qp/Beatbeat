package com.muqp.feature_search.common

enum class SearchQueryType(val position: Int) {
    TRACKS(0),
    ARTISTS(1),
    ALBUMS(2);

    companion object {
        fun fromPosition(position: Int?): SearchQueryType {
            return entries.firstOrNull { it.position == position } ?: TRACKS
        }
    }
}