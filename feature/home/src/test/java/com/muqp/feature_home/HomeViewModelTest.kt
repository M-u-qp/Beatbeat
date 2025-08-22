package com.muqp.feature_home

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.muqp.feature_home.domain.use_cases.GetMusicFeedsUseCase
import com.muqp.feature_home.domain.use_cases.SignOutUserUseCase
import com.muqp.feature_home.model.FeedItemUI
import com.muqp.feature_home.model.ImageSizeUI
import com.muqp.feature_home.model.LangUI
import com.muqp.feature_home.presentation.screen.HomeScreenState
import com.muqp.feature_home.presentation.screen.HomeViewModel
import junit.framework.TestCase.assertEquals
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var getMusicFeedsUseCase: GetMusicFeedsUseCase
    private lateinit var signOutUserUseCase: SignOutUserUseCase

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        getMusicFeedsUseCase = mock()
        signOutUserUseCase = mock()

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestFeedItem(): FeedItemUI {
        val mockLang = LangUI(
            en = "test en",
            ru = "test ru"
        )
        val mockImageSize = ImageSizeUI(
            size996X350 = "test 996X350",
            size315X111 = "test 315X111",
            size600X211 = "test 600X211",
            size470X165 = "test 470X165"
        )
        return FeedItemUI(
            id = "1",
            title = mockLang,
            link = "test link",
            dateStart = "test date start",
            dateEnd = "test date end",
            type = "test type",
            text = mockLang,
            images = mockImageSize
        )
    }

    @Test
    fun `loadMusicFeeds should update state to Success when paging succeeds`() = runTest {
        val mockItems = listOf(createTestFeedItem())

        val testPagingSource = object : PagingSource<Int, FeedItemUI>() {
            override fun getRefreshKey(state: PagingState<Int, FeedItemUI>): Int? = null

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedItemUI> {
                return LoadResult.Page(
                    data = mockItems,
                    prevKey = null,
                    nextKey = 2
                )
            }
        }

        `when`(getMusicFeedsUseCase.invoke()).thenReturn(testPagingSource)

        viewModel = HomeViewModel(
            getMusicFeedsUseCase,
            signOutUserUseCase
        )

        val states = mutableListOf<HomeScreenState?>()
        val job = viewModel.state
            .onEach { states.add(it) }
            .launchIn(this)

        viewModel.loadMusicFeeds()
        advanceUntilIdle()

        assertTrue(states.last() is HomeScreenState.Success)
        job.cancel()
    }

    @Test
    fun `loadMusicFeeds should update state to Error when paging fails`() = runTest {
        val testPagingSource = object : PagingSource<Int, FeedItemUI>() {
            override fun getRefreshKey(state: PagingState<Int, FeedItemUI>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedItemUI> {
                throw RuntimeException("test error")
            }
        }

        `when`(getMusicFeedsUseCase.invoke()).thenReturn(testPagingSource)

        viewModel = HomeViewModel(
            getMusicFeedsUseCase,
            signOutUserUseCase
        )

        val errorLoadState = CombinedLoadStates(
            refresh = LoadState.Error(Throwable("test error")),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            source = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )

        viewModel.handlePaginationLoadState(errorLoadState)

        val lastState = viewModel.state.value
        assertTrue(lastState is HomeScreenState.Error)
        assertEquals("test error", (lastState as HomeScreenState.Error).message)
    }

    @Test
    fun `signOut should update state to SignOut and call use case`() = runTest {
        `when`(signOutUserUseCase.invoke()).thenReturn(Unit)

        viewModel = HomeViewModel(
            getMusicFeedsUseCase,
            signOutUserUseCase
        )

        viewModel.signOut()
        advanceUntilIdle()

        verify(signOutUserUseCase).invoke()
        assertEquals(HomeScreenState.SignOut, viewModel.state.value)
    }
}