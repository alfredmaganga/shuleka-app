package com.shuleka.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuleka.app.ui.theme.*
import kotlinx.coroutines.delay

private const val TAG = "Shuleka"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        try {
            setContent {
                ShulekaTheme {
                    App()
                }
            }
            Log.d(TAG, "setContent done")
        } catch (e: Exception) {
            Log.e(TAG, "Crash in onCreate", e)
            Toast.makeText(this, "Hitilafu: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

@Composable
fun App() {
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (hasError) {
        Box(
            modifier = Modifier.fillMaxSize().background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("😔", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Hitilafu imetokea", color = OnPrimary, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = TextOnDark, fontSize = 14.sp)
            }
        }
        return
    }

    if (showSplash) {
        SplashScreen {
            showSplash = false
        }
    } else {
        try {
            com.shuleka.app.ui.screens.AppNavigation()
        } catch (e: Exception) {
            Log.e(TAG, "Crash in navigation", e)
            hasError = true
            errorMessage = e.message ?: "Unknown error"
        }
    }
}

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        try {
            scale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            delay(1200)
            onDone()
        } catch (e: Exception) {
            Log.e(TAG, "Splash error", e)
            onDone()
        }
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
