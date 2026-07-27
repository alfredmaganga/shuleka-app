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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuleka.app.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShulekaTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen { showSplash = false }
    } else {
        com.shuleka.app.ui.screens.AppNavigation()
    }
}

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(1200)
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
            modifier = Modifier.scale(scale.value)
        ) {
            Text("\uD83D\uDCDA", fontSize = 72.sp)
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
