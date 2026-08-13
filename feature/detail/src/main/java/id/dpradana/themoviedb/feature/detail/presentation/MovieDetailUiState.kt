package id.dpradana.themoviedb.feature.detail.presentation

import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail

sealed interface MovieDetailUiState {
    data object Loading : MovieDetailUiState
    data class Success(val movie: MovieDetail) : MovieDetailUiState
    data class Error(val message: String) : MovieDetailUiState
}
