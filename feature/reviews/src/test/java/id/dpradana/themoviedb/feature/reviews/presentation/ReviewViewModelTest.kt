package id.dpradana.themoviedb.feature.reviews.presentation

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.MovieSummary
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieReviewsUseCase
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieSummaryUseCase
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
class ReviewViewModelTest {

    private lateinit var viewModel: ReviewViewModel
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase = mockk()
    private val getMovieSummaryUseCase: GetMovieSummaryUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val reviewPage = ReviewPage(
        reviews = listOf(mockk()),
        page = 1,
        totalPages = 2
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getMovieSummaryUseCase(any()) } returns AppResult.Success(
            MovieSummary("Test Movie", 8.0, 100)
        )
        viewModel = ReviewViewModel(getMovieReviewsUseCase, getMovieSummaryUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchInitialReviews success updates state`() = runTest {
        coEvery { getMovieReviewsUseCase(1, 1) } returns AppResult.Success(reviewPage)

        viewModel.setMovieId(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ReviewUiState.Success)
        state as ReviewUiState.Success
        assertEquals(1, state.currentPage)
        assertTrue(state.hasNextPage)
    }

    @Test
    fun `fetchInitialReviews error updates state`() = runTest {
        coEvery { getMovieReviewsUseCase(1, 1) } returns AppResult.Error(Exception("Error"))

        viewModel.setMovieId(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ReviewUiState.Error)
        assertEquals("Error", (state as ReviewUiState.Error).message)
    }

    @Test
    fun `loadNextPage success appends reviews`() = runTest {
        coEvery { getMovieReviewsUseCase(1, 1) } returns AppResult.Success(reviewPage)
        viewModel.setMovieId(1)
        advanceUntilIdle()

        val secondPage = ReviewPage(listOf(mockk()), 2, 2)
        coEvery { getMovieReviewsUseCase(1, 2) } returns AppResult.Success(secondPage)

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ReviewUiState.Success)
        assertEquals(2, (state as ReviewUiState.Success).currentPage)
        assertFalse(state.hasNextPage)
    }
}
