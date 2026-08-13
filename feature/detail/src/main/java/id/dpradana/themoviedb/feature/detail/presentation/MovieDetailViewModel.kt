package id.dpradana.themoviedb.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.detail.domain.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    private var currentMovieId: Int? = null

    fun getMovieDetail(movieId: Int) {
        currentMovieId = movieId
        fetchMovieDetail(movieId)
    }

    fun retry() {
        currentMovieId?.let { fetchMovieDetail(it) }
    }

    private fun fetchMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            when (val result = getMovieDetailUseCase(movieId)) {
                is AppResult.Success -> {
                    _uiState.value = MovieDetailUiState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = MovieDetailUiState.Error(result.exception.message ?: "Unknown error occurred")
                }
                is AppResult.Loading -> {
                    _uiState.value = MovieDetailUiState.Loading
                }
            }
        }
    }
}
