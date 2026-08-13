package id.dpradana.themoviedb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.dpradana.themoviedb.core.network.api.MovieApi
import id.dpradana.themoviedb.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var movieApi: MovieApi

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MovieExplorerApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // movieApi is now injected and can be used here later
    }
}
