package id.dpradana.themoviedb.feature.reviews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.usecase.GetMovieVideosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrailerViewModel @Inject constructor(
    private val getMovieVideosUseCase: GetMovieVideosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrailerUiState>(TrailerUiState.Loading)
    val uiState: StateFlow<TrailerUiState> = _uiState.asStateFlow()

    fun getMovieVideos(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = TrailerUiState.Loading
            when (val result = getMovieVideosUseCase(movieId)) {
                is AppResult.Success -> {
                    _uiState.value = TrailerUiState.Success(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = TrailerUiState.Error(result.exception.message ?: "Unable to load trailer")
                }
                else -> {}
            }
        }
    }
}
