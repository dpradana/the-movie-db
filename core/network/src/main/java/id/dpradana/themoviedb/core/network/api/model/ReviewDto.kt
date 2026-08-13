package id.dpradana.themoviedb.core.network.api.model

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("author")
    val author: String?,
    @SerializedName("author_details")
    val authorDetails: ReviewAuthorDetailsDto?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("id")
    val id: String,
    @SerializedName("url")
    val url: String?
)

data class ReviewAuthorDetailsDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("avatar_path")
    val avatarPath: String?,
    @SerializedName("rating")
    val rating: Double?
)

data class ReviewResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<ReviewDto>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
