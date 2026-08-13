package id.dpradana.themoviedb.feature.detail.data.source

import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.core.network.api.model.MovieDetailDto
import javax.inject.Inject

class MovieDetailRemoteDataSource @Inject constructor(
    private val movieApi: MovieApi
) {
    suspend fun getMovieDetail(movieId: Int): MovieDetailDto {
        return movieApi.getMovieDetail(movieId)
    }
}
