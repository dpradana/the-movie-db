package id.dpradana.themoviedb.feature.reviews.presentation

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.feature.reviews.databinding.FragmentReviewsBinding
import id.dpradana.themoviedb.feature.reviews.di.DaggerReviewComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReviewFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ReviewViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private val adapter = ReviewAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerReviewComponent.factory().create(movieApi).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val movieId = arguments?.getInt("movieId") ?: -1
        val movieTitle = arguments?.getString("movieTitle") ?: ""
        val averageRating = arguments?.getFloat("averageRating") ?: 0f
        val voteCount = arguments?.getInt("voteCount") ?: 0

        setupHeader(movieTitle, averageRating, voteCount)
        setupRecyclerView()
        setupListeners()
        observeUiState()
        
        viewModel.setMovieId(movieId)
    }

    private fun setupHeader(title: String, rating: Float, count: Int) {
        binding.apply {
            tvMovieTitle.text = title
            tvAverageRating.text = String.format(java.util.Locale.US, "%.1f", rating)
            tvVoteCount.text = "Based on ${java.text.NumberFormat.getInstance().format(count)} reviews"
            
            tvRatingLabel.text = when {
                rating >= 8 -> "Outstanding"
                rating >= 7 -> "Good"
                rating >= 5 -> "Average"
                else -> "Poor"
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvReviews.adapter = adapter
        binding.rvReviews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
                    viewModel.loadNextPage()
                }
            }
        })
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

    private fun renderState(state: ReviewUiState) {
        binding.apply {
            pbLoading.isVisible = state.isLoading
            errorLayout.isVisible = state.error != null
            tvEmpty.isVisible = !state.isLoading && state.error == null && state.reviews.isEmpty()
            rvReviews.isVisible = state.reviews.isNotEmpty()
            llLoadMore.isVisible = state.isLoadingMore
            
            if (state.error != null) {
                tvErrorMessage.text = state.error
            }
            
            adapter.submitList(state.reviews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
