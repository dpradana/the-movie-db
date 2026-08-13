package id.dpradana.themoviedb.feature.reviews.di

import dagger.Binds
import dagger.Module
import id.dpradana.themoviedb.feature.reviews.data.repository.ReviewRepositoryImpl
import id.dpradana.themoviedb.feature.reviews.data.repository.TrailerRepositoryImpl
import id.dpradana.themoviedb.feature.reviews.domain.repository.ReviewRepository
import id.dpradana.themoviedb.feature.reviews.domain.repository.TrailerRepository

@Module
abstract class ReviewModule {
    @Binds
    abstract fun bindReviewRepository(repository: ReviewRepositoryImpl): ReviewRepository

    @Binds
    abstract fun bindTrailerRepository(repository: TrailerRepositoryImpl): TrailerRepository
}
