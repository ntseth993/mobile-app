package com.example.calltranslator.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Glassmorphism Card - Modern frosted glass effect
 */
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.15f),
    borderColor: Color = Color.White.copy(alpha = 0.25f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.2f),
                        backgroundColor.copy(alpha = 0.1f)
                    )
                )
            )
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .blur(0.5.dp)
    ) {
        content()
    }
}

/**
 * Neumorphic Button - Soft UI style
 */
@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    icon: androidx.compose.material.icons.Icons? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
            },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF0F0F0),
            contentColor = Color(0xFF333333)
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Text(label, fontSize = 14.sp)
        }
    }
}

/**
 * Modern Gradient Card with stats
 */
@Composable
fun GradientStatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    gradientColors: List<Color> = listOf(Color(0xFF00D4FF), Color(0xFF0099CC)),
    icon: androidx.compose.material.icons.Icons? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = value,
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Animated Pulse Button
 */
@Composable
fun PulseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    isPulsing: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isPulsing) 1.1f else 1f,
        animationSpec = repeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00D4FF)
        )
    ) {
        Text(label, color = Color.White)
    }
}

/**
 * Animated Toggle Switch
 */
@Composable
fun AnimatedToggleSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    activeColor: Color = Color(0xFF00D4FF)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Text(label, fontSize = 16.sp)
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp)),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = activeColor,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

/**
 * Floating Action Menu
 */
@Composable
fun FloatingActionMenu(
    modifier: Modifier = Modifier,
    mainIcon: androidx.compose.material.icons.Icons = Icons.Rounded.Add,
    items: List<Pair<String, () -> Unit>> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.size(56.dp)) {
        // Menu items
        if (isExpanded) {
            items.forEachIndexed { index, (label, onClick) ->
                val angle = (360f / items.size) * index
                val x = 100f * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val y = 100f * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()

                FloatingActionButton(
                    onClick = {
                        onClick()
                        isExpanded = false
                    },
                    modifier = Modifier
                        .offset(x.dp, y.dp)
                        .size(40.dp),
                    containerColor = Color(0xFF00D4FF),
                    contentColor = Color.White
                ) {
                    Text(label.first().toString())
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.align(Alignment.Center),
            containerColor = Color(0xFF00D4FF),
            contentColor = Color.White
        ) {
            Icon(mainIcon, contentDescription = "Menu", modifier = Modifier.size(28.dp))
        }
    }
}

/**
 * Shimmer Loading effect
 */
@Composable
fun ShimmerLoadingCard(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition()
    val position by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = androidx.compose.ui.geometry.Offset(position, 0f),
                    end = androidx.compose.ui.geometry.Offset(position + 200f, 0f)
                )
            )
    )
}

/**
 * Circular Progress Indicator with text
 */
@Composable
fun CircularProgressWithText(
    progress: Float,
    modifier: Modifier = Modifier,
    text: String = "${(progress * 100).toInt()}%",
    size: androidx.compose.ui.unit.Dp = 120.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 6.dp,
    color: Color = Color(0xFF00D4FF)
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = strokeWidth,
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text, fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}

/**
 * Smooth Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    if (isVisible) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFFF5F5F5),
            scrimColor = Color.Black.copy(alpha = 0.32f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                content()
            }
        }
    }
}
