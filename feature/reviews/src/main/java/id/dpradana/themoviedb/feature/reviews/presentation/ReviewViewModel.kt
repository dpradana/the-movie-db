package id.dpradana.themoviedb.feature.reviews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.MovieSummary
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieReviewsUseCase
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReviewViewModel @Inject constructor(
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieSummaryUseCase: GetMovieSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var movieId: Int = -1
    private var movieSummary: MovieSummary? = null

    fun setMovieId(id: Int) {
        if (movieId == id) return
        movieId = id
        loadInitialReviews()
    }

    fun loadInitialReviews() {
        if (movieId == -1) return
        _uiState.value = ReviewUiState.Loading
        movieSummary = null
        fetchMovieSummary()
        fetchReviews(1)
    }

    private fun fetchMovieSummary() {
        viewModelScope.launch {
            when (val result = getMovieSummaryUseCase(movieId)) {
                is AppResult.Success -> {
                    movieSummary = result.data
                    val currentState = _uiState.value
                    if (currentState is ReviewUiState.Success) {
                        _uiState.value = currentState.copy(
                            movieTitle = movieSummary?.title,
                            averageRating = movieSummary?.averageRating,
                            voteCount = movieSummary?.voteCount
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState is ReviewUiState.Success) {
            if (currentState.isLoadingMore || !currentState.hasNextPage || currentState.paginationError != null) return
            
            _uiState.value = currentState.copy(isLoadingMore = true, paginationError = null)
            fetchReviews(currentState.currentPage + 1)
        }
    }

    private fun fetchReviews(page: Int) {
        viewModelScope.launch {
            when (val result = getMovieReviewsUseCase(movieId, page)) {
                is AppResult.Success -> {
                    val reviewPage = result.data
                    val currentState = _uiState.value
                    val currentReviews = (currentState as? ReviewUiState.Success)?.reviews ?: emptyList()
                    val newReviews = if (page == 1) reviewPage.reviews else currentReviews + reviewPage.reviews
                    
                    if (newReviews.isEmpty()) {
                        _uiState.value = ReviewUiState.Empty
                    } else {
                        var successState = if (page == 1) {
                            ReviewUiState.Success(
                                reviews = newReviews,
                                currentPage = page,
                                hasNextPage = page < reviewPage.totalPages,
                                isLoadingMore = false,
                                paginationError = null
                            )
                        } else {
                            (currentState as ReviewUiState.Success).copy(
                                reviews = newReviews,
                                currentPage = page,
                                hasNextPage = page < reviewPage.totalPages,
                                isLoadingMore = false,
                                paginationError = null
                            )
                        }
                        
                        movieSummary?.let { summary ->
                            successState = successState.copy(
                                movieTitle = summary.title,
                                averageRating = summary.averageRating,
                                voteCount = summary.voteCount
                            )
                        }
                        
                        _uiState.value = successState
                    }
                }
                is AppResult.Error -> {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    val currentState = _uiState.value
                    if (page == 1) {
                        _uiState.value = ReviewUiState.Error(errorMessage)
                    } else if (currentState is ReviewUiState.Success) {
                        _uiState.value = currentState.copy(
                            isLoadingMore = false,
                            paginationError = errorMessage
                        )
                    }
                }
                is AppResult.Loading -> {
                    if (page == 1) _uiState.value = ReviewUiState.Loading
                }
            }
        }
    }

    fun retry() {
        val state = _uiState.value
        if (state is ReviewUiState.Error) {
            loadInitialReviews()
        } else if (state is ReviewUiState.Success && state.paginationError != null) {
            _uiState.value = state.copy(isLoadingMore = true, paginationError = null)
            fetchReviews(state.currentPage + 1)
        }
    }
}
