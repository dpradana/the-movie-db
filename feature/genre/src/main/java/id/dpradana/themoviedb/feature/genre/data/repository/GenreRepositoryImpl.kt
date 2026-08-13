package id.dpradana.themoviedb.feature.genre.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.genre.data.local.GenreLocalDataSource
import id.dpradana.themoviedb.feature.genre.data.mapper.toDomain
import id.dpradana.themoviedb.feature.genre.data.mapper.toEntity
import id.dpradana.themoviedb.feature.genre.data.remote.GenreRemoteDataSource
import id.dpradana.themoviedb.feature.genre.domain.model.Genre
import id.dpradana.themoviedb.feature.genre.domain.repository.GenreRepository
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val remoteDataSource: GenreRemoteDataSource,
    private val localDataSource: GenreLocalDataSource
) : GenreRepository {

    override suspend fun getGenres(): AppResult<List<Genre>> {
        return try {
            val response = remoteDataSource.getGenres()
            val entities = response.genres.map { it.toEntity() }
            localDataSource.replaceGenres(entities)
            AppResult.Success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            val cachedGenres = localDataSource.getGenres()
            if (cachedGenres.isNotEmpty()) {
                AppResult.Success(cachedGenres.map { it.toDomain() })
            } else {
                AppResult.Error(e)
            }
        }
    }
}
