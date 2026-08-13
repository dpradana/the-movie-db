package id.dpradana.themoviedb.feature.genre.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenreLocalDataSource @Inject constructor(
    private val genreDao: GenreDao
) {
    fun observeGenres(): Flow<List<GenreEntity>> {
        return genreDao.observeGenres()
    }

    suspend fun getGenres(): List<GenreEntity> {
        return genreDao.getGenres()
    }

    suspend fun replaceGenres(genres: List<GenreEntity>) {
        genreDao.replaceGenres(genres)
    }
}
