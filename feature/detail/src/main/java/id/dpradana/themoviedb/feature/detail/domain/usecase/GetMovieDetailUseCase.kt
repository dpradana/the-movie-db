package id.dpradana.themoviedb.feature.detail.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail
import id.dpradana.themoviedb.feature.detail.domain.repository.MovieDetailRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    suspend operator fun invoke(movieId: Int): AppResult<MovieDetail> {
        return repository.getMovieDetail(movieId)
    }
}
