package id.dpradana.themoviedb.feature.genre.domain.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.domain.model.Genre

interface GenreRepository {
    suspend fun getGenres(): AppResult<List<Genre>>
}
