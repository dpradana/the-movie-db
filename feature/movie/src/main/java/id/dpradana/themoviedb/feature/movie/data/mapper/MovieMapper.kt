package id.dpradana.themoviedb.feature.movie.data.mapper

import id.dpradana.themoviedb.core.network.api.model.MovieDto
import id.dpradana.themoviedb.core.network.api.model.MovieResponseDto
import id.dpradana.themoviedb.feature.movie.domain.model.Movie
import id.dpradana.themoviedb.feature.movie.domain.model.MoviePage

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = voteAverage ?: 0.0
    )
}

fun MovieResponseDto.toDomain(): MoviePage {
    return MoviePage(
        movies = results.map { it.toDomain() },
        page = page,
        totalPages = totalPages
    )
}
