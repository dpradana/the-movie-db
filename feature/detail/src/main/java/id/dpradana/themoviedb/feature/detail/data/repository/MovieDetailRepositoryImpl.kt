package id.dpradana.themoviedb.feature.detail.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.detail.data.mapper.toDomain
import id.dpradana.themoviedb.feature.detail.data.source.MovieDetailRemoteDataSource
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail
import id.dpradana.themoviedb.feature.detail.domain.repository.MovieDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieDetailRepositoryImpl @Inject constructor(
    private val remoteDataSource: MovieDetailRemoteDataSource
) : MovieDetailRepository {

    override suspend fun getMovieDetail(movieId: Int): AppResult<MovieDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = remoteDataSource.getMovieDetail(movieId)
                AppResult.Success(response.toDomain())
            } catch (e: Exception) {
                AppResult.Error(e)
            }
        }
    }
}
