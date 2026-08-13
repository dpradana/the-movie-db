package id.dpradana.themoviedb.feature.reviews.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.dpradana.themoviedb.feature.reviews.databinding.ItemReviewBinding
import id.dpradana.themoviedb.feature.reviews.domain.model.Review

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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
                        imageView.alpha = 0.5f // Simple half star representation
                    } else {
                        imageView.alpha = 0.2f
                    }
                }
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

    private object DiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean =
            oldItem == newItem
    }
}
