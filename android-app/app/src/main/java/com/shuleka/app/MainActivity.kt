package com.shuleka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuleka.app.navigation.ShulekaNavGraph
import com.shuleka.app.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShulekaTheme {
                ShulekaNavGraph()
            }
        }
    }
}

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            scale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        kotlinx.coroutines.delay(1500)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value).alpha(alpha.value)
        ) {
            Text("📚", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "SHULEKA",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = OnPrimary,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Taarifa za Shule",
                fontSize = 16.sp,
                color = TextOnDark,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShulekaNavGraph() {
    var showSplash by remember { mutableStateOf(true) }

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 } togetherWith
                    fadeOut(tween(300))
        },
        label = "splash"
    ) { isSplash ->
        if (isSplash) {
            SplashScreen { showSplash = false }
        } else {
            com.shuleka.app.ui.screens.AppNavigation()
        }
    }
}
