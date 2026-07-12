package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.audio.SoundPlayer
import com.example.ui.model.VeggieItem
import com.example.ui.model.VeggieType
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.FloatingMatchItem
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val progress by viewModel.progress.collectAsState()
    val veggiePile by viewModel.veggiePile.collectAsState()
    val slots by viewModel.slots.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val maxTimerSeconds by viewModel.maxTimerSeconds.collectAsState()
    val score by viewModel.score.collectAsState()
    val freezeActive by viewModel.powerFreezeActive.collectAsState()
    val freezeTimerSeconds by viewModel.freezeTimerSeconds.collectAsState()

    val floatingMatches by viewModel.floatingMatches.collectAsState()
    val comboCount by viewModel.comboCount.collectAsState()
    val comboMultiplier by viewModel.comboMultiplier.collectAsState()
    val comboRemainingPercent by viewModel.comboRemainingPercent.collectAsState()

    val requiredScore by viewModel.requiredScore.collectAsState()
    val specialVeggieLaserRow by viewModel.specialVeggieLaserRow.collectAsState()
    val specialVeggieLaserCol by viewModel.specialVeggieLaserCol.collectAsState()

    // Aggregate counts of remaining vegetables on the board (veggiePile + slots)
    // To show the goals at the top just like the video!
    val allActiveItems = veggiePile + slots
    val remainingByType = allActiveItems.groupBy { it.type }.mapValues { it.value.size }

    // Display up to 5 vegetable objectives at the top
    val activeTypesInLevel = remainingByType.keys.toList().take(5)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)) // Bright, warm organic light greens
                )
            )
            .padding(top = 40.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. HEADER SECTION (Level, Timer, Pause)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Label
            Column {
                Text(
                    text = "SEVİYE",
                    fontSize = 11.sp,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${progress?.level ?: 1}",
                    fontSize = 24.sp,
                    color = Color(0xFF1B5E20),
                    fontWeight = FontWeight.Black
                )
            }

            // Countdown Timer Capsule (Frozen ice theme if freeze is active!)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (freezeActive) Color(0xFFB2EBF2) else Color.Black.copy(alpha = 0.65f)
                    )
                    .border(
                        width = 2.dp,
                        color = if (freezeActive) Color(0xFF00ACC1) else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (freezeActive) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Frozen",
                        tint = Color(0xFF00838F),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp)
                    )
                }
                val min = timerSeconds / 60
                val sec = timerSeconds % 60
                val timeString = String.format("%02d:%02d", min, sec)

                Text(
                    text = if (freezeActive) "$timeString (❄️ $freezeTimerSeconds)" else timeString,
                    color = if (freezeActive) Color(0xFF006064) else Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Global Mute All Button
                val isMuted = progress?.isMuted ?: false
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) Color(0xFFE53935) else Color(0xFF8D6E63))
                        .clickable {
                            viewModel.toggleMuteAll()
                        }
                        .testTag("mute_all_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute All",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Pause Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8D6E63))
                        .clickable {
                            SoundPlayer.playMenuTap()
                            viewModel.quitGame() // takes user back to dashboard safely
                        }
                        .testTag("pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. LEVEL GOALS / OBJECTIVES STATUS BAR
        // Exactly matches the custom boxes at the top in the video!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            activeTypesInLevel.forEach { type ->
                val remaining = remainingByType[type] ?: 0
                if (remaining > 0) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF8D6E63), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = type.emoji, fontSize = 16.sp)
                            Text(
                                text = "$remaining",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4E342E)
                            )
                        }
                    }
                }
            }
        }

        // SCORE & COMBO STATUS ROW
        Row(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Real-time Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF9C4).copy(alpha = 0.95f))
                    .border(1.5.dp, Color(0xFFFBC02D), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🏆 SKOR: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFF57F17)
                )
                Text(
                    text = "$score / $requiredScore",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF3E2723)
                )
            }

            // Real-time Combo Tracker
            if (comboCount > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFE0B2).copy(alpha = 0.95f))
                        .border(1.5.dp, Color(0xFFE65100), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "KOMBO x${String.format("%.1f", comboMultiplier)} 🔥",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Tiny combo progress bar!
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFCC80))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(comboRemainingPercent)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFFB74D), Color(0xFFD84315))
                                    )
                                )
                        )
                    }
                }
            } else {
                // Hint message to encourage matching rapidly
                Text(
                    text = "Seri eşleştir, çarpan kazan! ⚡",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        // 3. MAIN INTERACTIVE GARDEN FIELD (The pile container)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1.0f) // square game box
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF5D4037)) // Rich fertile soil background color
                .border(
                    width = 6.dp,
                    color = Color(0xFF8D6E63), // Wooden frame borders
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp)
        ) {
            val fieldWidth = maxWidth
            val fieldHeight = maxHeight

            // Render all items in the vegetable pile
            veggiePile.forEach { item ->
                // Animated opacity based on whether it is covered
                val opacity = if (item.isCovered) 0.55f else 1.0f
                val scale = if (item.isCovered) 0.92f else 1.0f

                VeggieCard3D(
                    item = item,
                    isClickedState = false,
                    isMini = false,
                    onClick = {
                        viewModel.selectVeggie(item)
                    },
                    modifier = Modifier
                        .offset(
                            x = fieldWidth * item.xPercent - 28.dp,
                            y = fieldHeight * item.yPercent - 28.dp
                        )
                        .scale(scale)
                        .graphicsLayer {
                            rotationZ = item.rotation
                        }
                        .alpha(opacity)
                )
            }

            // If empty pile, show clear message inside container
            if (veggiePile.isEmpty() && slots.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BAHÇE TEMİZLENDİ! 🎉",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Render Special Veggie row/column laser beam animations!
            specialVeggieLaserRow?.let { laserY ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .offset(y = maxHeight * laserY - 14.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFEA00), // Glowing gold center
                                    Color(0xFFFF3D00), // Glowing fiery orange edge
                                    Color(0xFFFFEA00),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            specialVeggieLaserCol?.let { laserX ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(28.dp)
                        .offset(x = maxWidth * laserX - 14.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFEA00), // Glowing gold center
                                    Color(0xFFFF3D00), // Glowing fiery orange edge
                                    Color(0xFFFFEA00),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        // 4. SLOTS CONTAINER (Capacity of 7 slots at the bottom)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SEPET (En fazla 7 sebze)",
                fontSize = 11.sp,
                color = Color(0xFF388E3C),
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Slots horizontal layout
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF3E2723)) // Dark wooden bar background
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Generate 7 slot boxes
                for (i in 0..6) {
                    val slotItem = slots.getOrNull(i)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.0f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (slotItem != null) {
                            val scaleAnim = remember(slotItem.id) { androidx.compose.animation.core.Animatable(0.4f) }
                            androidx.compose.runtime.LaunchedEffect(slotItem.id) {
                                scaleAnim.animateTo(
                                    targetValue = 1.0f,
                                    animationSpec = tween(
                                        durationMillis = 220,
                                        easing = androidx.compose.animation.core.CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
                                    )
                                )
                            }
                            VeggieCard3D(
                                item = slotItem,
                                isClickedState = true,
                                isMini = true,
                                modifier = Modifier.scale(scaleAnim.value)
                            )
                        } else {
                            // Empty slot placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF5D4037))
                                    .border(
                                        width = 1.5.dp,
                                        color = Color(0xFF4E342E),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        // 5. BOTTOM POWER-UPS ACTION BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo Power Button
            PowerUpButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Geri Al",
                costText = if ((progress?.powerUndoCount ?: 0) > 0) "${progress?.powerUndoCount}" else "10 🪙",
                onClick = { viewModel.useUndo() }
            )

            // Magnet Power Button
            PowerUpButton(
                icon = Icons.Default.FlashOn,
                label = "Mıknatıs",
                costText = if ((progress?.powerMagnetCount ?: 0) > 0) "${progress?.powerMagnetCount}" else "20 🪙",
                onClick = { viewModel.useMagnet() }
            )

            // Freeze Power Button
            PowerUpButton(
                icon = Icons.Default.AcUnit,
                label = "Dondur",
                costText = if ((progress?.powerFreezeCount ?: 0) > 0) "${progress?.powerFreezeCount}" else "15 🪙",
                onClick = { viewModel.useFreeze() }
            )
        }
    }

    // Floating Match Animations Overlay
    floatingMatches.forEach { match ->
        FloatingMatchAnimation(match = match)
    }
}
}

@Composable
fun PowerUpButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    costText: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFB74D), Color(0xFFE65100))
                    )
                )
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF4E342E),
            fontWeight = FontWeight.Black
        )
        // Cost capsule badge
        Box(
            modifier = Modifier
                .offset(y = (-2).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF3E2723))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = costText,
                color = Color(0xFFFFD54F),
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun VeggieCard3D(
    item: com.example.ui.model.VeggieItem,
    isClickedState: Boolean = false,
    isMini: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cardBgColor = item.type.cardBgColor

    // Calculate a darker shade of the background color for 3D extrusion
    val darkerColor = remember(cardBgColor) {
        Color(
            red = (cardBgColor.red * 0.70f).coerceAtLeast(0f),
            green = (cardBgColor.green * 0.70f).coerceAtLeast(0f),
            blue = (cardBgColor.blue * 0.70f).coerceAtLeast(0f),
            alpha = cardBgColor.alpha
        )
    }

    // Determine 3D height depth offset based on size and lock state
    val extrusionDp = when {
        isMini -> 2.dp
        item.isCovered -> 1.5.dp
        else -> 4.5.dp
    }

    val shape = CircleShape

    Box(
        modifier = modifier
            .then(
                if (isMini) Modifier.fillMaxSize() else Modifier.size(56.dp)
            )
            .clickable(enabled = !item.isCovered && !isClickedState) {
                onClick()
            }
    ) {
        // 1. 3D Bottom Depth Shadow Extrusion
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = extrusionDp)
                .background(darkerColor, shape = shape)
        )

        // 2. Main High-Contrast Glossy Beveled Surface Sphere
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = extrusionDp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(cardBgColor, cardBgColor.copy(alpha = 0.85f))
                    ),
                    shape = shape
                )
                .border(
                    width = if (item.isCovered) 1.dp else 1.8.dp,
                    color = if (item.isCovered) Color.Gray.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.65f),
                    shape = shape
                )
        ) {
            // Specular shiny highlight/gloss circle at top-left
            Box(
                modifier = Modifier
                    .padding(start = if (isMini) 6.dp else 9.dp, top = if (isMini) 5.dp else 8.dp)
                    .size(if (isMini) 6.dp else 10.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.75f))
            )

            // Card Content (Centered Emoji and expression directly on the sphere)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = item.type.emoji,
                    fontSize = if (isMini) 22.sp else 28.sp,
                    modifier = Modifier.offset(y = if (isMini) (-1).dp else (-2).dp)
                )

                // Select facial expression based on state
                val faceText = when {
                    item.isCovered -> "ᵕ≀_ᵕ"   // Sleepy / covered face
                    isClickedState -> "≧◡≦"  // Clicked smiling face
                    else -> "•‿•"            // Simple joyful active face
                }

                Text(
                    text = faceText,
                    fontSize = if (isMini) 10.sp else 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (item.isCovered) Color.Black.copy(alpha = 0.45f) else Color(0xFF2D1500),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = if (isMini) 0.dp else 1.dp)
                )
            }

            // Lock overlay shadow
            if (item.isCovered) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f), shape = shape)
                )
            }
        }
    }
}

@Composable
fun FloatingMatchAnimation(match: FloatingMatchItem) {
    val animProgress = remember { Animatable(0f) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 750, easing = LinearOutSlowInEasing)
        )
    }

    val t = animProgress.value // 0f to 1f

    // Calculate animated properties:
    // 1. Vertical motion: starts at the basket slots (y ~ 0.85f of screen height) and floats up to 0.45f
    val startYPercent = 0.85f
    val endYPercent = 0.45f
    val currentYPercent = startYPercent - (startYPercent - endYPercent) * t

    // 2. Horizontal position: starts at the matching slot index center
    val startXPercent = 0.08f + match.initialIndex * 0.125f

    // 3. Scale: quickly pops up to 1.5f, then shrinks/fades
    val scale = if (t < 0.25f) {
        1.0f + (t / 0.25f) * 0.5f // 1.0 to 1.5
    } else {
        1.5f - ((t - 0.25f) / 0.75f) * 1.5f // 1.5 to 0.0
    }

    // 4. Alpha: fades out at the end
    val alpha = if (t > 0.6f) {
        (1.0f - t) / 0.4f
    } else {
        1.0f
    }

    // 5. Rotation: spin
    val rotation = t * 180f * (if (match.initialIndex % 2 == 0) 1 else -1)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Box(
            modifier = Modifier
                .offset(
                    x = screenWidth * startXPercent - 32.dp,
                    y = screenHeight * currentYPercent - 32.dp
                )
                .scale(scale.coerceAtLeast(0f))
                .alpha(alpha.coerceIn(0f, 1f))
                .graphicsLayer {
                    rotationZ = rotation
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Sphere shape representation of the matching veggie!
                val shape = CircleShape
                val cardBgColor = match.type.cardBgColor
                val darkerColor = Color(
                    red = (cardBgColor.red * 0.70f).coerceAtLeast(0f),
                    green = (cardBgColor.green * 0.70f).coerceAtLeast(0f),
                    blue = (cardBgColor.blue * 0.70f).coerceAtLeast(0f),
                    alpha = cardBgColor.alpha
                )

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(darkerColor, shape = shape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 3.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(cardBgColor, cardBgColor.copy(alpha = 0.85f))
                                ),
                                shape = shape
                            )
                            .border(1.8.dp, Color.White.copy(alpha = 0.65f), shape = shape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Specular highlight
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 8.dp, top = 6.dp)
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.75f))
                        )

                        Text(
                            text = match.type.emoji,
                            fontSize = 26.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Floating points badge (+100 or combo points!)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFD54F))
                        .border(1.dp, Color(0xFFF57C00), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${match.scoreGained}",
                        color = Color(0xFF5D4037),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
