package com.shuleka.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val title = intent.getStringExtra("title") ?: ""
        val body = intent.getStringExtra("body") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val pdfUrl = intent.getStringExtra("pdfUrl") ?: ""
        val createdAt = intent.getStringExtra("createdAt") ?: ""

        val toolbarTitle = findViewById<TextView>(R.id.toolbarTitle)
        val detailCategory = findViewById<TextView>(R.id.detailCategory)
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailDate = findViewById<TextView>(R.id.detailDate)
        val detailBody = findViewById<TextView>(R.id.detailBody)
        val pdfButton = findViewById<Button>(R.id.pdfButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        toolbarTitle.text = "Shuleka"
        detailCategory.text = category.uppercase()
        detailTitle.text = title
        detailBody.text = body
        detailDate.text = formatDate(createdAt)

        if (pdfUrl.isNotBlank()) {
            pdfButton.visibility = android.view.View.VISIBLE
            pdfButton.setOnClickListener {
                // Open PDF inside the app
                val intent = Intent(this, PdfViewerActivity::class.java).apply {
                    putExtra("pdfUrl", pdfUrl)
                    putExtra("title", title)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Fade in animation
        detailTitle.alpha = 0f
        detailTitle.translationY = 20f
        detailTitle.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(100).start()

        detailBody.alpha = 0f
        detailBody.translationY = 20f
        detailBody.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(200).start()
    }

    private fun formatDate(dateStr: String): String {
        return try {
            if (dateStr.isBlank()) return ""
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
            val outputFormat = java.text.SimpleDateFormat("dd MMMM yyyy 'saa' HH:mm", java.util.Locale.US)
            val date = inputFormat.parse(dateStr.replace("Z", "").take(19))
            if (date != null) outputFormat.format(date) else dateStr.take(10)
        } catch (e: Exception) {
            dateStr.take(10)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
