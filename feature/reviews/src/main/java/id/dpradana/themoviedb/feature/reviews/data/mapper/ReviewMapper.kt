package id.dpradana.themoviedb.feature.reviews.data.mapper

import id.dpradana.themoviedb.core.network.api.model.MovieDetailDto
import id.dpradana.themoviedb.core.network.api.model.ReviewDto
import id.dpradana.themoviedb.core.network.api.model.ReviewResponseDto
import id.dpradana.themoviedb.feature.reviews.domain.model.MovieSummary
import id.dpradana.themoviedb.feature.reviews.domain.model.Review
import id.dpradana.themoviedb.feature.reviews.domain.model.ReviewPage

fun ReviewResponseDto.toDomain(): ReviewPage {
    return ReviewPage(
        reviews = results.map { it.toDomain() },
        page = page,
        totalPages = totalPages
    )
}

fun ReviewDto.toDomain(): Review {
    return Review(
        id = id,
        authorName = authorDetails?.name
            ?: authorDetails?.username
            ?: author
            ?: "Anonymous",
        username = authorDetails?.username.orEmpty(),
        avatarPath = authorDetails?.avatarPath,
        rating = authorDetails?.rating,
        content = content.orEmpty(),
        createdAt = createdAt,
        url = url
    )
}

fun MovieDetailDto.toDomain(): MovieSummary {
    return MovieSummary(
        title = title.orEmpty(),
        averageRating = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}
