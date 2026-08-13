package id.dpradana.themoviedb.feature.reviews.presentation

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object ReviewDateFormatter {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun format(dateString: String?): String? {
        if (dateString == null) return null
        return try {
            val date = inputFormat.parse(dateString) ?: return null
            val now = System.currentTimeMillis()
            val diff = now - date.time

            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000} mins ago"
                diff < 86400000 -> "${diff / 3600000} hours ago"
                diff < 604800000 -> "${diff / 86400000} days ago"
                diff < 2592000000 -> "${diff / 604800000} weeks ago"
                else -> outputFormat.format(date)
            }
        } catch (e: Exception) {
            null
        }
    }
}
