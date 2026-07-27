package com.shuleka.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private var posts: List<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryDot: View = view.findViewById(R.id.categoryDot)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val bodyText: TextView = view.findViewById(R.id.bodyText)
        val pdfIndicator: LinearLayout = view.findViewById(R.id.pdfIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.titleText.text = post.title
        holder.categoryText.text = post.category.uppercase()

        // Category color
        val color = getCategoryColor(post.category)
        holder.categoryDot.setBackgroundColor(Color.parseColor(color))

        // Date
        holder.dateText.text = formatDate(post.createdAt)

        // Body preview
        if (post.body.isNotBlank()) {
            holder.bodyText.text = post.body
            holder.bodyText.visibility = View.VISIBLE
        } else {
            holder.bodyText.visibility = View.GONE
        }

        // PDF indicator
        if (post.pdfUrl.isNotBlank()) {
            holder.pdfIndicator.visibility = View.VISIBLE
        } else {
            holder.pdfIndicator.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onClick(post) }

        // Animate entry
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 30f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay((position * 50).toLong())
            .start()
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    private fun getCategoryColor(category: String): String {
        return when (category.lowercase()) {
            "matokeo" -> "#4F46E5"
            "taarifa" -> "#F59E0B"
            "notes" -> "#10B981"
            "vipimo" -> "#EF4444"
            "matukio" -> "#8B5CF6"
            else -> "#6B7280"
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            if (dateStr.isBlank()) return ""
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val date = inputFormat.parse(dateStr.replace("Z", "").take(19))
            if (date != null) outputFormat.format(date) else dateStr.take(10)
        } catch (e: Exception) {
            dateStr.take(10)
        }
    }
}
