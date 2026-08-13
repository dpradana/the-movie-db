package id.dpradana.themoviedb.feature.reviews.data.source

import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.core.network.api.model.VideoResponseDto
import javax.inject.Inject

class VideoRemoteDataSource @Inject constructor(
    private val movieApi: MovieApi
) {
    suspend fun getMovieVideos(movieId: Int): VideoResponseDto {
        return movieApi.getMovieVideos(movieId)
    }
}
