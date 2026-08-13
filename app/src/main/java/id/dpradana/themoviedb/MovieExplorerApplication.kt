package id.dpradana.themoviedb

import android.app.Application
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.di.AppComponent
import id.dpradana.themoviedb.di.AppModule
import id.dpradana.themoviedb.di.DaggerAppComponent

class MovieExplorerApplication : Application(), AppComponentProvider {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
            
        appComponent.inject(this)
    }

    override fun movieApi(): MovieApi = appComponent.movieApi()
}
