package id.dpradana.themoviedb.core.network.api

import id.dpradana.themoviedb.core.network.api.model.GenreResponseDto
import id.dpradana.themoviedb.core.network.api.model.MovieDetailDto
import id.dpradana.themoviedb.core.network.api.model.MovieResponseDto
import id.dpradana.themoviedb.core.network.api.model.ReviewResponseDto
import id.dpradana.themoviedb.core.network.api.model.VideoResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreResponseDto

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int
    ): MovieResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int
    ): MovieDetailDto

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int
    ): ReviewResponseDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int
    ): VideoResponseDto
}
