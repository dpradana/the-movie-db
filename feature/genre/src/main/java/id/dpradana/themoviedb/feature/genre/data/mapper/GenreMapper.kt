package id.dpradana.themoviedb.feature.genre.data.mapper

import id.dpradana.themoviedb.core.network.api.model.GenreDto
import id.dpradana.themoviedb.feature.genre.domain.model.Genre

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
