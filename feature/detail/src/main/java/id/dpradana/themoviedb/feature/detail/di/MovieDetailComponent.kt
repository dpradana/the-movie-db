package id.dpradana.themoviedb.feature.detail.di

import dagger.BindsInstance
import dagger.Component
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.feature.detail.presentation.MovieDetailFragment

@Component(modules = [MovieDetailModule::class, MovieDetailViewModelModule::class])
interface MovieDetailComponent {
    fun inject(fragment: MovieDetailFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance movieApi: MovieApi): MovieDetailComponent
    }
}
