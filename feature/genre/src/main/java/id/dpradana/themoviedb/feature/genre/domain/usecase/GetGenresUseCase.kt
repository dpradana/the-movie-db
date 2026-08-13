package id.dpradana.themoviedb.feature.genre.domain.usecase

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import id.dpradana.themoviedb.feature.genre.domain.repository.GenreRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: GenreRepository
) {
    suspend operator fun invoke(): AppResult<List<Genre>> {
        return repository.getGenres()
    }
}
