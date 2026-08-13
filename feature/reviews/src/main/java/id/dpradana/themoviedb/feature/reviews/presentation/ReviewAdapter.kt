package id.dpradana.themoviedb.feature.reviews.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.dpradana.themoviedb.core.common.R as CommonR
import id.dpradana.themoviedb.feature.reviews.R
import id.dpradana.themoviedb.feature.reviews.databinding.ItemReviewBinding
import id.dpradana.themoviedb.feature.reviews.domain.model.Review

class ReviewAdapter(
    private val onRetryClick: () -> Unit
) : ListAdapter<Review, RecyclerView.ViewHolder>(DiffCallback) {

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
            val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReviewViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReviewViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is FooterViewHolder) {
            holder.bind(isLoadingMore, paginationError)
        }
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.apply {
                tvAuthorName.text = review.authorName
                tvContent.text = review.content
                
                val formattedDate = ReviewDateFormatter.format(review.createdAt)
                tvDate.text = formattedDate
                tvDate.isVisible = formattedDate != null

                val rating5 = (review.rating ?: 0.0) / 2.0
                val stars = listOf(ivStar1, ivStar2, ivStar3, ivStar4, ivStar5)
                stars.forEachIndexed { index, imageView ->
                    val starLevel = index + 1
                    if (starLevel <= rating5) {
                        imageView.alpha = 1.0f
                    } else if (starLevel - 0.5 <= rating5) {
                        imageView.alpha = 0.5f 
                    } else {
                        imageView.alpha = 0.2f
                    }
                }
                
                tvRating.text = review.rating?.toString()
                tvRating.isVisible = review.rating != null
                llStars.isVisible = review.rating != null

                val avatarUrl = review.avatarPath?.let { path ->
                    if (path.startsWith("http")) path
                    else "https://image.tmdb.org/t/p/w185$path"
                }
                ivAvatar.load(avatarUrl) {
                    crossfade(true)
                }
            }
        }
    }

    inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(isLoading: Boolean, error: String?) {
            itemView.findViewById<ProgressBar>(R.id.progressBar)?.isVisible = isLoading
            itemView.findViewById<View>(R.id.errorContainer)?.isVisible = error != null
            itemView.findViewById<TextView>(R.id.tvErrorMessage)?.text = error ?: itemView.context.getString(CommonR.string.pagination_error_message)
            itemView.findViewById<Button>(R.id.btnRetry)?.setOnClickListener { onRetryClick() }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean =
            oldItem == newItem
    }
}
