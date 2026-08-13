package id.dpradana.themoviedb.feature.detail.data.mapper

import id.dpradana.themoviedb.core.network.api.model.GenreDto
import id.dpradana.themoviedb.core.network.api.model.MovieDetailDto
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail
import id.dpradana.themoviedb.feature.detail.domain.model.MovieGenre

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        rating = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        runtime = runtime,
        genres = genres?.map { it.toDomain() }.orEmpty(),
        tagline = tagline,
        status = status.orEmpty()
    )
}

fun GenreDto.toDomain(): MovieGenre {
    return MovieGenre(
        id = id,
        name = name
    )
}
