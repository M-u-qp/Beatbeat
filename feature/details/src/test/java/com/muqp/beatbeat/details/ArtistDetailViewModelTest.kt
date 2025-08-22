package com.muqp.beatbeat.details

import com.muqp.beatbeat.details.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteArtistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetArtistByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertArtistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertTrackUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetAllArtistAlbumsUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetPopularArtistTracksUseCase
import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.presentation.screen.artist_details.ArtistDetailViewModel
import com.muqp.beatbeat.details.presentation.screen.artist_details.ArtistDetailsScreenState
import com.muqp.beatbeat.exo_player.PlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ArtistDetailViewModelTest {
    private lateinit var getAllArtistAlbumsUseCase: GetAllArtistAlbumsUseCase
    private lateinit var getArtistByIdUseCase: GetArtistByIdUseCase
    private lateinit var insertArtistUseCase: InsertArtistUseCase
    private lateinit var deleteArtistUseCase: DeleteArtistUseCase
    private lateinit var getPopularArtistTracksUseCase: GetPopularArtistTracksUseCase
    private lateinit var insertTrackUseCase: InsertTrackUseCase
    private lateinit var deleteTrackUseCase: DeleteTrackUseCase
    private lateinit var getTrackByIdUseCase: GetTrackByIdUseCase
    private lateinit var playerManager: PlayerManager
    private lateinit var getPlaylistsUseCase: GetPlaylistsUseCase
    private lateinit var addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ArtistDetailViewModel

    @Before
    fun setup() {
        getAllArtistAlbumsUseCase = mock()
        getArtistByIdUseCase = mock()
        insertArtistUseCase = mock()
        deleteArtistUseCase = mock()
        getPopularArtistTracksUseCase = mock()
        insertTrackUseCase = mock()
        deleteTrackUseCase = mock()
        getTrackByIdUseCase = mock()
        playerManager = mock()
        getPlaylistsUseCase = mock()
        addTrackToPlaylistUseCase = mock()

        Dispatchers.setMain(testDispatcher)

        viewModel = ArtistDetailViewModel(
            getAllArtistAlbumsUseCase,
            getArtistByIdUseCase,
            insertArtistUseCase,
            deleteArtistUseCase,
            getPopularArtistTracksUseCase,
            insertTrackUseCase,
            deleteTrackUseCase,
            getTrackByIdUseCase,
            playerManager,
            getPlaylistsUseCase,
            addTrackToPlaylistUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getArtistDetailsData should set Success state with correct data`() = runTest {
        val artistId = 1
        val mockTracks = listOf(
            ItemTrackUI(
                id = "1",
                name = "test track",
                duration = "test duration",
                audio = "test audio",
                audioDownload = "test audio download",
                audioDownloadAllowed = false,
                isFavorite = false
            )
        )
        val mockAlbums = AlbumUI(
            results = listOf(
                ItemAlbumUI(
                    id = "1",
                    name = "test album",
                    releaseDate = "test release date",
                    artistId = "1",
                    artistName = "test name",
                    image = "test image",
                    tracks = mockTracks,
                    isFavorite = false
                )
            )
        )

        `when`(getAllArtistAlbumsUseCase.invoke(artistId.toString())).thenReturn(mockAlbums)
        `when`(getPopularArtistTracksUseCase.invoke(artistId)).thenReturn(mockTracks)

        viewModel.getArtistDetailsData(artistId)

        val state = viewModel.state.value
        assertTrue(state is ArtistDetailsScreenState.Success)
        val successState = state as ArtistDetailsScreenState.Success
        assertEquals(mockAlbums, successState.albums)
        assertEquals(mockTracks, successState.popularTracks)
    }

    @Test
    fun `getArtistDetailsData should set Error state when exception occurs`() = runTest {
        val artistId = 1
        val errorMessage = "Network error"

        `when`(getAllArtistAlbumsUseCase.invoke(artistId.toString()))
            .thenThrow(RuntimeException(errorMessage))
        `when`(getPopularArtistTracksUseCase.invoke(artistId))
            .thenReturn(emptyList())

        val states = mutableListOf<ArtistDetailsScreenState?>()
        val job = viewModel.state
            .onEach { states.add(it) }
            .launchIn(this)

        viewModel.getArtistDetailsData(artistId)
        advanceUntilIdle()

        assertTrue(states.any { it is ArtistDetailsScreenState.Error })
        val errorState =
            states.last { it is ArtistDetailsScreenState.Error } as ArtistDetailsScreenState.Error
        assertEquals(errorMessage, errorState.message)
        job.cancel()
    }

    @Test
    fun `initial state should be null`() = runTest {
        assertNull(viewModel.state.value)
    }
}