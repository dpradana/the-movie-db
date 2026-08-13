package id.dpradana.themoviedb.feature.movie.presentation

import id.dpradana.themoviedb.feature.movie.domain.model.Movie

sealed interface MovieUiState {
    data object Loading : MovieUiState
    
    data class Success(
        val movies: List<Movie>,
        val currentPage: Int,
        val hasNextPage: Boolean,
        val isLoadingMore: Boolean = false,
        val paginationError: String? = null
    ) : MovieUiState
    
    data object Empty : MovieUiState
    
    data class Error(val message: String) : MovieUiState
}
