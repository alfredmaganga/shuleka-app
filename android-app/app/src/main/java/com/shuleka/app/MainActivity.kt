package com.shuleka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF5F5F5)
            ) {
                ShulekaApp()
            }
        }
    }
}

data class Post(
    val id: String,
    val title: String,
    val body: String,
    val category: String
)

@Composable
fun ShulekaApp() {
    var posts by remember { mutableStateOf(listOf<Post>()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val result = withContext(Dispatchers.IO) {
                val url = URL("https://uiwgbviucbbqcxdkpdwa.supabase.co/rest/v1/posts?select=*&order=created_at.desc")
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg")
                conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg")
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                val arr = JSONArray(text)
                val list = mutableListOf<Post>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(Post(o.optString("id",""), o.optString("title",""), o.optString("body",""), o.optString("category","")))
                }
                list
            }
            posts = result
        } catch (e: Exception) {
            posts = listOf(Post("", "Error", e.message ?: "unknown", "error"))
        }
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = Color(0xFF4F46E5), modifier = Modifier.fillMaxWidth()) {
            Text("Shuleka", modifier = Modifier.padding(16.dp), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(posts) { post ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(post.category.uppercase(), fontSize = 11.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(post.title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            if (post.body.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(post.body.take(150), fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
