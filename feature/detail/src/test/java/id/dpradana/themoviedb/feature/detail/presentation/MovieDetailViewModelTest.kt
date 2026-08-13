package id.dpradana.themoviedb.feature.detail.presentation

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail
import id.dpradana.themoviedb.feature.detail.domain.usecase.GetMovieDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private lateinit var viewModel: MovieDetailViewModel
    private val getMovieDetailUseCase: GetMovieDetailUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val movieDetail = MovieDetail(
        id = 1,
        title = "Test Movie",
        overview = "Overview",
        posterPath = null,
        backdropPath = null,
        releaseDate = "2026-08-13",
        rating = 8.2,
        voteCount = 100,
        runtime = 120,
        genres = emptyList(),
        tagline = "Tagline",
        status = "Released"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieDetailViewModel(getMovieDetailUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovieDetail success should update state to Success`() = runTest {
        coEvery { getMovieDetailUseCase(1) } returns AppResult.Success(movieDetail)

        viewModel.loadMovieDetail(1)
        
        assertEquals(MovieDetailUiState.Loading, viewModel.uiState.value)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailUiState.Success)
        assertEquals(movieDetail, (state as MovieDetailUiState.Success).movie)
    }

    @Test
    fun `loadMovieDetail error should update state to Error`() = runTest {
        coEvery { getMovieDetailUseCase(1) } returns AppResult.Error(Exception("API Error"))

        viewModel.loadMovieDetail(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailUiState.Error)
        assertEquals("API Error", (state as MovieDetailUiState.Error).message)
    }

    @Test
    fun `retry should request the same movie detail`() = runTest {
        coEvery { getMovieDetailUseCase(1) } returns AppResult.Error(Exception("Error"))
        viewModel.loadMovieDetail(1)
        advanceUntilIdle()

        coEvery { getMovieDetailUseCase(1) } returns AppResult.Success(movieDetail)
        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieDetailUiState.Success)
        assertEquals(movieDetail, (state as MovieDetailUiState.Success).movie)
    }
}
