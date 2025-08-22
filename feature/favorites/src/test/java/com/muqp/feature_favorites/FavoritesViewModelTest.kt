package com.muqp.feature_favorites

import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.feature_favorites.domain.use_cases.CreatePlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeletePlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistWithTracksUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistsUseCase
import com.muqp.feature_favorites.domain.use_cases.GetTrackCountForPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.RemoveTrackFromPlaylistUseCase
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistUI
import com.muqp.feature_favorites.presentation.screen.favorites.FavoritesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.anyList
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    private lateinit var createPlaylistUseCase: CreatePlaylistUseCase
    private lateinit var getPlaylistsUseCase: GetPlaylistsUseCase
    private lateinit var getPlaylistWithTracksUseCase: GetPlaylistWithTracksUseCase
    private lateinit var deletePlaylistUseCase: DeletePlaylistUseCase
    private lateinit var removeTrackFromPlaylistUseCase: RemoveTrackFromPlaylistUseCase
    private lateinit var getTrackCountForPlaylistUseCase: GetTrackCountForPlaylistUseCase
    private lateinit var playerManager: PlayerManager

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        createPlaylistUseCase = mock()
        getPlaylistsUseCase = mock()
        getPlaylistWithTracksUseCase = mock()
        deletePlaylistUseCase = mock()
        removeTrackFromPlaylistUseCase = mock()
        getTrackCountForPlaylistUseCase = mock()
        playerManager = mock()

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `init should load playlists`() = runTest {
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
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        advanceUntilIdle()

        assertEquals(mockPlaylists, viewModel.playlists.value)
    }

    @Test
    fun `getPlaylistTracks should update tracks state`() = runTest {
        val playlistId = 1L
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

        `when`(getPlaylistsUseCase.invoke()).thenReturn(emptyList())
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)
        `when`(getPlaylistWithTracksUseCase.invoke(playlistId))
            .thenReturn(
                Pair(
                    PlaylistUI(
                        id = playlistId,
                        name = "test name",
                        description = "test description",
                        createdAt = null,
                        coverImage = "test cover image",
                        trackCount = 0
                    ),
                    mockTracks
                )
            )

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.getPlaylistTracks(playlistId)

        assertEquals(mockTracks, viewModel.tracks.value)
    }

    @Test
    fun `deletePlaylist should refresh playlists`() = runTest {
        val playlistId = 1L
        val mockPlaylists = listOf(
            PlaylistUI(
                id = playlistId,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.deletePlaylist(playlistId)

        verify(deletePlaylistUseCase).invoke(playlistId)
        assertEquals(mockPlaylists, viewModel.playlists.value)
    }

    @Test
    fun `removeTrackFromPlaylist should refresh tracks and playlists`() = runTest {
        val playlistId = 1L
        val trackId = "track1"
        val mockTracks = listOf<ItemTrack>()
        val mockPlaylists = listOf(
            PlaylistUI(
                id = playlistId,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        `when`(getPlaylistWithTracksUseCase.invoke(playlistId))
            .thenReturn(
                Pair(
                    PlaylistUI(
                        id = playlistId,
                        name = "test name",
                        description = "test description",
                        createdAt = null,
                        coverImage = "test cover image",
                        trackCount = 0
                    ), mockTracks
                )
            )
        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.removeTrackFromPlaylist(playlistId, trackId)

        verify(removeTrackFromPlaylistUseCase).invoke(playlistId, trackId)
        assertEquals(mockTracks, viewModel.tracks.value)
        assertEquals(mockPlaylists, viewModel.playlists.value)
    }

    @Test
    fun `createPlaylist should refresh playlists`() = runTest {
        val name = "New Playlist"
        val description = "Description"
        val mockPlaylists = listOf(
            PlaylistUI(
                id = 1L,
                name = name,
                description = description,
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.createPlaylist(name, description)

        verify(createPlaylistUseCase).invoke(name, description)
        assertEquals(mockPlaylists, viewModel.playlists.value)
    }

    @Test
    fun `onTrackClicked should call playerManager`() = runTest {
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

        val playlistId = 1L
        val mockPlaylists = listOf(
            PlaylistUI(
                id = playlistId,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        `when`(getPlaylistWithTracksUseCase.invoke(playlistId))
            .thenReturn(
                Pair(
                    PlaylistUI(
                        id = playlistId,
                        name = "test name",
                        description = "test description",
                        createdAt = null,
                        coverImage = "test cover image",
                        trackCount = 0
                    ), mockTracks
                )
            )
        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.onTrackClicked(mockTracks[0])

        verify(playerManager).playTrack(
            trackId = mockTracks[0].id,
            audioUrl = mockTracks[0].audio,
            title = mockTracks[0].name,
            imageUrl = mockTracks[0].image
        )
    }

    @Test
    fun `onPlaylistClicked should set playlist when tracks exist`() = runTest {
        val playlistId = 1L
        val mockPlaylists = listOf(
            PlaylistUI(
                id = playlistId,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )
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

        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(1)
        `when`(getPlaylistWithTracksUseCase.invoke(playlistId))
            .thenReturn(
                Pair(
                    mockPlaylists[0],
                    mockTracks
                )
            )

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.onPlaylistClicked(playlistId)

        verify(playerManager).setPlaylist(anyList(), eq(0))
    }

    @Test
    fun `onPlaylistClicked should not set playlist when no tracks`() = runTest {
        val playlistId = 1L
        val mockPlaylists = listOf(
            PlaylistUI(
                id = playlistId,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        `when`(getPlaylistsUseCase.invoke()).thenReturn(mockPlaylists)
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        `when`(getPlaylistWithTracksUseCase.invoke(playlistId))
            .thenReturn(
                Pair(
                    mockPlaylists[0],
                    emptyList()
                )
            )

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.onPlaylistClicked(playlistId)

        verify(playerManager, never()).setPlaylist(anyList(), eq(0))
    }

    @Test
    fun `error handling should update errorMessage`() = runTest {
        val errorMessage = "test error"

        `when`(getPlaylistsUseCase.invoke())
            .thenThrow(RuntimeException(errorMessage))
        `when`(getTrackCountForPlaylistUseCase.invoke(any())).thenReturn(0)

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        advanceUntilIdle()
        assertEquals(errorMessage, viewModel.errorMessage.value)
    }

    @Test
    fun `resetErrorMessage should clear error`() = runTest {

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        viewModel.resetErrorMessage()
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `loadPlaylistDataRecycler should return correct items`() = runTest {
        val playlists = listOf(
            PlaylistUI(
                id = 1L,
                name = "test name",
                description = "test description",
                createdAt = null,
                coverImage = "test cover image",
                trackCount = 0
            )
        )

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        val result = viewModel.loadPlaylistDataRecycler(
            playlists = playlists,
            onDeletePlaylist = {},
            onPlaylistClicked = {},
            onBindTracks = { _, _ -> }
        )

        assertEquals(1, result.size)
        assertEquals(playlists[0].id, result[0].getId().toLong())
    }

    @Test
    fun `loadPlaylistTracksDataRecycler should return correct items`() = runTest {
        val tracks = listOf(
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

        viewModel = FavoritesViewModel(
            createPlaylistUseCase,
            getPlaylistsUseCase,
            getPlaylistWithTracksUseCase,
            deletePlaylistUseCase,
            removeTrackFromPlaylistUseCase,
            getTrackCountForPlaylistUseCase,
            playerManager
        )

        val result = viewModel.loadPlaylistTracksDataRecycler(
            tracks = tracks,
            onClickTrack = {},
            onMenuClickListener = { _, _ -> }
        )

        assertEquals(1, result.size)
        assertEquals(tracks[0].id, result[0].getId())
    }
}