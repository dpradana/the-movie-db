package id.dpradana.themoviedb.feature.movie.presentation

import id.dpradana.themoviedb.feature.movie.domain.model.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val paginationError: String? = null,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = true
)
