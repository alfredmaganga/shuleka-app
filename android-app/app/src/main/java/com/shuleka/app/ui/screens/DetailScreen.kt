package com.shuleka.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuleka.app.data.Post
import com.shuleka.app.data.PostCategory
import com.shuleka.app.data.SupabaseClient
import com.shuleka.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(postId: String, onBack: () -> Unit) {
    var post by remember { mutableStateOf<Post?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(postId) {
        try {
            val allPosts = SupabaseClient.getPosts()
            post = allPosts.find { it.id == postId }
        } catch (_: Exception) {}
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taarifa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Rudi")
                    }
                },
                actions = {
                    post?.let { p ->
                        if (p.pdfUrl != null) {
                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(p.pdfUrl))
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = "Fungua PDF")
                            }
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, p.title)
                                putExtra(Intent.EXTRA_TEXT, "${p.title}\n\n${p.body}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Shiriki"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Shiriki")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            post == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Taarifa haijapatikana", color = TextSecondary)
                    }
                }
            }
            else -> {
                val p = post!!
                val category = PostCategory.fromString(p.category)
                val categoryColor = when (category) {
                    PostCategory.MATOKEO -> ColorMatokeo
                    PostCategory.TAARIFA -> ColorTaarifa
                    PostCategory.NOTES -> ColorNotes
                    PostCategory.VIPIMO -> ColorVipimo
                    PostCategory.MENGINEYO -> ColorMengineyo
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Background)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(categoryColor)
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                "${category.icon} ${category.label}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                p.title,
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                formatDate(p.createdAt),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Content
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (p.body.isNotBlank()) {
                            Text(
                                p.body,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                lineHeight = 26.sp
                            )
                        }

                        // PDF section
                        if (p.pdfUrl != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📄", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                "PDF Imepakiwa",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = PrimaryDark
                                            )
                                            Text(
                                                "Bofya kuangalia",
                                                fontSize = 13.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(p.pdfUrl))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                    ) {
                                        Icon(
                                            Icons.Default.OpenInBrowser,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text("Fungua PDF")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
