package id.dpradana.themoviedb.feature.reviews.data.mapper

import id.dpradana.themoviedb.core.network.api.model.VideoDto
import id.dpradana.themoviedb.feature.reviews.domain.model.Trailer

fun VideoDto.toDomain(): Trailer {
    return Trailer(
        id = id,
        key = key,
        name = name,
        site = site,
        type = type,
        official = official
    )
}
