package id.dpradana.themoviedb.feature.genre.presentation

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import id.dpradana.themoviedb.feature.genre.domain.usecase.GetGenresUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

    private lateinit var viewModel: GenreViewModel
    private val getGenresUseCase: GetGenresUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading and then Success`() = runTest {
        val genres = listOf(Genre(1, "Action"))
        coEvery { getGenresUseCase() } returns AppResult.Success(genres)

        viewModel = GenreViewModel(getGenresUseCase)
        
        assertTrue(viewModel.uiState.value is GenreUiState.Loading)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is GenreUiState.Success)
        assertEquals(genres, (state as GenreUiState.Success).genres)
    }

    @Test
    fun `initial state should be Loading and then Error`() = runTest {
        coEvery { getGenresUseCase() } returns AppResult.Error(Exception("Network error"))

        viewModel = GenreViewModel(getGenresUseCase)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is GenreUiState.Error)
        assertEquals("Network error", (state as GenreUiState.Error).message)
    }

    @Test
    fun `retry should request genres again`() = runTest {
        coEvery { getGenresUseCase() } returns AppResult.Error(Exception("Error"))
        viewModel = GenreViewModel(getGenresUseCase)
        advanceUntilIdle()
        
        coEvery { getGenresUseCase() } returns AppResult.Success(listOf(Genre(1, "Action")))
        viewModel.retry()
        
        assertTrue(viewModel.uiState.value is GenreUiState.Loading)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is GenreUiState.Success)
    }
}
