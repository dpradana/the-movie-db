package id.dpradana.themoviedb.feature.reviews.presentation

import id.dpradana.themoviedb.feature.reviews.domain.model.Review

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val paginationError: String? = null,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = true
)
