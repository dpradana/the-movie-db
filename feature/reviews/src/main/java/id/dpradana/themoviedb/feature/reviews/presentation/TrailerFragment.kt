package id.dpradana.themoviedb.feature.reviews.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import id.dpradana.themoviedb.core.network.di.AppComponentProvider
import id.dpradana.themoviedb.feature.reviews.databinding.FragmentTrailerBinding
import id.dpradana.themoviedb.feature.reviews.di.DaggerReviewComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrailerFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TrailerViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentTrailerBinding? = null
    private val binding get() = _binding!!

    private var videoId: String = ""
    private var previousOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val movieApi = (requireActivity().application as AppComponentProvider).movieApi()
        DaggerReviewComponent.factory().create(movieApi).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrailerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousOrientation = savedInstanceState?.getInt(STATE_PREVIOUS_ORIENTATION)
            ?: requireActivity().requestedOrientation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterFullscreenLandscape()
        
        val movieId = arguments?.getInt("movieId") ?: -1
        val movieTitle = arguments?.getString("movieTitle") ?: ""
        
        binding.tvTitle.text = movieTitle
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        observeUiState()
        viewModel.getMovieVideos(movieId)

    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is TrailerUiState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is TrailerUiState.Success -> {
                        binding.progressBar.isVisible = false
                        val trailer = state.trailers.firstOrNull {
                            it.site == "YouTube" &&
                            it.type == "Trailer" &&
                            it.official
                        } ?: state.trailers.firstOrNull { it.site == "YouTube" }
                        
                        if (trailer != null) {
                            videoId = trailer.key
                            binding.tvTrailerName.text = trailer.name

                            lifecycleScope.launch {
                                delay(DELAY_INIT)
                                initYoutubeConfiguration()
                            }
                        } else {
                            Toast.makeText(requireContext(), "No trailer available", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                    is TrailerUiState.Error -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initYoutubeConfiguration() {
        if (_binding == null) return
        binding.apply {
            // Enable JavaScript
            youtubeWebView.settings.javaScriptEnabled = true

            // Improve web performance
            youtubeWebView.settings.apply {
                cacheMode = WebSettings.LOAD_DEFAULT
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = false
                builtInZoomControls = false
                displayZoomControls = false
            }
            youtubeWebView.overScrollMode = View.OVER_SCROLL_NEVER
            youtubeWebView.isVerticalScrollBarEnabled = false
            youtubeWebView.isHorizontalScrollBarEnabled = false

            // Ensure links are handled inside the WebView
            youtubeWebView.settings.mediaPlaybackRequiresUserGesture = false
            youtubeWebView.webViewClient = WebViewClient()

            // YouTube requires an app identity for WebView embeds. This is its
            // documented Android approach for a direct embedded-player URL.
            youtubeWebView.loadUrl(
                getEmbedUrl(),
                mapOf("Referer" to "https://${requireContext().packageName}")
            )
        }
    }

    private fun getEmbedUrl(): String {
        return Uri.Builder()
            .scheme("https")
            .authority("www.youtube.com")
            .appendPath("embed")
            .appendPath(videoId)
            .appendQueryParameter("autoplay", "1")
            .appendQueryParameter("mute", "1")
            .appendQueryParameter("controls", "1")
            .appendQueryParameter("fs", "0")
            .appendQueryParameter("playsinline", "1")
            .appendQueryParameter("rel", "0")
            .appendQueryParameter("modestbranding", "1")
            .build()
            .toString()
    }

    override fun onDestroyView() {
        if (!requireActivity().isChangingConfigurations) {
            exitFullscreenLandscape()
        }
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_PREVIOUS_ORIENTATION, previousOrientation)
        super.onSaveInstanceState(outState)
    }

    private fun enterFullscreenLandscape() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        WindowInsetsControllerCompat(requireActivity().window, binding.root).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun exitFullscreenLandscape() {
        requireActivity().requestedOrientation = previousOrientation
        WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
            .show(WindowInsetsCompat.Type.systemBars())
    }

    companion object {
        private const val DELAY_INIT = 500L
        private const val STATE_PREVIOUS_ORIENTATION = "previous_orientation"
    }
}
