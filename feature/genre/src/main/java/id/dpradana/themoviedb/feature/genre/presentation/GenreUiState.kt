package id.dpradana.themoviedb.feature.genre.presentation

import id.dpradana.themoviedb.feature.genre.domain.model.Genre

sealed interface GenreUiState {
    data object Loading : GenreUiState
    data class Success(val genres: List<Genre>) : GenreUiState
    data object Empty : GenreUiState
    data class Error(val message: String) : GenreUiState
}
