package id.dpradana.themoviedb.feature.movie.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.domain.model.MoviePage
import id.dpradana.themoviedb.feature.movie.domain.repository.MovieRepository
import javax.inject.Inject

class DiscoverMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        genreId: Int,
        page: Int
    ): AppResult<MoviePage> {
        return repository.discoverMovies(genreId, page)
    }
}
