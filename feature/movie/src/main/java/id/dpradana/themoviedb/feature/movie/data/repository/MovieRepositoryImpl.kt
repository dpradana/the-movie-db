package id.dpradana.themoviedb.feature.movie.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.movie.data.mapper.toDomain
import id.dpradana.themoviedb.feature.movie.data.source.MovieRemoteDataSource
import id.dpradana.themoviedb.feature.movie.domain.model.MoviePage
import id.dpradana.themoviedb.feature.movie.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: MovieRemoteDataSource
) : MovieRepository {

    override suspend fun discoverMovies(
        genreId: Int,
        page: Int
    ): AppResult<MoviePage> {
        return try {
            val response = remoteDataSource.discoverMovies(genreId, page)
            AppResult.Success(response.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }
}
