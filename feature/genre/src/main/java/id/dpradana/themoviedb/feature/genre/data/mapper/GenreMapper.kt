package id.dpradana.themoviedb.feature.genre.data.mapper

import id.dpradana.themoviedb.core.network.api.model.GenreDto
import id.dpradana.themoviedb.feature.genre.data.local.GenreEntity
import id.dpradana.themoviedb.feature.genre.domain.model.Genre

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun GenreDto.toEntity(): GenreEntity {
    return GenreEntity(
        id = id,
        name = name
    )
}

fun GenreEntity.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
