package id.dpradana.themoviedb.feature.genre.data.remote

import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.core.network.api.model.GenreResponseDto
import javax.inject.Inject

class GenreRemoteDataSource @Inject constructor(
    private val movieApi: MovieApi
) {

    suspend fun getGenres(): GenreResponseDto {
        return movieApi.getMovieGenres()
    }
}
