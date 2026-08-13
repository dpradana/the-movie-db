package id.dpradana.themoviedb.feature.movie.presentation

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.domain.model.Movie
import id.dpradana.themoviedb.feature.movie.domain.model.MoviePage
import id.dpradana.themoviedb.feature.movie.domain.usecase.DiscoverMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {

    private lateinit var viewModel: MovieViewModel
    private val discoverMoviesUseCase: DiscoverMoviesUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val movie1 = Movie(1, "Movie 1", "Overview 1", null, null, "2026", 8.0)
    private val movie2 = Movie(2, "Movie 2", "Overview 2", null, null, "2026", 7.0)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieViewModel(discoverMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load success should update state correctly`() = runTest {
        val page = MoviePage(listOf(movie1), 1, 2)
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Success(page)

        viewModel.setGenre(28)
        
        assertTrue(viewModel.uiState.value is MovieUiState.Loading)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieUiState.Success)
        state as MovieUiState.Success
        assertEquals(listOf(movie1), state.movies)
        assertEquals(1, state.currentPage)
        assertTrue(state.hasNextPage)
        assertNull(state.paginationError)
    }

    @Test
    fun `initial load error should update state correctly`() = runTest {
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Error(Exception("API Error"))

        viewModel.setGenre(28)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieUiState.Error)
        assertEquals("API Error", (state as MovieUiState.Error).message)
    }

    @Test
    fun `load next page should append movies`() = runTest {
        // Initial load
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Success(MoviePage(listOf(movie1), 1, 2))
        viewModel.setGenre(28)
        advanceUntilIdle()

        // Next page
        coEvery { discoverMoviesUseCase(28, 2) } returns AppResult.Success(MoviePage(listOf(movie2), 2, 2))
        viewModel.loadNextPage()
        
        val loadingState = viewModel.uiState.value
        assertTrue(loadingState is MovieUiState.Success && loadingState.isLoadingMore)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieUiState.Success)
        state as MovieUiState.Success
        assertFalse(state.isLoadingMore)
        assertEquals(listOf(movie1, movie2), state.movies)
        assertEquals(2, state.currentPage)
        assertFalse(state.hasNextPage)
    }

    @Test
    fun `load next page error should not remove existing movies`() = runTest {
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Success(MoviePage(listOf(movie1), 1, 2))
        viewModel.setGenre(28)
        advanceUntilIdle()

        coEvery { discoverMoviesUseCase(28, 2) } returns AppResult.Error(Exception("Pagination Error"))
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MovieUiState.Success)
        state as MovieUiState.Success
        assertFalse(state.isLoadingMore)
        assertEquals(listOf(movie1), state.movies)
        assertEquals("Pagination Error", state.paginationError)
    }

    @Test
    fun `prevent duplicate next page requests`() = runTest {
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Success(MoviePage(listOf(movie1), 1, 2))
        viewModel.setGenre(28)
        advanceUntilIdle()

        coEvery { discoverMoviesUseCase(28, 2) } returns AppResult.Success(MoviePage(listOf(movie2), 2, 2))
        
        // Multiple calls
        viewModel.loadNextPage()
        viewModel.loadNextPage()
        viewModel.loadNextPage()
        
        advanceUntilIdle()

        coVerify(exactly = 1) { discoverMoviesUseCase(28, 2) }
    }

    @Test
    fun `retry initial load should request page 1`() = runTest {
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Error(Exception("Error"))
        viewModel.setGenre(28)
        advanceUntilIdle()
        
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Success(MoviePage(listOf(movie1), 1, 2))
        viewModel.retry()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is MovieUiState.Success)
        assertEquals(listOf(movie1), (state as MovieUiState.Success).movies)
    }

    @Test
    fun `retry pagination should request the same page`() = runTest {
        coEvery { discoverMoviesUseCase(28, 1) } returns AppResult.Success(MoviePage(listOf(movie1), 1, 2))
        viewModel.setGenre(28)
        advanceUntilIdle()

        coEvery { discoverMoviesUseCase(28, 2) } returns AppResult.Error(Exception("Error"))
        viewModel.loadNextPage()
        advanceUntilIdle()

        coEvery { discoverMoviesUseCase(28, 2) } returns AppResult.Success(MoviePage(listOf(movie2), 2, 2))
        viewModel.retry()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is MovieUiState.Success)
        state as MovieUiState.Success
        assertEquals(listOf(movie1, movie2), state.movies)
        assertEquals(2, state.currentPage)
    }
}
