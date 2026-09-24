package com.example.calltranslator.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calltranslator.ui.theme.*
import com.example.calltranslator.viewmodel.CallState
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun AnimatedMic(
    isListening: Boolean,
    level: Float,
    onTap: () -> Unit
) {
    val basePulseScale by animateFloatAsState(
        targetValue = 1.0f + (if (isListening) level * 0.5f else 0.0f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "mic_pulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .clickable(onClick = onTap)
    ) {
        // Glowing Outer layer 1
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(
                    color = (if (isListening) PrimaryNeon else SecondaryNeon).copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )
        // Glowing Layer 2
        Box(
            modifier = Modifier
                .size(105.dp * basePulseScale)
                .background(
                    color = (if (isListening) SuccessGreen else PrimaryNeon).copy(alpha = 0.15f),
                    shape = CircleShape
                )
        )
        // Central Core Circle Orb Action Controller
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .background(
                    brush = if (isListening) AppGradients.LiveStreamGradient else AppGradients.PremiumGradient,
                    shape = CircleShape
                )
        ) {
            Crossfade(targetState = isListening, label = "mic_icon") { listening ->
                if (listening) {
                    Icon(Icons.Rounded.BlurOn, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(36.dp))
                } else {
                    Icon(Icons.Rounded.Mic, contentDescription = "Idle", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun AnimatedSoundWave(
    isActive: Boolean,
    color: Color = PrimaryNeon,
    modifier: Modifier = Modifier.fillMaxWidth().height(60.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val midY = size.height / 2f
        val width = size.width
        val path = Path()
        
        path.moveTo(0f, midY)
        
        val step = 4f
        for (x in 0..width.toInt() step step.toInt()) {
            val cycle = (x.toFloat() / width) * 4f * PI.toFloat()
            val amplitude = if (isActive) {
                (size.height / 3f) * sin(cycle - phaseShift) * cos(cycle * 0.5f + phaseShift)
            } else {
                2f * sin(cycle)
            }
            val edgeFactor = sin((x.toFloat() / width) * PI.toFloat())
            path.lineTo(x.toFloat(), midY + amplitude * edgeFactor)
        }

        drawPath(
            path = path,
            color = color.copy(alpha = if (isActive) 0.8f else 0.2f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Overlay lighter subwave
        val pathSec = Path()
        pathSec.moveTo(0f, midY)
        for (x in 0..width.toInt() step step.toInt()) {
            val cycle = (x.toFloat() / width) * 5f * PI.toFloat()
            val amplitude = if (isActive) {
                (size.height / 4f) * cos(cycle + phaseShift) * sin(cycle * 0.3f - phaseShift)
            } else {
                1f * cos(cycle)
            }
            val edgeFactor = sin((x.toFloat() / width) * PI.toFloat())
            pathSec.lineTo(x.toFloat(), midY + amplitude * edgeFactor)
        }

        drawPath(
            path = pathSec,
            color = SecondaryNeon.copy(alpha = if (isActive) 0.4f else 0.1f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ConnectionIndicator(state: CallState) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_node")
    val pulseFactor by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val statusColor = when (state) {
        CallState.CONNECTED -> SuccessGreen
        CallState.CONNECTING, CallState.RECONNECTING -> PrimaryNeon
        CallState.WEAK_CONNECTION -> AccentPink
        else -> Color.Gray
    }

    val statusText = when (state) {
        CallState.CONNECTED -> "CONNECTED"
        CallState.CONNECTING -> "CONNECTING..."
        CallState.RECONNECTING -> "RECONNECTING..."
        CallState.WEAK_CONNECTION -> "WEAK CONNECTION"
        else -> "DISCONNECTED"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(220.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            val midY = size.height / 2f
            val width = size.width

            // Draw linking channel pipe path line
            if (state == CallState.CONNECTING || state == CallState.RECONNECTING) {
                val dashWidth = 8.dp.toPx()
                val dashSpace = 6.dp.toPx()
                var currentX = 20.dp.toPx()
                while (currentX < width - 20.dp.toPx()) {
                    drawLine(
                        color = statusColor.copy(alpha = 0.4f),
                        start = Offset(currentX, midY),
                        end = androidx.compose.ui.geometry.Offset(currentX + dashWidth, midY),
                        strokeWidth = 2.dp.toPx()
                    )
                    currentX += dashWidth + dashSpace
                }
            } else {
                drawLine(
                    color = statusColor.copy(alpha = 0.3f),
                    start = androidx.compose.ui.geometry.Offset(20.dp.toPx(), midY),
                    end = androidx.compose.ui.geometry.Offset(width - 20.dp.toPx(), midY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Left Node Link Me
            drawCircle(color = PrimaryNeon, radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(20.dp.toPx(), midY))
            drawCircle(color = PrimaryNeon.copy(alpha = 0.2f), radius = 6.dp.toPx() + pulseFactor, center = androidx.compose.ui.geometry.Offset(20.dp.toPx(), midY))

            // Center routing hub or link bullet
            if (state != CallState.IDLE) {
                drawCircle(color = statusColor, radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(width / 2f, midY))
            }

            // Right Node Link Remote
            drawCircle(color = SecondaryNeon, radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(width - 20.dp.toPx(), midY))
            drawCircle(color = SecondaryNeon.copy(alpha = 0.2f), radius = 6.dp.toPx() + (8f - pulseFactor), center = androidx.compose.ui.geometry.Offset(width - 20.dp.toPx(), midY))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Capsule chip status badge
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.12f))
                .border(1.dp, statusColor.copy(alpha = 0.3f), CircleShape)
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
