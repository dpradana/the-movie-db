package id.dpradana.themoviedb

import android.app.Application
import id.dpradana.themoviedb.di.AppComponent
import id.dpradana.themoviedb.di.AppModule
import id.dpradana.themoviedb.di.DaggerAppComponent

class MovieExplorerApplication : Application() {

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
}
