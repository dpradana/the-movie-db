package id.dpradana.themoviedb.feature.reviews.presentation

import id.dpradana.themoviedb.feature.reviews.domain.model.Trailer

sealed interface TrailerUiState {
    data object Loading : TrailerUiState
    data class Success(val trailers: List<Trailer>) : TrailerUiState
    data class Error(val message: String) : TrailerUiState
}
