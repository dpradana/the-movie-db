package id.dpradana.themoviedb.feature.movie.presentation

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.dpradana.themoviedb.core.common.R as CommonR
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.feature.movie.databinding.FragmentMovieBinding
import id.dpradana.themoviedb.feature.movie.di.DaggerMovieComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MovieViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieAdapter: MovieAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerMovieComponent.factory().create(movieApi).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val genreId = arguments?.getInt("genreId") ?: -1
        val genreName = arguments?.getString("genreName") ?: "Movies"
        
        binding.tvGenreName.text = genreName
        viewModel.setGenre(genreId)
        
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onMovieClick = { movie ->
                val bundle = Bundle().apply { putInt("movieId", movie.id) }
                findNavController().navigate(CommonR.id.action_movieFragment_to_movieDetailFragment, bundle)
            },
            onRetryClick = { viewModel.retry() }
        )
        
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (movieAdapter.getItemViewType(position) == 1) 2 else 1
            }
        }
        
        binding.rvMovies.apply {
            this.layoutManager = layoutManager
            adapter = movieAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    
                    if (lastVisibleItemPosition + 5 >= totalItemCount) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.layoutError.root.findViewById<Button>(CommonR.id.btnRetry)?.setOnClickListener {
            viewModel.retry()
        }
        binding.layoutError.root.findViewById<View>(CommonR.id.tvGoBack)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                renderState(state)
            }
        }
    }

    private fun renderState(state: MovieUiState) {
        binding.progressBar.isVisible = state is MovieUiState.Loading
        binding.rvMovies.isVisible = state is MovieUiState.Success
        binding.layoutError.root.isVisible = state is MovieUiState.Error
        binding.layoutEmpty.isVisible = state is MovieUiState.Empty

        when (state) {
            is MovieUiState.Success -> {
                movieAdapter.submitList(state.movies)
                movieAdapter.setState(state.isLoadingMore, state.paginationError)
            }
            is MovieUiState.Error -> {
                val genreName = arguments?.getString("genreName") ?: "movies"
                binding.layoutError.root.findViewById<TextView>(CommonR.id.tvErrorMessage)?.text = 
                    getString(CommonR.string.error_failed_to_load_pattern, genreName) + "\n" +
                    getString(CommonR.string.error_message_generic)
            }
            is MovieUiState.Empty -> {
                binding.layoutEmpty.setTitle(CommonR.string.movie_empty_title)
                binding.layoutEmpty.setMessage(CommonR.string.movie_empty_message)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
