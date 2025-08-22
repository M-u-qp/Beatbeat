package com.muqp.feature_favorites

import com.muqp.feature_favorites.domain.use_cases.DeleteArtistUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllArtistsUseCase
import com.muqp.feature_favorites.model.ItemArtist
import com.muqp.feature_favorites.presentation.screen.favorite_artists.FavoriteArtistsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteArtistsViewModelTest {
    private lateinit var getAllArtistsUseCase: GetAllArtistsUseCase
    private lateinit var deleteArtistUseCase: DeleteArtistUseCase

    private lateinit var viewModel: FavoriteArtistsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        getAllArtistsUseCase = mock()
        deleteArtistUseCase = mock()

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllArtists should emit artists from use case`() = runTest {
        val mockArtists = listOf(
            ItemArtist(
                id = "1",
                name = "test name",
                website = "test website",
                joinDate = "test join date",
                image = "test image",
                shareUrl = "test share url",
                isFavorite = false
            )
        )

        `when`(getAllArtistsUseCase.invoke()).thenReturn(flowOf(mockArtists))

        viewModel = FavoriteArtistsViewModel(
            getAllArtistsUseCase,
            deleteArtistUseCase
        )

        val result = viewModel.getAllArtists.first()

        assertEquals(mockArtists, result)
    }

    @Test
    fun `loadFavoriteArtistsDataRecycler should map artists correctly`() = runTest {
        val mockArtists = listOf(
            ItemArtist(
                id = "1",
                name = "test name",
                website = "test website",
                joinDate = "test join date",
                image = "test image",
                shareUrl = "test share url",
                isFavorite = false
            )
        )

        viewModel = FavoriteArtistsViewModel(
            getAllArtistsUseCase,
            deleteArtistUseCase
        )

        val result = viewModel.loadFavoriteArtistsDataRecycler(
            mockArtists,
            onArtistClick = {},
            onMenuClickListener = { _, _ -> }
        )

        assertEquals(1, result.size)
        assertEquals("1", result[0].getId())
    }

    @Test
    fun `deleteArtist should call use case`() = runTest {
        val mockArtist = ItemArtist(
            id = "1",
            name = "test name",
            website = "test website",
            joinDate = "test join date",
            image = "test image",
            shareUrl = "test share url",
            isFavorite = false
        )

        viewModel = FavoriteArtistsViewModel(
            getAllArtistsUseCase,
            deleteArtistUseCase
        )

        viewModel.deleteArtist(mockArtist)

        verify(deleteArtistUseCase).invoke(mockArtist)
    }

    @Test
    fun `error handling should update error message`() = runTest {
        val errorMessage = "test error"

        `when`(getAllArtistsUseCase.invoke()).thenReturn( flow { throw RuntimeException(errorMessage) })

        viewModel = FavoriteArtistsViewModel(
            getAllArtistsUseCase,
            deleteArtistUseCase
        )

        viewModel.getAllArtists.first()
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.errorMessage.value)
    }

    @Test
    fun `resetErrorMessage should clear error message`() {
        viewModel = FavoriteArtistsViewModel(
            getAllArtistsUseCase,
            deleteArtistUseCase
        )

        viewModel.resetErrorMessage()

        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `flow should handle errors through coroutineEH`() = runTest {
        val errorMessage = "flow error"
        `when`(getAllArtistsUseCase.invoke()).thenReturn(flow { throw RuntimeException(errorMessage) })

        viewModel = FavoriteArtistsViewModel(
            getAllArtistsUseCase,
            deleteArtistUseCase
        )

        viewModel.getAllArtists.first()
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.errorMessage.value)
    }
}