package id.dpradana.themoviedb.feature.movie.domain.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.domain.model.MoviePage

interface MovieRepository {
    suspend fun discoverMovies(
        genreId: Int,
        page: Int
    ): AppResult<MoviePage>
}
