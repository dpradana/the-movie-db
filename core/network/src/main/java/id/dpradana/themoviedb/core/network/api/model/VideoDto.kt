package id.dpradana.themoviedb.core.network.api.model

import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("site")
    val site: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("official")
    val official: Boolean
)

data class VideoResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<VideoDto>
)
