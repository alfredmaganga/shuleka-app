package com.shuleka.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SUPABASE_URL = "https://uiwgbviucbbqcxdkpdwa.supabase.co"
        private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg"
        private val CATEGORIES = listOf("Zote", "Matokeo", "Taarifa", "Notes", "Vipimo", "Matukio")
        private const val NOTIFICATION_PERMISSION_CODE = 1001
    }

    private lateinit var tabContainer: LinearLayout
    private lateinit var postList: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var loadingContainer: View
    private lateinit var emptyContainer: View
    private lateinit var errorContainer: View
    private lateinit var retryButton: View
    private lateinit var adapter: PostAdapter

    private var allPosts = listOf<Post>()
    private var selectedCategory = "Zote"
    private var tabViews = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        tabContainer = findViewById(R.id.tabContainer)
        postList = findViewById(R.id.postList)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        loadingContainer = findViewById(R.id.loadingContainer)
        emptyContainer = findViewById(R.id.emptyContainer)
        errorContainer = findViewById(R.id.errorContainer)
        retryButton = findViewById(R.id.retryButton)

        setupTabs()
        setupRecyclerView()
        setupSwipeRefresh()
        setupRetry()

        loadPosts()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    private fun setupTabs() {
        tabContainer.removeAllViews()
        tabViews.clear()

        for (category in CATEGORIES) {
            val tabView = LayoutInflater.from(this)
                .inflate(R.layout.item_category_tab, tabContainer, false)

            val label = tabView.findViewById<TextView>(R.id.tabLabel)
            val indicator = tabView.findViewById<View>(R.id.tabIndicator)

            label.text = category

            if (category == selectedCategory) {
                label.setTextColor(Color.parseColor("#4F46E5"))
                label.setTypeface(null, Typeface.BOLD)
                indicator.visibility = View.VISIBLE
            } else {
                label.setTextColor(Color.parseColor("#6B7280"))
                label.setTypeface(null, Typeface.NORMAL)
                indicator.visibility = View.INVISIBLE
            }

            tabView.setOnClickListener {
                selectedCategory = category
                updateTabs()
                filterPosts()
            }

            tabContainer.addView(tabView)
            tabViews.add(tabView)
        }
    }

    private fun updateTabs() {
        for ((index, tabView) in tabViews.withIndex()) {
            val label = tabView.findViewById<TextView>(R.id.tabLabel)
            val indicator = tabView.findViewById<View>(R.id.tabIndicator)

            if (CATEGORIES[index] == selectedCategory) {
                label.setTextColor(Color.parseColor("#4F46E5"))
                label.setTypeface(null, Typeface.BOLD)
                indicator.visibility = View.VISIBLE
            } else {
                label.setTextColor(Color.parseColor("#6B7280"))
                label.setTypeface(null, Typeface.NORMAL)
                indicator.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(emptyList()) { post ->
            val intent = Intent(this, PostDetailActivity::class.java).apply {
                putExtra("title", post.title)
                putExtra("body", post.body)
                putExtra("category", post.category)
                putExtra("pdfUrl", post.pdfUrl)
                putExtra("createdAt", post.createdAt)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        postList.layoutManager = LinearLayoutManager(this)
        postList.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeColors(Color.parseColor("#4F46E5"))
        swipeRefresh.setOnRefreshListener { loadPosts() }
    }

    private fun setupRetry() {
        retryButton.setOnClickListener { loadPosts() }
    }

    private fun showState(state: String) {
        loadingContainer.visibility = if (state == "loading") View.VISIBLE else View.GONE
        swipeRefresh.visibility = if (state == "loaded") View.VISIBLE else View.GONE
        emptyContainer.visibility = if (state == "empty") View.VISIBLE else View.GONE
        errorContainer.visibility = if (state == "error") View.VISIBLE else View.GONE
    }

    private fun loadPosts() {
        showState("loading")

        Thread {
            try {
                val url = URL("$SUPABASE_URL/rest/v1/posts?select=*&order=created_at.desc")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("apikey", SUPABASE_ANON_KEY)
                conn.setRequestProperty("Authorization", "Bearer $SUPABASE_ANON_KEY")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    val text = conn.inputStream.bufferedReader().readText()
                    conn.disconnect()

                    val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                    val rawPosts: List<Map<String, Any>> = Gson().fromJson(text, type)

                    val posts = rawPosts.map { map ->
                        Post(
                            id = map["id"]?.toString() ?: "",
                            title = map["title"]?.toString() ?: "",
                            body = map["body"]?.toString() ?: "",
                            category = map["category"]?.toString() ?: "",
                            pdfUrl = map["pdf_url"]?.toString() ?: "",
                            createdAt = map["created_at"]?.toString() ?: "",
                            createdBy = map["created_by"]?.toString() ?: ""
                        )
                    }

                    runOnUiThread {
                        allPosts = posts
                        filterPosts()
                        swipeRefresh.isRefreshing = false
                    }
                } else {
                    runOnUiThread {
                        showError("Hitilafu ya mtandao: $responseCode")
                        swipeRefresh.isRefreshing = false
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showError("Hitilafu: ${e.message}")
                    swipeRefresh.isRefreshing = false
                }
            }
        }.start()
    }

    private fun filterPosts() {
        val filtered = if (selectedCategory == "Zote") {
            allPosts
        } else {
            allPosts.filter {
                it.category.equals(selectedCategory, ignoreCase = true)
            }
        }

        adapter.updatePosts(filtered)

        if (filtered.isEmpty() && allPosts.isNotEmpty()) {
            showState("empty")
            findViewById<TextView>(R.id.emptyText).text = "Hakuna $selectedCategory"
        } else if (allPosts.isEmpty()) {
            showState("empty")
            findViewById<TextView>(R.id.emptyText).text = "Hakuna taarifa bado"
        } else {
            showState("loaded")
        }
    }

    private fun showError(message: String) {
        showState("error")
        findViewById<TextView>(R.id.errorDetail).text = message
    }

    override fun onResume() {
        super.onResume()
        if (allPosts.isEmpty()) {
            loadPosts()
        }
    }
}
