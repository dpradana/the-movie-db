package id.dpradana.themoviedb.feature.reviews.presentation

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieReviewsUseCase
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
    private val testDispatcher = StandardTestDispatcher()

    private val reviewPage = ReviewPage(
        reviews = emptyList(),
        page = 1,
        totalPages = 2
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReviewViewModel(getMovieReviewsUseCase)
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
        assertFalse(state.isLoading)
        assertEquals(1, state.currentPage)
        assertTrue(state.hasNextPage)
    }

    @Test
    fun `fetchInitialReviews error updates state`() = runTest {
        coEvery { getMovieReviewsUseCase(1, 1) } returns AppResult.Error(Exception("Error"))

        viewModel.setMovieId(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error", state.error)
    }

    @Test
    fun `loadNextPage success appends reviews`() = runTest {
        coEvery { getMovieReviewsUseCase(1, 1) } returns AppResult.Success(reviewPage)
        viewModel.setMovieId(1)
        advanceUntilIdle()

        val secondPage = ReviewPage(emptyList(), 2, 2)
        coEvery { getMovieReviewsUseCase(1, 2) } returns AppResult.Success(secondPage)

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.currentPage)
        assertFalse(state.hasNextPage)
    }
}
