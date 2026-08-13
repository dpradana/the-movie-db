package id.dpradana.themoviedb.feature.genre.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import id.dpradana.themoviedb.feature.genre.data.local.GenreDao
import id.dpradana.themoviedb.feature.genre.data.local.GenreDatabase
import id.dpradana.themoviedb.feature.genre.data.repository.GenreRepositoryImpl
import id.dpradana.themoviedb.feature.genre.domain.repository.GenreRepository
import javax.inject.Singleton

@Module
abstract class GenreModule {

    @Binds
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl
    ): GenreRepository

    companion object {
        @Provides
        @Singleton
        fun provideGenreDatabase(context: Context): GenreDatabase {
            return Room.databaseBuilder(
                context,
                GenreDatabase::class.java,
                "genre_database"
            ).build()
        }

        @Provides
        @Singleton
        fun provideGenreDao(database: GenreDatabase): GenreDao {
            return database.genreDao()
        }
    }
}
