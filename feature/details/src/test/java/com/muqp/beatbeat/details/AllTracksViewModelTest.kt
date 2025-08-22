package com.muqp.beatbeat.details

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.muqp.beatbeat.details.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertTrackUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetPagingPopularArtistTracksUseCase
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.presentation.screen.all_tracks.AllTracksViewModel
import com.muqp.beatbeat.exo_player.PlayerManager
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
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
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AllTracksViewModelTest {
    private lateinit var getPagingPopularArtistTracksUseCase: GetPagingPopularArtistTracksUseCase
    private lateinit var insertTrackUseCase: InsertTrackUseCase
    private lateinit var deleteTrackUseCase: DeleteTrackUseCase
    private lateinit var getTrackByIdUseCase: GetTrackByIdUseCase
    private lateinit var playerManager: PlayerManager
    private lateinit var getPlaylistsUseCase: GetPlaylistsUseCase
    private lateinit var addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AllTracksViewModel

    @Before
    fun setup() {
        getPagingPopularArtistTracksUseCase = mock()
        insertTrackUseCase = mock()
        deleteTrackUseCase = mock()
        getTrackByIdUseCase = mock()
        playerManager = mock()
        getPlaylistsUseCase = mock()
        addTrackToPlaylistUseCase = mock()

        Dispatchers.setMain(testDispatcher)

        viewModel = AllTracksViewModel(
            getPagingPopularArtistTracksUseCase,
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
    fun `loadAllPopularTracksByArtist should update state with paging data`() = runTest {
        val artistId = 1
        val mockItems = listOf(
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

        val testPagingSource = object : PagingSource<Int, ItemTrackUI>() {
            override fun getRefreshKey(state: PagingState<Int, ItemTrackUI>): Int? = null

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemTrackUI> {
                return LoadResult.Page(
                    data = mockItems,
                    prevKey = null,
                    nextKey = 2
                )
            }

        }

        `when`(getPagingPopularArtistTracksUseCase.invoke(artistId))
            .thenReturn(testPagingSource)

        val states = mutableListOf<PagingData<ItemTrackUI>?>()
        val job = viewModel.state
            .onEach { states.add(it) }
            .launchIn(this)

        viewModel.loadAllPopularTracksByArtist(artistId)

        advanceUntilIdle()

        assertNotNull(viewModel.state.value)
        assertTrue(states.isNotEmpty())

        job.cancel()
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

    @Test
    fun `resetErrorMessage should clear error message`() = runTest {
        viewModel.resetErrorMessage()
        assertNull(viewModel.errorMessage.value)
    }
}