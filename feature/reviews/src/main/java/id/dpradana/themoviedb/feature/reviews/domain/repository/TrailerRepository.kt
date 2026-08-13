package id.dpradana.themoviedb.feature.reviews.domain.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.Trailer

interface TrailerRepository {
    suspend fun getMovieVideos(movieId: Int): AppResult<List<Trailer>>
}
