package id.dpradana.themoviedb.feature.movie.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.dpradana.themoviedb.feature.movie.databinding.ItemMovieBinding
import id.dpradana.themoviedb.feature.movie.databinding.ItemMovieFooterBinding
import id.dpradana.themoviedb.feature.movie.domain.model.Movie

class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onRetryClick: () -> Unit
) : ListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    private var isLoadingMore: Boolean = false
    private var paginationError: String? = null

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    fun setState(isLoadingMore: Boolean, paginationError: String?) {
        val hadFooter = this.isLoadingMore || this.paginationError != null
        this.isLoadingMore = isLoadingMore
        this.paginationError = paginationError
        val hasFooter = this.isLoadingMore || this.paginationError != null
        
        if (hadFooter && !hasFooter) {
            notifyItemRemoved(super.getItemCount())
        } else if (!hadFooter && hasFooter) {
            notifyItemInserted(super.getItemCount())
        } else if (hadFooter && hasFooter) {
            notifyItemChanged(super.getItemCount())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) VIEW_TYPE_ITEM else VIEW_TYPE_FOOTER
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        return if (isLoadingMore || paginationError != null) count + 1 else count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MovieViewHolder(binding)
        } else {
            val binding = ItemMovieFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is FooterViewHolder) {
            holder.bind(isLoadingMore, paginationError)
        }
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.tvTitle.text = movie.title
            binding.tvRating.text = "★ ${movie.rating}"
            binding.tvYear.text = movie.releaseDate?.take(4) ?: ""
            
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            binding.ivPoster.load(imageUrl) {
                crossfade(true)
                placeholder(id.dpradana.themoviedb.core.common.R.color.background_card)
                error(id.dpradana.themoviedb.core.common.R.color.background_card)
            }
            
            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    inner class FooterViewHolder(private val binding: ItemMovieFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(isLoading: Boolean, error: String?) {
            binding.progressBar.isVisible = isLoading
            binding.errorContainer.isVisible = error != null
            binding.tvErrorMessage.text = error
            binding.btnRetry.setOnClickListener { onRetryClick() }
        }
    }

    private class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}
