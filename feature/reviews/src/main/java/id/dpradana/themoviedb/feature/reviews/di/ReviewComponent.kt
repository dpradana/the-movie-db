package id.dpradana.themoviedb.feature.reviews.di

import dagger.BindsInstance
import dagger.Component
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.feature.reviews.presentation.ReviewFragment
import id.dpradana.themoviedb.feature.reviews.presentation.TrailerFragment

@Component(modules = [ReviewModule::class, ReviewViewModelModule::class])
interface ReviewComponent {
    fun inject(fragment: ReviewFragment)
    fun inject(fragment: TrailerFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance movieApi: MovieApi): ReviewComponent
    }
}
