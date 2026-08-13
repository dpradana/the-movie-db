package id.dpradana.themoviedb.core.network.api

import id.dpradana.themoviedb.core.network.api.model.GenreResponseDto
import id.dpradana.themoviedb.core.network.api.model.MovieDetailDto
import id.dpradana.themoviedb.core.network.api.model.MovieResponseDto
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
}
