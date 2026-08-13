package id.dpradana.themoviedb.feature.movie.di

import dagger.Binds
import dagger.Module
import id.dpradana.themoviedb.feature.movie.data.repository.MovieRepositoryImpl
import id.dpradana.themoviedb.feature.movie.domain.repository.MovieRepository

@Module
abstract class MovieModule {
    @Binds
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository
}
