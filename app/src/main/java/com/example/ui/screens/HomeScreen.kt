package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.audio.SoundPlayer
import com.example.ui.viewmodel.GameViewModel

@Composable
fun HomeScreen(viewModel: GameViewModel) {
    val progress by viewModel.progress.collectAsState()
    val energyTimerSeconds by viewModel.energyTimerSeconds.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background image
        Image(
            painter = painterResource(id = R.drawable.img_farm_background_1783875165057),
            contentDescription = "Farm background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay with a transparent green gradient to blend with the organic playful vibe
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f)
                        )
                    )
                )
        )

        // Main Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP STATUS BAR (Coins, Energy, and Profile)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar capsule
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable {
                            SoundPlayer.playMenuTap()
                            viewModel.showProfile(true)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB74D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = progress?.name ?: "Çiftçi",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Seviye ${progress?.level ?: 1}",
                            color = Color(0xFFFFCC80),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Currency / Energy row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Energy capsule
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val energyText = if (progress != null && progress!!.energy < progress!!.maxEnergy && energyTimerSeconds > 0) {
                            val m = energyTimerSeconds / 60
                            val s = energyTimerSeconds % 60
                            val timeStr = String.format("%02d:%02d", m, s)
                            "${progress?.energy ?: 0}/${progress?.maxEnergy ?: 5} ($timeStr)"
                        } else {
                            "${progress?.energy ?: 0}/${progress?.maxEnergy ?: 5}"
                        }
                        
                        Text(
                            text = energyText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                                .clickable {
                                    SoundPlayer.playMenuTap()
                                    viewModel.showShop(true)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add energy",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Coins capsule
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🪙",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${progress?.coins ?: 0}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB300))
                                .clickable {
                                    SoundPlayer.playMenuTap()
                                    viewModel.showShop(true)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add coins",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // MIDDLE LOGO AND FARM BOARD
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Playful App Title Card
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .offset(y = (-10).dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF8D6E63) // Rich wooden brown
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFA1887F), Color(0xFF6D4C41))
                                )
                            )
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VEGGIE MATCH",
                            fontSize = 28.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Sebze Eşleştirme Hikayesi",
                            fontSize = 13.sp,
                            color = Color(0xFFFFE082),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Small Farm Banner Decor
                Image(
                    painter = painterResource(id = R.drawable.img_veggie_app_icon_1783875153473),
                    contentDescription = "Cute vegetable group icon",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Daily Harvest Button
                    val isHarvestAvailable by viewModel.isDailyHarvestAvailable.collectAsState()
                    val harvestScale = if (isHarvestAvailable) pulseScale else 1.0f
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .scale(harvestScale)
                            .clickable {
                                SoundPlayer.playMenuTap()
                                viewModel.showDailyHarvest(true)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHarvestAvailable) Color(0xFFFFB300) else Color(0xFFFFF9C4)
                        ),
                        elevation = CardDefaults.cardElevation(if (isHarvestAvailable) 6.dp else 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎁", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Günlük Hasat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF5D4037)
                                )
                                Text(
                                    text = if (isHarvestAvailable) "ÖDÜL HAZIR!" else "Yarın Bekleriz",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHarvestAvailable) Color(0xFFD84315) else Color.Gray
                                )
                            }
                        }
                    }

                    // Daily Quests Button
                    val hasUnclaimedQuests = if (progress != null) {
                        val prog = progress!!
                        (prog.quest1Completed && !prog.quest1Claimed) ||
                        (prog.quest2Completed && !prog.quest2Claimed) ||
                        (prog.quest3Completed && !prog.quest3Claimed) ||
                        (prog.quest4Completed && !prog.quest4Claimed)
                    } else false

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                SoundPlayer.playMenuTap()
                                viewModel.showDailyQuests(true)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (hasUnclaimedQuests) Color(0xFF81C784) else Color(0xFFE8F5E9)
                        ),
                        elevation = CardDefaults.cardElevation(if (hasUnclaimedQuests) 6.dp else 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box {
                                Text(text = "📋", fontSize = 16.sp)
                                if (hasUnclaimedQuests) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Günlük Görevler",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = if (hasUnclaimedQuests) "ÖDÜLÜ AL! 🎉" else "Görevleri Yap",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasUnclaimedQuests) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // LEVEL INDICATOR & PLAY BUTTON
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Play Button Card (Big Orange circle with level)
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(160.dp)
                        .clickable {
                            SoundPlayer.playMenuTap()
                            val currentLevel = progress?.level ?: 1
                            viewModel.startLevel(currentLevel)
                        }
                        .testTag("play_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Shadow Outer Circle
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE65100).copy(alpha = 0.3f))
                    )
                    // Mid Accent Circle
                    Box(
                        modifier = Modifier
                            .size(144.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFB74D), Color(0xFFF57C00))
                                )
                            )
                    )
                    // Inner Circle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .size(126.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF9800))
                    ) {
                        Text(
                            text = "${progress?.level ?: 1}",
                            fontSize = 32.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "SEVİYE",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Oyna",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // BOTTOM NAVIGATION BAR (Playful Wood Panel style)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shop Tab
                BottomTabItem(
                    icon = Icons.Default.ShoppingCart,
                    label = "Market",
                    onClick = {
                        SoundPlayer.playMenuTap()
                        viewModel.showShop(true)
                    }
                )

                // Home Tab (Active)
                BottomTabItem(
                    icon = Icons.Default.PlayArrow,
                    label = "Ana Sayfa",
                    activeColor = Color(0xFF4CAF50),
                    onClick = { }
                )

                // Settings Tab
                BottomTabItem(
                    icon = Icons.Default.Settings,
                    label = "Ayarlar",
                    onClick = {
                        SoundPlayer.playMenuTap()
                        viewModel.showSettings(true)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomTabItem(
    icon: ImageVector,
    label: String,
    activeColor: Color? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = activeColor ?: Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = activeColor ?: Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold
        )
    }
}
