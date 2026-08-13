package id.dpradana.themoviedb.feature.genre.presentation

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import id.dpradana.themoviedb.core.common.R as CommonR
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerGenreComponent.factory().create(context, movieApi).inject(this)
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
        val genreAdapter = GenreAdapter { genre ->
            navigateToMovieList(genre.id, genre.name)
        }
        binding.rvGenres.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = genreAdapter
        }
    }

    private fun setupListeners() {
        binding.layoutError.root.findViewById<Button>(CommonR.id.btnRetry)?.setOnClickListener {
            viewModel.retry()
        }
        binding.layoutError.root.findViewById<View>(CommonR.id.tvGoBack)?.isVisible = false
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
        binding.layoutError.root.isVisible = state is GenreUiState.Error
        binding.layoutEmpty.isVisible = state is GenreUiState.Empty

        when (state) {
            is GenreUiState.Success -> {
                (binding.rvGenres.adapter as? GenreAdapter)?.submitList(state.genres)
            }
            is GenreUiState.Error -> {
                val errorMsg = getString(CommonR.string.error_failed_to_load_pattern, "genres") + "\n" +
                        getString(CommonR.string.error_message_generic)
                binding.layoutError.root.findViewById<TextView>(CommonR.id.tvErrorMessage)?.text = errorMsg
            }
            is GenreUiState.Empty -> {
                binding.layoutEmpty.setTitle(CommonR.string.genre_empty_title)
                binding.layoutEmpty.setMessage(CommonR.string.genre_empty_message)
            }
            else -> {}
        }
    }

    private fun navigateToMovieList(genreId: Int, genreName: String) {
        val bundle = Bundle().apply {
            putInt("genreId", genreId)
            putString("genreName", genreName)
        }
        findNavController().navigate(CommonR.id.action_genreFragment_to_movieFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
