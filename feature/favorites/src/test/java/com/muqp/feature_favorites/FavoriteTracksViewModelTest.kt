package com.muqp.feature_favorites

import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.feature_favorites.domain.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeleteTrackUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllTracksUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistsUseCase
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistUI
import com.muqp.feature_favorites.presentation.screen.favorite_tracks.FavoriteTracksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteTracksViewModelTest {
    private lateinit var getAllTracksUseCase: GetAllTracksUseCase
    private lateinit var deleteTrackUseCase: DeleteTrackUseCase
    private lateinit var playerManager: PlayerManager
    private lateinit var getPlaylistsUseCase: GetPlaylistsUseCase
    private lateinit var addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase

    private lateinit var viewModel: FavoriteTracksViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        getAllTracksUseCase = mock()
        deleteTrackUseCase = mock()
        playerManager = mock()
        getPlaylistsUseCase = mock()
        addTrackToPlaylistUseCase = mock()

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllTracks should emit tracks from use case`() = runTest {
        val mockTracks = listOf(
            ItemTrack(
                id = "1",
                name = "test name",
                duration = 0,
                artistId = "1",
                artistName = "test name",
                albumName = "test name",
                albumId = "1",
                releaseDate = "test release date",
                albumImage = "test album image",
                audio = "test audio",
                audioDownload = "test audio download",
                shareUrl = "test share url",
                image = "test image",
                audioDownloadAllowed = false,
                isFavorite = false
            )
        )

        `when`(getAllTracksUseCase.invoke()).thenReturn(flowOf(mockTracks))

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        val result = viewModel.getAllTracks.first()

        assertEquals(mockTracks, result)
    }

    @Test
    fun `addTrackToPlaylist should call use case`() = runTest {
        val playlistId = 1L
        val trackId = "1"

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        viewModel.addTrackToPlaylist(playlistId, trackId)

        verify(addTrackToPlaylistUseCase).invoke(playlistId, trackId)
    }

    @Test
    fun `getPlaylists should update playlists state`() = runTest {
        val mockPlaylists = listOf(
            PlaylistUI(
                id = 1L,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        viewModel.getPlaylists()
        advanceUntilIdle()

        assertEquals(mockPlaylists, viewModel.playlists.value)
    }

    @Test
    fun `loadFavoriteTracksDataRecycler should map tracks correctly`() = runTest {
        val mockTracks = listOf(
            ItemTrack(
                id = "1",
                name = "test name",
                duration = 0,
                artistId = "1",
                artistName = "test name",
                albumName = "test name",
                albumId = "1",
                releaseDate = "test release date",
                albumImage = "test album image",
                audio = "test audio",
                audioDownload = "test audio download",
                shareUrl = "test share url",
                image = "test image",
                audioDownloadAllowed = false,
                isFavorite = false
            )
        )

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        val result = viewModel.loadFavoriteTracksDataRecycler(
            mockTracks,
            onClickTrack = {},
            onMenuClickListener = { _, _ -> }
        )

        assertEquals(1, result.size)
        assertEquals("1", result[0].getId())
    }

    @Test
    fun `onTrackClicked should call player manager`() = runTest {
        val mockTrack = ItemTrack(
            id = "1",
            name = "test name",
            duration = 0,
            artistId = "1",
            artistName = "test name",
            albumName = "test name",
            albumId = "1",
            releaseDate = "test release date",
            albumImage = "test album image",
            audio = "test audio",
            audioDownload = "test audio download",
            shareUrl = "test share url",
            image = "test image",
            audioDownloadAllowed = false,
            isFavorite = false
        )

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        viewModel.onTrackClicked(mockTrack)

        verify(playerManager).playTrack(
            trackId = mockTrack.id,
            audioUrl = mockTrack.audio,
            title = mockTrack.name,
            imageUrl = mockTrack.image
        )
    }

    @Test
    fun `deleteTrack should call use case`() = runTest {
        val mockTrack = ItemTrack(
            id = "1",
            name = "test name",
            duration = 0,
            artistId = "1",
            artistName = "test name",
            albumName = "test name",
            albumId = "1",
            releaseDate = "test release date",
            albumImage = "test album image",
            audio = "test audio",
            audioDownload = "test audio download",
            shareUrl = "test share url",
            image = "test image",
            audioDownloadAllowed = false,
            isFavorite = false
        )

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        viewModel.deleteTrack(mockTrack)

        verify(deleteTrackUseCase).invoke(mockTrack)
    }

    @Test
    fun `error handling should update error message`() = runTest {
        val errorMessage = "test error"

        `when`(getPlaylistsUseCase.invoke()).thenThrow(RuntimeException(errorMessage))

        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        viewModel.getPlaylists()
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.errorMessage.value)
    }

    @Test
    fun `resetErrorMessage should clear error message`() = runTest {
        viewModel = FavoriteTracksViewModel(
            getAllTracksUseCase,
            deleteTrackUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )

        viewModel.resetErrorMessage()

        assertNull(viewModel.errorMessage.value)
    }
}