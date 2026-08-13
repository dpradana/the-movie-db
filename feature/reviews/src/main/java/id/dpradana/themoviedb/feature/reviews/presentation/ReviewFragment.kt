package id.dpradana.themoviedb.feature.reviews.presentation

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.dpradana.themoviedb.core.common.R as CommonR
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.feature.reviews.databinding.FragmentReviewsBinding
import id.dpradana.themoviedb.feature.reviews.di.DaggerReviewComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

class ReviewFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ReviewViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewAdapter: ReviewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerReviewComponent.factory().create(movieApi).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val movieId = arguments?.getInt("movieId") ?: -1
        viewModel.setMovieId(movieId)
        
        setupRecyclerView()
        setupListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(
            onRetryClick = { viewModel.retry() }
        )
        
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reviewAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    
                    if (lastVisibleItemPosition + 3 >= totalItemCount) {
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

    private fun renderState(state: ReviewUiState) {
        binding.pbLoading.isVisible = state is ReviewUiState.Loading
        binding.rvReviews.isVisible = state is ReviewUiState.Success
        binding.layoutError.root.isVisible = state is ReviewUiState.Error
        binding.layoutEmpty.isVisible = state is ReviewUiState.Empty
        binding.cvRatingSummary.isVisible = state is ReviewUiState.Success
        
        binding.llLoadMore.isVisible = false

        when (state) {
            is ReviewUiState.Success -> {
                reviewAdapter.submitList(state.reviews)
                reviewAdapter.setState(state.isLoadingMore, state.paginationError)
                
                binding.tvMovieTitle.text = state.movieTitle
                binding.tvMovieTitle.isVisible = !state.movieTitle.isNullOrEmpty()
                
                state.averageRating?.let { rating ->
                    binding.tvAverageRating.text = DecimalFormat("#.1").format(rating)
                    
                    val label = when {
                        rating >= 8.0 -> "Outstanding"
                        rating >= 7.0 -> "Good"
                        rating >= 5.0 -> "Average"
                        else -> "Poor"
                    }
                    binding.tvRatingLabel.text = label
                }

                state.voteCount?.let { count ->
                    val formattedCount = DecimalFormat("#,###").format(count)
                    binding.tvVoteCount.text = "Based on $formattedCount reviews"
                    binding.tvVoteCount.isVisible = true
                } ?: run {
                    binding.tvVoteCount.isVisible = false
                }
            }
            is ReviewUiState.Error -> {
                val errorMsg = getString(CommonR.string.error_failed_to_load_pattern, "reviews") + "\n" +
                        getString(CommonR.string.error_message_generic)
                binding.layoutError.root.findViewById<TextView>(CommonR.id.tvErrorMessage)?.text = errorMsg
            }
            is ReviewUiState.Empty -> {
                binding.layoutEmpty.setTitle(CommonR.string.review_empty_title)
                binding.layoutEmpty.setMessage(CommonR.string.review_empty_message)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
