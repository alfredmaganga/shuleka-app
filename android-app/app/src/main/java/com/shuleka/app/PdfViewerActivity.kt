package com.shuleka.app

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbarTitle: TextView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        webView = findViewById(R.id.pdfWebView)
        progressBar = findViewById(R.id.pdfProgressBar)
        toolbarTitle = findViewById(R.id.pdfToolbarTitle)
        val backButton = findViewById<ImageView>(R.id.pdfBackButton)

        val pdfUrl = intent.getStringExtra("pdfUrl") ?: ""
        val title = intent.getStringExtra("title") ?: "PDF"

        toolbarTitle.text = title

        backButton.setOnClickListener { finish() }

        // Configure WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }

        if (pdfUrl.isNotBlank()) {
            // Use Google Docs viewer to render PDF inside WebView
            val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
            webView.loadUrl(googleDocsUrl)
        } else {
            Toast.makeText(this, "Hakuna PDF", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
