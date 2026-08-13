package id.dpradana.themoviedb.feature.detail.domain.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail

interface MovieDetailRepository {
    suspend fun getMovieDetail(movieId: Int): AppResult<MovieDetail>
}
