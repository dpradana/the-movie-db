package id.dpradana.themoviedb.feature.genre.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.dpradana.themoviedb.feature.genre.databinding.ItemGenreBinding
import id.dpradana.themoviedb.feature.genre.domain.model.Genre

class GenreAdapter(
    private val onGenreClick: (Genre) -> Unit
) : ListAdapter<Genre, GenreAdapter.GenreViewHolder>(GenreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val backgrounds = listOf(
            id.dpradana.themoviedb.feature.genre.R.drawable.bg_genre_1,
            id.dpradana.themoviedb.feature.genre.R.drawable.bg_genre_2,
            id.dpradana.themoviedb.feature.genre.R.drawable.bg_genre_3,
            id.dpradana.themoviedb.feature.genre.R.drawable.bg_genre_placeholder
        )
        holder.bind(getItem(position), backgrounds[position % backgrounds.size])
    }

    inner class GenreViewHolder(
        private val binding: ItemGenreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: Genre, backgroundResId: Int) {
            binding.tvGenreName.text = genre.name
            binding.ivGenreBackground.setImageResource(backgroundResId)
            binding.root.setOnClickListener {
                onGenreClick(genre)
            }
        }
    }

    private class GenreDiffCallback : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }
    }
}
