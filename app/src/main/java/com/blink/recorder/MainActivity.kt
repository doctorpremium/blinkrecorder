package com.blink.recorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF0B0B0C),
                    surface = Color(0xFF1E1E1E),
                    primary = Color(0xFFE8894A),
                    onPrimary = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlinkRecorderScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlinkRecorderScreen() {
    var isRecording by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }
    var facecamEnabled by remember { mutableStateOf(false) }
    var micEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000)
                seconds++
            }
        }
    }

    val formatTime = { s: Int ->
        val m = s / 60
        val sec = s % 60
        String.format("%02d:%02d", m, sec)
    }

    // Radial gradient background approximation dari referensi web (rgba(232,137,74,0.10))
    val bgGradient = Brush.radialGradient(
        colors = listOf(Color(0xFFE8894A).copy(alpha = 0.15f), Color(0xFF0B0B0C)),
        radius = 1500f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("👋 Hai!", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
                    Text("Mau merekam?", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(
                    onClick = { /* Buka Settings Modal */ },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickToggleCard(
                    modifier = Modifier.weight(1f),
                    title = "Kamera Depan",
                    subtitle = "Reaksi wajah",
                    icon = Icons.Default.Face,
                    active = facecamEnabled,
                    onToggle = { facecamEnabled = !facecamEnabled }
                )
                QuickToggleCard(
                    modifier = Modifier.weight(1f),
                    title = "Suara Mic",
                    subtitle = "Audio eksternal",
                    icon = Icons.Default.Mic,
                    active = micEnabled,
                    onToggle = { micEnabled = !micEnabled }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Record Panel Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isRecording) {
                    Text(
                        text = formatTime(seconds),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SEDANG MEREKAM", color = Color(0xFFE8894A), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(48.dp))
                }

                RecordButton(
                    isRecording = isRecording,
                    onClick = { 
                        if (!isRecording) seconds = 0
                        isRecording = !isRecording 
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Library Button Bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable { /* Buka Library Bottom Sheet */ },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.List, contentDescription = null, tint = Color(0xFFE8894A))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Perpustakaan Rekaman", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickToggleCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    active: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = if (active) Color(0xFFE8894A).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = if (active) Color(0xFFE8894A) else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp)
                )
                Switch(
                    checked = active,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFE8894A),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f),
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(0.8f) // Bikin switch agak kecil biar rapi
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
    }
}

@Composable
fun RecordButton(isRecording: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(140.dp)
    ) {
        if (isRecording) {
            // Efek detak gelombang di belakang
            Box(
                modifier = Modifier
                    .size((110 * scale).dp)
                    .background(Color(0xFFE53935).copy(alpha = 0.2f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size((90 * scale).dp)
                    .background(Color(0xFFE53935).copy(alpha = 0.4f), CircleShape)
            )
        }
        
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color.Transparent else Color(0xFFE8894A)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (isRecording) {
                // Kotak STOP (Merah)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE53935), RoundedCornerShape(6.dp))
                )
            } else {
                // Ikon bulat (Putih) saat siap merekam
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}
