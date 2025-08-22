package com.muqp.feature_favorites

import com.muqp.feature_favorites.domain.use_cases.DeleteAlbumUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllAlbumsUseCase
import com.muqp.feature_favorites.model.ItemAlbum
import com.muqp.feature_favorites.presentation.screen.favorite_albums.FavoriteAlbumsViewModel
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
class FavoriteAlbumsViewModelTest {
    private lateinit var getAllAlbumsUseCase: GetAllAlbumsUseCase
    private lateinit var deleteAlbumUseCase: DeleteAlbumUseCase

    private lateinit var viewModel: FavoriteAlbumsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        getAllAlbumsUseCase = mock()
        deleteAlbumUseCase = mock()

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllAlbums should emit albums from use case`() = runTest {
        val mockAlbums = listOf(
            ItemAlbum(
                id = "1",
                name = "test name",
                releaseDate = "test release date",
                artistId = "1",
                artistName = "test artist name",
                image = "test image",
                zip = "test zip",
                shareUrl = "test share url",
                zipAllowed = false,
                isFavorite = false
            )
        )

        `when`(getAllAlbumsUseCase.invoke()).thenReturn(flowOf(mockAlbums))

        viewModel = FavoriteAlbumsViewModel(
            getAllAlbumsUseCase,
            deleteAlbumUseCase
        )

        val result = viewModel.getAllAlbums.first()

        assertEquals(mockAlbums, result)
    }

    @Test
    fun `loadFavoriteAlbumsDataRecycler should map albums correctly`() = runTest {
        val mockAlbums = listOf(
            ItemAlbum(
                id = "1",
                name = "test name",
                releaseDate = "test release date",
                artistId = "1",
                artistName = "test artist name",
                image = "test image",
                zip = "test zip",
                shareUrl = "test share url",
                zipAllowed = false,
                isFavorite = false
            )
        )

        viewModel = FavoriteAlbumsViewModel(
            getAllAlbumsUseCase,
            deleteAlbumUseCase
        )

        val result = viewModel.loadFavoriteAlbumsDataRecycler(
            mockAlbums,
            onAlbumClick = {},
            onMenuClickListener = { _, _ -> }
        )

        assertEquals(1, result.size)
        assertEquals("1", result[0].getId())
    }

    @Test
    fun `deleteAlbum should call use case`() = runTest {
        val mockAlbum = ItemAlbum(
            id = "1",
            name = "test name",
            releaseDate = "test release date",
            artistId = "1",
            artistName = "test artist name",
            image = "test image",
            zip = "test zip",
            shareUrl = "test share url",
            zipAllowed = false,
            isFavorite = false
        )

        viewModel = FavoriteAlbumsViewModel(
            getAllAlbumsUseCase,
            deleteAlbumUseCase
        )

        viewModel.deleteAlbum(mockAlbum)

        verify(deleteAlbumUseCase).invoke(mockAlbum)
    }

    @Test
    fun `error handling should update error message`() = runTest {
        val errorMessage = "test error"

        `when`(getAllAlbumsUseCase.invoke()).thenReturn( flow { throw RuntimeException(errorMessage) })

        viewModel = FavoriteAlbumsViewModel(
            getAllAlbumsUseCase,
            deleteAlbumUseCase
        )

        viewModel.getAllAlbums.first()
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.errorMessage.value)
    }

    @Test
    fun `resetErrorMessage should clear error message`() {
        viewModel = FavoriteAlbumsViewModel(
            getAllAlbumsUseCase,
            deleteAlbumUseCase
        )

        viewModel.resetErrorMessage()

        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `flow should handle errors through coroutineEH`() = runTest {
        val errorMessage = "flow error"
        `when`(getAllAlbumsUseCase.invoke()).thenReturn(flow { throw RuntimeException(errorMessage) })

        viewModel = FavoriteAlbumsViewModel(
            getAllAlbumsUseCase,
            deleteAlbumUseCase
        )

        viewModel.getAllAlbums.first()
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.errorMessage.value)
    }
}