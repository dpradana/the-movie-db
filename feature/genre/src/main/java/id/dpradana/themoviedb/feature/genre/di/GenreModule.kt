package id.dpradana.themoviedb.feature.genre.di

import dagger.Binds
import dagger.Module
import id.dpradana.themoviedb.feature.genre.data.repository.GenreRepositoryImpl
import id.dpradana.themoviedb.feature.genre.domain.repository.GenreRepository

@Module
abstract class GenreModule {

    @Binds
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl
    ): GenreRepository
}
