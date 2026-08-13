package id.dpradana.themoviedb.feature.reviews.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.MovieSummary
import id.dpradana.themoviedb.feature.reviews.domain.repository.ReviewRepository
import javax.inject.Inject

class GetMovieSummaryUseCase @Inject constructor(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(
        movieId: Int
    ): AppResult<MovieSummary> {
        return repository.getMovieSummary(movieId)
    }
}
