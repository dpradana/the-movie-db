package id.dpradana.themoviedb.feature.detail.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import id.dpradana.themoviedb.core.common.R as CommonR
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val movieId = arguments?.getInt("movieId") ?: -1
        viewModel.loadMovieDetail(movieId)
        
        setupListeners()
        observeUiState()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.layoutError.root.findViewById<Button>(CommonR.id.btnRetry)?.setOnClickListener {
            viewModel.retry()
        }
        binding.layoutError.root.findViewById<View>(CommonR.id.tvGoBack)?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = (scrollY.toFloat() / 400f).coerceIn(0f, 1f)
            binding.vHeaderBg.alpha = alpha
            binding.tvHeaderTitle.alpha = alpha
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
        binding.scrollView.isVisible = state is MovieDetailUiState.Success
        binding.layoutError.root.isVisible = state is MovieDetailUiState.Error

        when (state) {
            is MovieDetailUiState.Success -> {
                bindMovie(state.movie)
            }
            is MovieDetailUiState.Error -> {
                val errorMsg = getString(CommonR.string.error_failed_to_load_pattern, "movie details") + "\n" +
                        getString(CommonR.string.error_message_generic)
                binding.layoutError.root.findViewById<TextView>(CommonR.id.tvErrorMessage)?.text = errorMsg
            }
            else -> {}
        }
    }

    private fun bindMovie(movie: MovieDetail) {
        binding.tvTitle.text = movie.title
        binding.tvHeaderTitle.text = movie.title
        binding.tvOverview.text = movie.overview
        binding.tvRuntime.text = "${movie.runtime}m"
        binding.tvRating.text = "★ ${movie.rating}/10"
        binding.tvReleaseDate.text = movie.releaseDate?.take(4) ?: ""
        binding.tvGenresShort.text = movie.genres.joinToString { it.name }
        binding.tvStatus.text = movie.status

        binding.ivPoster.load("https://image.tmdb.org/t/p/w500${movie.posterPath}") {
            crossfade(true)
            placeholder(CommonR.color.background_card)
        }
        binding.ivBackdrop.load("https://image.tmdb.org/t/p/original${movie.backdropPath}") {
            crossfade(true)
        }

        binding.btnReviews.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("movieId", movie.id)
                putString("movieTitle", movie.title)
                putFloat("averageRating", movie.rating.toFloat())
                putInt("voteCount", movie.voteCount)
            }
            findNavController().navigate(CommonR.id.action_movieDetailFragment_to_reviewFragment, bundle)
        }

        binding.btnTrailer.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("movieId", movie.id)
                putString("movieTitle", movie.title)
            }
            findNavController().navigate(CommonR.id.action_movieDetailFragment_to_trailerFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
