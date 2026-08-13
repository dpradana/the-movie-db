package id.dpradana.themoviedb.feature.detail.di

import dagger.Binds
import dagger.Module
import id.dpradana.themoviedb.feature.detail.data.repository.MovieDetailRepositoryImpl
import id.dpradana.themoviedb.feature.detail.domain.repository.MovieDetailRepository

@Module
abstract class MovieDetailModule {
    @Binds
    abstract fun bindMovieDetailRepository(
        movieDetailRepositoryImpl: MovieDetailRepositoryImpl
    ): MovieDetailRepository
}
