package id.dpradana.themoviedb.core.network.api.model

import com.google.gson.annotations.SerializedName

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class GenreResponseDto(
    @SerializedName("genres")
    val genres: List<GenreDto>
)
