package id.dpradana.themoviedb.feature.reviews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieReviewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReviewViewModel @Inject constructor(
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var movieId: Int? = null

    fun setMovieId(movieId: Int) {
        if (this.movieId == movieId) return
        this.movieId = movieId
        fetchInitialReviews()
    }

    fun fetchInitialReviews() {
        val currentMovieId = movieId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = getMovieReviewsUseCase(currentMovieId, 1)) {
                is AppResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            reviews = result.data.reviews,
                            isLoading = false,
                            currentPage = 1,
                            hasNextPage = 1 < result.data.totalPages
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception.message ?: "Unable to load reviews.") }
                }
                else -> {}
            }
        }
    }

    fun loadNextPage() {
        val currentMovieId = movieId ?: return
        val state = _uiState.value
        
        if (state.isLoadingMore || !state.hasNextPage || state.isLoading) return
        
        val nextPage = state.currentPage + 1
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, paginationError = null) }
            
            when (val result = getMovieReviewsUseCase(currentMovieId, nextPage)) {
                is AppResult.Success -> {
                    _uiState.update { s ->
                        s.copy(
                            reviews = s.reviews + result.data.reviews,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            hasNextPage = nextPage < result.data.totalPages
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingMore = false, paginationError = result.exception.message ?: "Failed to load more.") }
                }
                else -> {}
            }
        }
    }

    fun retry() {
        if (_uiState.value.reviews.isEmpty()) {
            fetchInitialReviews()
        } else {
            loadNextPage()
        }
    }
}
