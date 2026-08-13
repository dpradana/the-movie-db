package id.dpradana.themoviedb.core.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import id.dpradana.themoviedb.core.common.databinding.ViewEmptyStateContentBinding

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewEmptyStateContentBinding

    init {
        gravity = Gravity.CENTER
        orientation = VERTICAL
        val padding = (24 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)
        binding = ViewEmptyStateContentBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setTitle(title: CharSequence?) {
        binding.tvEmptyTitle.text = title
    }

    fun setTitle(@StringRes titleRes: Int) {
        binding.tvEmptyTitle.setText(titleRes)
    }

    fun setMessage(message: CharSequence?) {
        binding.tvEmptyMessage.text = message
    }

    fun setMessage(@StringRes messageRes: Int) {
        binding.tvEmptyMessage.setText(messageRes)
    }
}
