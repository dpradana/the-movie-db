package id.dpradana.themoviedb.feature.genre.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.data.mapper.toDomain
import id.dpradana.themoviedb.feature.genre.data.remote.GenreRemoteDataSource
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import id.dpradana.themoviedb.feature.genre.domain.repository.GenreRepository
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val remoteDataSource: GenreRemoteDataSource
) : GenreRepository {

    override suspend fun getGenres(): AppResult<List<Genre>> {
        return try {
            val response = remoteDataSource.getGenres()
            AppResult.Success(response.genres.map { it.toDomain() })
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }
}
