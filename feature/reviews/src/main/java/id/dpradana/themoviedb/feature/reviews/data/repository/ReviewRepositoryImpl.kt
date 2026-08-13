package id.dpradana.themoviedb.feature.reviews.data.repository

import id.dpradana.themoviedb.core.common.AppResult
import id.dpradana.themoviedb.feature.reviews.data.mapper.toDomain
import id.dpradana.themoviedb.feature.reviews.data.source.ReviewRemoteDataSource
import id.dpradana.themoviedb.feature.reviews.domain.model.MovieSummary
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage
import id.dpradana.themoviedb.feature.reviews.domain.repository.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource
) : ReviewRepository {

    override suspend fun getReviews(
        movieId: Int,
        page: Int
    ): AppResult<ReviewPage> {
        return try {
            val response = remoteDataSource.getReviews(movieId, page)
            AppResult.Success(response.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun getMovieSummary(
        movieId: Int
    ): AppResult<MovieSummary> {
        return try {
            val response = remoteDataSource.getMovieDetail(movieId)
            AppResult.Success(response.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }
}
