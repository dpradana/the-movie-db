package id.dpradana.themoviedb.feature.movie.di

import dagger.BindsInstance
import dagger.Component
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.feature.movie.presentation.MovieFragment

@Component(modules = [MovieModule::class, MovieViewModelModule::class])
interface MovieComponent {
    fun inject(fragment: MovieFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance movieApi: MovieApi): MovieComponent
    }
}
