package id.dpradana.themoviedb.di

import dagger.Component
import id.dpradana.themoviedb.MainActivity
import id.dpradana.themoviedb.MovieExplorerApplication
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.core.network.di.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class
    ]
)
interface AppComponent {

    fun inject(application: MovieExplorerApplication)

    fun inject(activity: MainActivity)

    fun movieApi(): MovieApi
}
