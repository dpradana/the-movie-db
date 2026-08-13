package id.dpradana.themoviedb.feature.reviews.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.data.mapper.toDomain
import id.dpradana.themoviedb.feature.reviews.data.source.VideoRemoteDataSource
import id.dpradana.themoviedb.feature.reviews.domain.model.Trailer
import id.dpradana.themoviedb.feature.reviews.domain.repository.TrailerRepository
import javax.inject.Inject

class TrailerRepositoryImpl @Inject constructor(
    private val remoteDataSource: VideoRemoteDataSource
) : TrailerRepository {
    override suspend fun getMovieVideos(movieId: Int): AppResult<List<Trailer>> {
        return try {
            val response = remoteDataSource.getMovieVideos(movieId)
            AppResult.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }
}
