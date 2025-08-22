package com.muqp.core_network.api

import com.muqp.core_network.model.AlbumDetailsResponse
import com.muqp.core_network.model.AllArtistAlbumsResponse
import com.muqp.core_network.model.MusicFeedsResponse
import com.muqp.core_network.model.SearchResultToAlbumResponse
import com.muqp.core_network.model.SearchResultToArtistResponse
import com.muqp.core_network.model.SearchResultToTrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoApi {
    companion object {
        const val BASE_URL = "https://api.jamendo.com/"
    }

    @GET("v3.0/feeds/")
    suspend fun getMusicFeeds(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("offset") offset: Int,
        @Query("limit") limit: String,
        @Query("order") order: String = "date_end",
        @Query("type") type: List<String> = listOf(
            "album",
            "artist",
            "playlist",
            "track",
            "news"
        )
    ): Response<MusicFeedsResponse>

    @GET("v3.0/tracks/")
    suspend fun getSearchResultToTrack(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: String? = null,
        @Query("namesearch") search: String? = null,
        @Query("artist_id") artistId: Int? = null,
        @Query("order") order: String? = null,
        @Query("tags") tags: String? = null,
        @Query("acousticelectric") acoEle: String? = null,
        @Query("gender") gender: String? = null,
        @Query("speed") speed: List<String>? = null
    ): Response<SearchResultToTrackResponse>

    @GET("v3.0/artists/")
    suspend fun getSearchResultToArtist(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("offset") offset: Int,
        @Query("limit") limit: String,
        @Query("namesearch") nameSearch: String
    ): Response<SearchResultToArtistResponse>

    @GET("v3.0/albums/")
    suspend fun getSearchResultToAlbum(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("offset") offset: Int,
        @Query("limit") limit: String,
        @Query("namesearch") nameSearch: String
    ): Response<SearchResultToAlbumResponse>

    @GET("v3.0/albums/tracks/")
    suspend fun getAlbumDetails(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: String = "1",
        @Query("id") id: Int
    ): Response<AlbumDetailsResponse>

    @GET("v3.0/albums/")
    suspend fun getAllArtistAlbums(
        @Query("client_id") clientId: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: String = "all",
        @Query("artist_id") artistId: String
    ): Response<AllArtistAlbumsResponse>
}