package id.dpradana.themoviedb.feature.detail.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val rating: Double,
    val voteCount: Int,
    val runtime: Int?,
    val genres: List<MovieGenre>,
    val tagline: String?,
    val status: String
)
