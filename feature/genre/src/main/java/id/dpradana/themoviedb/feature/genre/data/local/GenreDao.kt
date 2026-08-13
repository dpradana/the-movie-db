package id.dpradana.themoviedb.feature.genre.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun observeGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM genres ORDER BY name ASC")
    suspend fun getGenres(): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("DELETE FROM genres")
    suspend fun clearGenres()

    @Transaction
    suspend fun replaceGenres(genres: List<GenreEntity>) {
        clearGenres()
        insertGenres(genres)
    }
}
