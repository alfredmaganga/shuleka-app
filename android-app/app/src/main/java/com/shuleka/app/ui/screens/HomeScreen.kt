package com.shuleka.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuleka.app.data.Post
import com.shuleka.app.data.PostCategory
import com.shuleka.app.data.SupabaseClient
import com.shuleka.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onPostClick: (String) -> Unit) {
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("all") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun loadPosts() {
        isLoading = true
        errorMessage = null
        try {
            posts = SupabaseClient.getPosts(selectedCategory)
        } catch (e: Exception) {
            errorMessage = "Imeshindikana kupata taarifa"
        }
        isLoading = false
    }

    LaunchedEffect(selectedCategory) {
        loadPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📚", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Shuleka",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { scope.launch { loadPosts() } }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Sasisha",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
        ) {
            // Category Tabs
            CategoryTabs(
                selected = selectedCategory,
                onSelect = { selectedCategory = it }
            )

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("😔", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(errorMessage!!, color = TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { scope.launch { loadPosts() } }) {
                                Text("Jaribu Tena")
                            }
                        }
                    }
                }
                posts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Hakuna taarifa bado", color = TextSecondary)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(posts) { index, post ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300, delayMillis = index * 80)) +
                                        slideInVertically(tween(300, delayMillis = index * 80)) { it / 2 }
                            ) {
                                PostCard(post = post, onClick = { onPostClick(post.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf(
        "all" to "Zote",
        "matokeo" to "Matokeo",
        "taarifa" to "Taarifa",
        "notes" to "Notes",
        "vipimo" to "Vipimo",
        "mengineyo" to "Mengineyo"
    )

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (key, label) ->
            val isSelected = selected == key
            val bgColor = if (isSelected) Primary else Color(0xFFF1F5F9)
            val textColor = if (isSelected) Color.White else TextSecondary

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelect(key) },
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PostCard(post: Post, onClick: () -> Unit) {
    val category = PostCategory.fromString(post.category)
    val categoryColor = when (category) {
        PostCategory.MATOKEO -> ColorMatokeo
        PostCategory.TAARIFA -> ColorTaarifa
        PostCategory.NOTES -> ColorNotes
        PostCategory.VIPIMO -> ColorVipimo
        PostCategory.MENGINEYO -> ColorMengineyo
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${category.icon} ${category.label}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = categoryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (post.pdfUrl != null) {
                    Text("📄", fontSize = 16.sp)
                }
            }

            Text(
                text = post.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (post.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.body.take(120) + if (post.body.length > 120) "..." else "",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatDate(post.createdAt),
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

fun formatDate(dateStr: String): String {
    return try {
        val parts = dateStr.substringBefore("T").split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } catch (e: Exception) {
        dateStr
    }
}
