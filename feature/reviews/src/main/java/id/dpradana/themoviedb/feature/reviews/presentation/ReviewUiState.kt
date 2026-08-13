package id.dpradana.themoviedb.feature.reviews.presentation

import id.dpradana.themoviedb.feature.reviews.domain.model.Review

sealed interface ReviewUiState {
    data object Loading : ReviewUiState
    
    data class Success(
        val movieTitle: String? = null,
        val averageRating: Double? = null,
        val voteCount: Int? = null,
        val reviews: List<Review>,
        val currentPage: Int,
        val hasNextPage: Boolean,
        val isLoadingMore: Boolean = false,
        val paginationError: String? = null
    ) : ReviewUiState
    
    data object Empty : ReviewUiState
    
    data class Error(val message: String) : ReviewUiState
}
