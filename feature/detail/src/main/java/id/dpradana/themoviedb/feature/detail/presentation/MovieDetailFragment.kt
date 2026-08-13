package id.dpradana.themoviedb.feature.detail.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.feature.detail.databinding.FragmentMovieDetailBinding
import id.dpradana.themoviedb.feature.detail.di.DaggerMovieDetailComponent
import id.dpradana.themoviedb.feature.detail.domain.model.MovieDetail
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MovieDetailViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerMovieDetailComponent.factory().create(movieApi).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
        observeUiState()
        setupScrollListener()
        
        val movieId = arguments?.getInt("movieId") ?: -1
        viewModel.getMovieDetail(movieId)
    }

    private fun setupScrollListener() {
        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = (scrollY.toFloat() / 300f).coerceIn(0f, 1f)
            binding.vHeaderBg.alpha = alpha
            binding.tvHeaderTitle.alpha = alpha
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRetry.setOnClickListener {
            viewModel.retry()
        }
        binding.btnTrailer.setOnClickListener {
            // Future contract
        }
        binding.btnReviews.setOnClickListener {
            // Future contract
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                renderState(state)
            }
        }
    }

    private fun renderState(state: MovieDetailUiState) {
        binding.progressBar.isVisible = state is MovieDetailUiState.Loading
        binding.errorContainer.isVisible = state is MovieDetailUiState.Error
        binding.scrollView.isVisible = state is MovieDetailUiState.Success

        when (state) {
            is MovieDetailUiState.Success -> {
                displayMovieDetail(state.movie)
            }
            is MovieDetailUiState.Error -> {
                binding.tvErrorMessage.text = state.message
            }
            MovieDetailUiState.Loading -> {
                // Already handled by progressBar.isVisible
            }
        }
    }

    private fun displayMovieDetail(movie: MovieDetail) {
        binding.apply {
            ivBackdrop.load("https://image.tmdb.org/t/p/w780${movie.backdropPath}") {
                crossfade(true)
            }
            ivPoster.load("https://image.tmdb.org/t/p/w500${movie.posterPath}") {
                crossfade(true)
            }
            tvTitle.text = movie.title
            tvHeaderTitle.text = movie.title
            
            val rating = movie.rating
            tvRating.text = "★ ${String.format("%.1f", rating)}/10"
            
            tvReleaseDate.text = movie.releaseDate?.take(4) ?: ""
            
            val hours = (movie.runtime ?: 0) / 60
            val minutes = (movie.runtime ?: 0) % 60
            tvRuntime.text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            
            tvGenresShort.text = movie.genres.joinToString(", ") { it.name }
            
            tvOverview.text = movie.overview
            tvStatus.text = movie.status
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
