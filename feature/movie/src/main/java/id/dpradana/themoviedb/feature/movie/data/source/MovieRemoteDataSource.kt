package id.dpradana.themoviedb.feature.movie.data.source

import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.core.network.api.model.MovieResponseDto
import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val movieApi: MovieApi
) {

    suspend fun discoverMovies(
        genreId: Int,
        page: Int
    ): MovieResponseDto {
        return movieApi.discoverMovies(
            genreId = genreId,
            page = page
        )
    }
}
