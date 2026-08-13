package id.dpradana.themoviedb.feature.genre.di

import dagger.BindsInstance
import dagger.Component
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.feature.genre.presentation.GenreFragment

@Component(modules = [GenreModule::class, ViewModelModule::class])
interface GenreComponent {
    fun inject(fragment: GenreFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance movieApi: MovieApi): GenreComponent
    }
}
