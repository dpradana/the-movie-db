package id.dpradana.themoviedb.feature.reviews.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.domain.model.Trailer
import id.dpradana.themoviedb.feature.reviews.domain.repository.TrailerRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val repository: TrailerRepository
) {
    suspend operator fun invoke(movieId: Int): AppResult<List<Trailer>> {
        return repository.getMovieVideos(movieId)
    }
}
