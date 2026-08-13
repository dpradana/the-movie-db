package id.dpradana.themoviedb.feature.movie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.domain.usecase.DiscoverMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieViewModel @Inject constructor(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private var genreId: Int = -1

    fun setGenre(id: Int) {
        if (genreId == id) return
        genreId = id
        loadInitialMovies()
    }

    fun loadInitialMovies() {
        if (genreId == -1) return

        _uiState.update { 
            it.copy(
                isLoading = true, 
                error = null, 
                movies = emptyList(), 
                currentPage = 1, 
                hasNextPage = true 
            ) 
        }
        
        fetchMovies(1)
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasNextPage || currentState.isLoading) return

        _uiState.update { it.copy(isLoadingMore = true, paginationError = null) }
        fetchMovies(currentState.currentPage + 1)
    }

    private fun fetchMovies(page: Int) {
        viewModelScope.launch {
            when (val result = discoverMoviesUseCase(genreId, page)) {
                is AppResult.Success -> {
                    val moviePage = result.data
                    _uiState.update { state ->
                        state.copy(
                            movies = if (page == 1) moviePage.movies else state.movies + moviePage.movies,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = page,
                            hasNextPage = page < moviePage.totalPages,
                            error = null,
                            paginationError = null
                        )
                    }
                }
                is AppResult.Error -> {
                    val errorMessage = result.exception.message ?: "Unknown error"
                    _uiState.update { state ->
                        if (page == 1) {
                            state.copy(isLoading = false, error = errorMessage)
                        } else {
                            state.copy(isLoadingMore = false, paginationError = errorMessage)
                        }
                    }
                }
                is AppResult.Loading -> {
                    // Initial loading handled by loadInitialMovies
                }
            }
        }
    }

    fun retry() {
        val state = _uiState.value
        if (state.error != null) {
            loadInitialMovies()
        } else if (state.paginationError != null) {
            loadNextPage()
        }
    }
}
