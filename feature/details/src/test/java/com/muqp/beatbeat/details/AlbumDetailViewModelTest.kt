package com.muqp.beatbeat.details

import com.muqp.beatbeat.details.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteAlbumUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetAlbumByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertAlbumUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertTrackUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetAlbumDetailsUseCase
import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.presentation.screen.album_details.AlbumDetailViewModel
import com.muqp.beatbeat.details.presentation.screen.album_details.AlbumDetailsScreenState
import com.muqp.beatbeat.exo_player.PlayerManager
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {
    private lateinit var getAlbumDetailsUseCase: GetAlbumDetailsUseCase
    private lateinit var insertTrackUseCase: InsertTrackUseCase
    private lateinit var deleteTrackUseCase: DeleteTrackUseCase
    private lateinit var getTrackByIdUseCase: GetTrackByIdUseCase
    private lateinit var insertAlbumUseCase: InsertAlbumUseCase
    private lateinit var deleteAlbumUseCase: DeleteAlbumUseCase
    private lateinit var getAlbumByIdUseCase: GetAlbumByIdUseCase
    private lateinit var playerManager: PlayerManager
    private lateinit var getPlaylistsUseCase: GetPlaylistsUseCase
    private lateinit var addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AlbumDetailViewModel

    @Before
    fun setup() {
        getAlbumDetailsUseCase = mock()
        insertTrackUseCase = mock()
        deleteTrackUseCase = mock()
        getTrackByIdUseCase = mock()
        insertAlbumUseCase = mock()
        deleteAlbumUseCase = mock()
        getAlbumByIdUseCase = mock()
        playerManager = mock()
        getPlaylistsUseCase = mock()
        addTrackToPlaylistUseCase = mock()

        Dispatchers.setMain(testDispatcher)

        viewModel = AlbumDetailViewModel(
            getAlbumDetailsUseCase,
            insertTrackUseCase,
            deleteTrackUseCase,
            getTrackByIdUseCase,
            insertAlbumUseCase,
            deleteAlbumUseCase,
            getAlbumByIdUseCase,
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
    fun `loadAlbumById should set Success state when album loaded`() = runTest {
        val albumId = 1
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

        `when`(getAlbumDetailsUseCase.invoke(albumId))
            .thenReturn(mockAlbums)

        viewModel.loadAlbumById(albumId)

        val state = viewModel.state.value
        assertTrue(state is AlbumDetailsScreenState.Success)
    }

    @Test
    fun `toggleAlbumFavorite should update favoriteSideEffect when toggled`() = runTest {
        val mockTracks = ItemTrackUI(
            id = "1",
            name = "test track",
            duration = "test duration",
            audio = "test audio",
            audioDownload = "test audio download",
            audioDownloadAllowed = false,
            isFavorite = false
        )
        val mockAlbum = ItemAlbumUI(
            id = "1",
            name = "test album",
            releaseDate = "test release date",
            artistId = "1",
            artistName = "test name",
            image = "test image",
            tracks = listOf(mockTracks),
            isFavorite = false
        )

        `when`(getAlbumByIdUseCase.invoke(mockAlbum.id)).thenReturn(null)

        viewModel.toggleAlbumFavorite(mockAlbum) { }

        assertNotNull(viewModel.favoriteSideEffect.value)
    }

    @Test
    fun `onTrackClicked should call playerManager playTrack`() = runTest {
        val track = ItemTrackUI(
            id = "1",
            name = "test track",
            duration = "test duration",
            audio = "test audio",
            audioDownload = "test audio download",
            audioDownloadAllowed = false,
            isFavorite = false
        )

        viewModel.onTrackClicked(track)

        verify(playerManager).playTrack(
            trackId = track.id,
            audioUrl = track.audio,
            title = track.name,
            imageUrl = ""
        )
    }
}