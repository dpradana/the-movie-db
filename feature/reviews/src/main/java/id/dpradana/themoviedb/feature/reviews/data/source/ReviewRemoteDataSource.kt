package id.dpradana.themoviedb.feature.reviews.data.source

import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.core.network.api.model.ReviewResponseDto
import javax.inject.Inject

class ReviewRemoteDataSource @Inject constructor(
    private val movieApi: MovieApi
) {
    suspend fun getReviews(
        movieId: Int,
        page: Int
    ): ReviewResponseDto {
        return movieApi.getMovieReviews(
            movieId = movieId,
            page = page
        )
    }
}
