package id.dpradana.themoviedb.feature.reviews.domain.model

data class Review(
    val id: String,
    val authorName: String,
    val username: String,
    val avatarPath: String?,
    val rating: Double?,
    val content: String,
    val createdAt: String?,
    val url: String?
)

data class ReviewPage(
    val reviews: List<Review>,
    val page: Int,
    val totalPages: Int
)
