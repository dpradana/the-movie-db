package id.dpradana.themoviedb.feature.genre.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GenreEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GenreDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
}
