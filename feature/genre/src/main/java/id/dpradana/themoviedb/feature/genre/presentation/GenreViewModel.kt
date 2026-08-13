package id.dpradana.themoviedb.feature.genre.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.domain.usecase.GetGenresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GenreViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenreUiState>(GenreUiState.Loading)
    val uiState: StateFlow<GenreUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        _uiState.value = GenreUiState.Loading
        viewModelScope.launch {
            when (val result = getGenresUseCase()) {
                is AppResult.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = GenreUiState.Empty
                    } else {
                        _uiState.value = GenreUiState.Success(result.data)
                    }
                }
                is AppResult.Error -> {
                    _uiState.value = GenreUiState.Error(result.exception.message ?: "Unknown error")
                }
                is AppResult.Loading -> {
                    _uiState.value = GenreUiState.Loading
                }
            }
        }
    }

    fun retry() {
        loadGenres()
    }
}
