package id.dpradana.themoviedb.feature.reviews.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage
import id.dpradana.themoviedb.feature.reviews.domain.repository.ReviewRepository
import javax.inject.Inject

class GetMovieReviewsUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        page: Int
    ): AppResult<ReviewPage> {
        return repository.getReviews(
            movieId = movieId,
            page = page
        )
    }
}
