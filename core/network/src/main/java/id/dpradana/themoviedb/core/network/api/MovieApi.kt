package id.dpradana.themoviedb.core.network.api

import id.dpradana.themoviedb.core.network.api.model.GenreResponseDto
import retrofit2.http.GET

interface MovieApi {

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreResponseDto
}
