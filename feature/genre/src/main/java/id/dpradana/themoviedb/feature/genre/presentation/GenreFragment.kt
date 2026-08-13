package id.dpradana.themoviedb.feature.genre.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.feature.genre.databinding.FragmentGenreBinding
import id.dpradana.themoviedb.feature.genre.di.DaggerGenreComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class GenreFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: GenreViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentGenreBinding? = null
    private val binding get() = _binding!!

    private lateinit var genreAdapter: GenreAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerGenreComponent.factory().create(movieApi).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        genreAdapter = GenreAdapter { genre ->
            // Navigation placeholder
            Toast.makeText(requireContext(), "Clicked: ${genre.name}", Toast.LENGTH_SHORT).show()
            navigateToMovieList(genre.id, genre.name)
        }
        binding.rvGenres.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = genreAdapter
        }
    }

    private fun setupListeners() {
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

    private fun renderState(state: GenreUiState) {
        binding.progressBar.isVisible = state is GenreUiState.Loading
        binding.rvGenres.isVisible = state is GenreUiState.Success
        binding.errorContainer.isVisible = state is GenreUiState.Error
        binding.tvEmptyMessage.isVisible = state is GenreUiState.Empty

        when (state) {
            is GenreUiState.Success -> {
                genreAdapter.submitList(state.genres)
            }
            is GenreUiState.Error -> {
                binding.tvErrorMessage.text = state.message
            }
            else -> {}
        }
    }

    private fun navigateToMovieList(genreId: Int, genreName: String) {
        // Navigation action prepared
        // For now, it's just a placeholder as feature:movie is not implemented.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
