package id.dpradana.themoviedb.feature.movie.presentation

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
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
        inflater: LayoutInflater, container: ViewGroup?,
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
        
        setupRecyclerView()
        setupListeners()
        observeUiState()
        
        viewModel.setGenre(genreId)
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onMovieClick = { movie ->
                val bundle = Bundle().apply {
                    putInt("movieId", movie.id)
                }
                findNavController().navigate(
                    CommonR.id.action_movieFragment_to_movieDetailFragment,
                    bundle
                )
            },
            onRetryClick = {
                viewModel.retry()
            }
        )
        
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (movieAdapter.getItemViewType(position) == 1) 2 else 1
            }
        }
        
        binding.rvMovies.apply {
            layoutManager = gridLayoutManager
            adapter = movieAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager?.childCount ?: 0
                    val totalItemCount = layoutManager?.itemCount ?: 0
                    val firstVisibleItemPosition = (layoutManager as? GridLayoutManager)?.findFirstVisibleItemPosition() ?: 0
                    
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnRetry.setOnClickListener {
            viewModel.retry()
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
        binding.progressBar.isVisible = state.isLoading
        binding.rvMovies.isVisible = state.movies.isNotEmpty()
        binding.errorContainer.isVisible = state.error != null
        binding.tvEmptyMessage.isVisible = !state.isLoading && state.error == null && state.movies.isEmpty()
        
        if (state.error != null) {
            binding.tvErrorMessage.text = state.error
        }
        
        movieAdapter.submitList(state.movies)
        movieAdapter.setState(state.isLoadingMore, state.paginationError)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
