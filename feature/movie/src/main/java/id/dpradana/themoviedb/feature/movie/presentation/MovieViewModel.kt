package id.dpradana.themoviedb.feature.movie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.domain.usecase.DiscoverMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private var genreId: Int = -1

    fun setGenre(id: Int) {
        if (genreId == id) return
        genreId = id
        loadInitialMovies()
    }

    fun loadInitialMovies() {
        if (genreId == -1) return
        _uiState.value = MovieUiState.Loading
        fetchMovies(1)
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState is MovieUiState.Success) {
            if (currentState.isLoadingMore || !currentState.hasNextPage || currentState.paginationError != null) return
            
            _uiState.value = currentState.copy(isLoadingMore = true, paginationError = null)
            fetchMovies(currentState.currentPage + 1)
        }
    }

    private fun fetchMovies(page: Int) {
        viewModelScope.launch {
            when (val result = discoverMoviesUseCase(genreId, page)) {
                is AppResult.Success -> {
                    val moviePage = result.data
                    val currentMovies = (_uiState.value as? MovieUiState.Success)?.movies ?: emptyList()
                    val newMovies = if (page == 1) moviePage.movies else currentMovies + moviePage.movies
                    
                    if (newMovies.isEmpty()) {
                        _uiState.value = MovieUiState.Empty
                    } else {
                        _uiState.value = MovieUiState.Success(
                            movies = newMovies,
                            currentPage = page,
                            hasNextPage = page < moviePage.totalPages,
                            isLoadingMore = false,
                            paginationError = null
                        )
                    }
                }
                is AppResult.Error -> {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    val currentState = _uiState.value
                    if (page == 1) {
                        _uiState.value = MovieUiState.Error(errorMessage)
                    } else if (currentState is MovieUiState.Success) {
                        _uiState.value = currentState.copy(
                            isLoadingMore = false,
                            paginationError = errorMessage
                        )
                    }
                }
                is AppResult.Loading -> {
                    if (page == 1) _uiState.value = MovieUiState.Loading
                }
            }
        }
    }

    fun retry() {
        val state = _uiState.value
        if (state is MovieUiState.Error) {
            loadInitialMovies()
        } else if (state is MovieUiState.Success && state.paginationError != null) {
            // Re-fetch the page that failed
            _uiState.value = state.copy(isLoadingMore = true, paginationError = null)
            fetchMovies(state.currentPage + 1)
        }
    }
}
