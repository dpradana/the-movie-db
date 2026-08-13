package id.dpradana.themoviedb.feature.reviews.domain.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage

interface ReviewRepository {
    suspend fun getReviews(
        movieId: Int,
        page: Int
    ): AppResult<ReviewPage>
}
