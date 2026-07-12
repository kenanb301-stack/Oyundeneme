package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.audio.SoundPlayer
import com.example.ui.viewmodel.GameViewModel

@Composable
fun GameDialogs(viewModel: GameViewModel) {
    val progress by viewModel.progress.collectAsState()
    val levelCleared by viewModel.levelCleared.collectAsState()
    val levelFailed by viewModel.levelFailed.collectAsState()
    val levelFailedReason by viewModel.levelFailedReason.collectAsState()
    val isShopShowing by viewModel.isShopShowing.collectAsState()
    val isProfileShowing by viewModel.isProfileShowing.collectAsState()
    val isSettingsShowing by viewModel.isSettingsShowing.collectAsState()
    val isDailyHarvestShowing by viewModel.isDailyHarvestShowing.collectAsState()
    val isDailyQuestsShowing by viewModel.isDailyQuestsShowing.collectAsState()
    val score by viewModel.score.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. LEVEL CLEARED OVERLAY
        AnimatedVisibility(
            visible = levelCleared,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFF1B5E20), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TEBRİKLER! 🎉",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1B5E20),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Bahçe Tamamen Temizlendi!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF388E3C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Stars Display
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star 1",
                                tint = Color(0xFFFBC02D),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star 2",
                                tint = Color(0xFFFBC02D),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Star 3",
                                tint = Color(0xFFFBC02D),
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Card
                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Kazanılan Skor",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "$score Puan",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎁 Bölüm Ödülü: ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "50 Altın 🪙", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFB300))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Next Level Button
                        Button(
                            onClick = {
                                SoundPlayer.playMenuTap()
                                viewModel.quitGame() // exit back to home
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(52.dp)
                                .border(3.dp, Color(0xFF1B5E20), RoundedCornerShape(16.dp))
                                .testTag("level_clear_next_button")
                        ) {
                            Text(
                                text = "DEVAM ET",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 2. LEVEL FAILED OVERLAY
        AnimatedVisibility(
            visible = levelFailed,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFFC62828), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFF5F5), Color(0xFFFFCDD2))
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val (titleText, subText, tipText) = when (levelFailedReason) {
                            "basket_full" -> Triple(
                                "SEPET DOLDU! 🧺",
                                "Sepetiniz tamamen doldu ve yapacak hamleniz kalmadı.",
                                "Gereksiz sebze biriktirmek sepeti tıkar! Sadece eşleştirebileceğin sebzeleri seçmeye özen göster. Sıkışırsan Geri Al (Undo) veya Mıknatıs (Magnet) kullanarak sepeti rahatlatabilirsin!"
                            )
                            "time_up" -> Triple(
                                "SÜRE BİTTİ! ⏱️",
                                "Bölüm süresi sona erdi, bahçe zamanında temizlenemedi.",
                                "Zamana karşı yarışıyorsun, biraz daha pratik yapmalısın! Sıkışırsan Dondurma (Freeze) gücünü kullanarak süreyi 15 saniyeliğine durdurabilirsin!"
                            )
                            else -> Triple(
                                "BÖLÜM GEÇİLEMEDİ! 😢",
                                "Sepetiniz doldu veya süreniz bitti. Pes etmek yok!",
                                "Bir dahaki sefere Geri Al, Mıknatıs veya Dondurma güçlerini kullanmayı unutma!"
                            )
                        }

                        Text(
                            text = titleText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC62828),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = subText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "🍎 Bahçıvan Tavsiyesi:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4E342E)
                        )
                        Text(
                            text = tipText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5D4037),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Home Button
                            Button(
                                onClick = {
                                    SoundPlayer.playMenuTap()
                                    viewModel.quitGame()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp)
                                    .border(2.dp, Color(0xFF4E342E), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = "ANA SAYFA",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Retry Button
                            Button(
                                onClick = {
                                    SoundPlayer.playMenuTap()
                                    val currentLevel = progress?.level ?: 1
                                    viewModel.startLevel(currentLevel)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                                    .border(2.dp, Color(0xFFB71C1C), RoundedCornerShape(12.dp))
                                    .testTag("retry_level_button")
                            ) {
                                Text(
                                    text = "TEKRAR DENE",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. SHOP OVERLAY
        AnimatedVisibility(
            visible = isShopShowing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.85f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFF5D4037), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFE082))
                                )
                            )
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Title Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MANAV MARKETİ 🛒",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF5D4037)
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD84315))
                                    .border(2.dp, Color(0xFF5D4037), CircleShape)
                                    .clickable {
                                        SoundPlayer.playMenuTap()
                                        viewModel.showShop(false)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Current Coins Badge
                        Row(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF57C00))
                                .border(2.dp, Color(0xFFE65100), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Bakiye: ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${progress?.coins ?: 0} 🪙", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Shop Items List
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ShopItemRow(
                                title = "Geri Al (Undo)",
                                description = "Tuttuğun son sebzeyi bahçeye geri koyar.",
                                iconEmoji = "↩️",
                                cost = 30,
                                ownedCount = progress?.powerUndoCount ?: 0,
                                onBuy = { viewModel.buyPowerUp("undo") }
                            )

                            ShopItemRow(
                                title = "Mıknatıs (Magnet)",
                                description = "Eşleşecek sebzeleri sepete çeker.",
                                iconEmoji = "🧲",
                                cost = 50,
                                ownedCount = progress?.powerMagnetCount ?: 0,
                                onBuy = { viewModel.buyPowerUp("magnet") }
                            )

                            ShopItemRow(
                                title = "Dondurma (Freeze)",
                                description = "Süreyi 15 saniye dondurur.",
                                iconEmoji = "❄️",
                                cost = 40,
                                ownedCount = progress?.powerFreezeCount ?: 0,
                                onBuy = { viewModel.buyPowerUp("freeze") }
                            )

                            ShopItemRow(
                                title = "Enerji Deposu (Refill)",
                                description = "Bahçe enerjisini fulle.",
                                iconEmoji = "⚡",
                                cost = 100,
                                ownedCount = progress?.energy ?: 0,
                                maxLimit = progress?.maxEnergy ?: 5,
                                onBuy = { viewModel.buyPowerUp("energy") }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Real Money Coins section
                        Text(
                            text = "ALTIN PAKETLERİ (GERÇEK PARA) 🪙",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF5D4037),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            CoinPackRow(
                                title = "Acemi Çiftçi Paketi",
                                description = "Çiftlik yolculuğuna hızlı bir başlangıç yap.",
                                coinEmoji = "💰",
                                coinsGained = 250,
                                priceStr = "19.99 ₺",
                                onPurchase = { viewModel.buyCoinsWithRealMoney("starter") }
                            )

                            CoinPackRow(
                                title = "Bahçıvanın Gözdesi",
                                description = "Güçlerinle bahçeyi dilediğince yönet.",
                                coinEmoji = "🍯",
                                coinsGained = 600,
                                priceStr = "39.99 ₺",
                                onPurchase = { viewModel.buyCoinsWithRealMoney("gardener") }
                            )

                            CoinPackRow(
                                title = "Çiftlik Ağası Paketi",
                                description = "Sınırsız eğlence ve tam yetki!",
                                coinEmoji = "👑",
                                coinsGained = 1500,
                                priceStr = "79.99 ₺",
                                onPurchase = { viewModel.buyCoinsWithRealMoney("master") }
                            )
                        }
                    }
                }
            }
        }

        // 4. PROFILE OVERLAY
        AnimatedVisibility(
            visible = isProfileShowing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFF5D4037), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    var nameInput by remember { mutableStateOf(progress?.name ?: "Çiftçi") }

                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80))
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ÇİFTÇİ PROFİLİ 🧑‍🌾",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF5D4037)
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD84315))
                                    .border(2.dp, Color(0xFF5D4037), CircleShape)
                                    .clickable {
                                        SoundPlayer.playMenuTap()
                                        viewModel.showProfile(false)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Avatar Placeholer
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF81C784))
                                .border(4.dp, Color.White, CircleShape)
                                .border(6.dp, Color(0xFF4CAF50), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧑‍🌾", fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Edit Name Field
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Çiftçi İsmi", color = Color(0xFF5D4037)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE65100),
                                unfocusedBorderColor = Color(0xFF8D6E63),
                                focusedLabelColor = Color(0xFFE65100),
                                unfocusedLabelColor = Color(0xFF5D4037),
                                focusedTextColor = Color(0xFF4E342E),
                                unfocusedTextColor = Color(0xFF4E342E)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Statistics Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF8D6E63).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📊 Çiftlik İstatistikleri",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF4E342E)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Mevcut Seviye:", fontSize = 12.sp, color = Color.Gray)
                                    Text(text = "${progress?.level ?: 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Altın Bakiye:", fontSize = 12.sp, color = Color.Gray)
                                    Text(text = "${progress?.coins ?: 0} 🪙", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Save Button
                        Button(
                            onClick = {
                                SoundPlayer.playMenuTap()
                                viewModel.changePlayerName(nameInput)
                                viewModel.showProfile(false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(2.dp, Color(0xFFE65100), RoundedCornerShape(12.dp))
                        ) {
                            Text(text = "KAYDET", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // 5. SETTINGS OVERLAY
        AnimatedVisibility(
            visible = isSettingsShowing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFF1B5E20), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFF1F8E9), Color(0xFFC8E6C9))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AYARLAR ⚙️",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD84315))
                                    .border(2.dp, Color(0xFF1B5E20), CircleShape)
                                    .clickable {
                                        SoundPlayer.playMenuTap()
                                        viewModel.showSettings(false)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sound Effects Slider
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Ses Efektleri 🔊",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "Eşleşme ve tıklama sesleri.",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "${((progress?.soundVolume ?: 0.8f) * 100).toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            Slider(
                                value = progress?.soundVolume ?: 0.8f,
                                onValueChange = { viewModel.setSoundVolume(it) },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF4CAF50),
                                    activeTrackColor = Color(0xFF4CAF50),
                                    inactiveTrackColor = Color(0xFFE8F5E9)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Background Music Slider
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Arka Plan Müziği 🎵",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "Dinlendirici çiftlik melodisi.",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "${((progress?.musicVolume ?: 0.8f) * 100).toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            Slider(
                                value = progress?.musicVolume ?: 0.8f,
                                onValueChange = { viewModel.setMusicVolume(it) },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF4CAF50),
                                    activeTrackColor = Color(0xFF4CAF50),
                                    inactiveTrackColor = Color(0xFFE8F5E9)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Footer Credits Info
                        Text(
                            text = "Veggie Match v1.0.0\nOyuncular için sevgiyle geliştirildi ❤️",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 6. DAILY HARVEST OVERLAY
        AnimatedVisibility(
            visible = isDailyHarvestShowing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFFE65100), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFD54F))
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GÜNLÜK HASAT! 🌾",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE65100),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bugünün taze ödülleri hazır! Topla ve çiftliğini büyütmeye başla.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        // Rewards Cards Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Gold reward card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color(0xFFE65100), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "🪙", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "200 Altın", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE65100))
                                }
                            }

                            // Energy reward card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "⚡", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "5 Enerji", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4CAF50))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Claim Button
                        Button(
                            onClick = {
                                SoundPlayer.playMenuTap()
                                viewModel.claimDailyHarvest()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .border(3.dp, Color(0xFF2E7D32), RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = "ÖDÜLÜ AL 🎁",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 7. DAILY QUESTS OVERLAY
        AnimatedVisibility(
            visible = isDailyQuestsShowing,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.85f)
                        .padding(12.dp)
                        .border(4.dp, Color(0xFF1B5E20), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF7)),
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFF1F8E9), Color(0xFFC8E6C9))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GÜNLÜK GÖREVLER 📋",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD84315))
                                    .border(2.dp, Color(0xFF1B5E20), CircleShape)
                                    .clickable {
                                        SoundPlayer.playMenuTap()
                                        viewModel.showDailyQuests(false)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scrollable Quests list
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (progress != null) {
                                val prog = progress!!

                                // Quest 1: Match 15 Veggies
                                QuestRow(
                                    title = "Sebze Avcısı 🥕",
                                    description = "Bölümlerde toplam 15 sebze eşleştir.",
                                    progress = prog.quest1Progress,
                                    target = 15,
                                    rewardText = "50 Altın 🪙",
                                    isCompleted = prog.quest1Completed,
                                    isClaimed = prog.quest1Claimed,
                                    onClaim = { viewModel.claimQuestReward(1) }
                                )

                                // Quest 2: Reach 3x Combo
                                QuestRow(
                                    title = "Kombo Ustası 🔥",
                                    description = "Bölümde en az 3x kombo sayısına ulaş.",
                                    progress = if (prog.quest2Completed) 1 else 0,
                                    target = 1,
                                    rewardText = "+1 Mıknatıs 🧲",
                                    isCompleted = prog.quest2Completed,
                                    isClaimed = prog.quest2Claimed,
                                    onClaim = { viewModel.claimQuestReward(2) }
                                )

                                // Quest 3: Play 2 levels
                                QuestRow(
                                    title = "Bahçıvanın Gücü ⚡",
                                    description = "Herhangi 2 bölüm oyna (kazan veya kaybet).",
                                    progress = prog.quest3Progress,
                                    target = 2,
                                    rewardText = "+1 Dondurma ❄️",
                                    isCompleted = prog.quest3Completed,
                                    isClaimed = prog.quest3Claimed,
                                    onClaim = { viewModel.claimQuestReward(3) }
                                )

                                // Quest 4: Spend 50 coins
                                QuestRow(
                                    title = "Zengin Çiftçi 🪙",
                                    description = "Markette 50 altın harca.",
                                    progress = prog.quest4Progress,
                                    target = 50,
                                    rewardText = "+5 Enerji ⚡",
                                    isCompleted = prog.quest4Completed,
                                    isClaimed = prog.quest4Claimed,
                                    onClaim = { viewModel.claimQuestReward(4) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemRow(
    title: String,
    description: String,
    iconEmoji: String,
    cost: Int,
    ownedCount: Int,
    maxLimit: Int = -1,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFF5D4037), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF9C4))
                    .border(2.dp, Color(0xFF5D4037), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
                // Stock counts
                Text(
                    text = if (maxLimit > 0) "Mevcut: $ownedCount / $maxLimit" else "Sahip olunan: $ownedCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Buy Button
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .width(82.dp)
                    .border(2.dp, Color(0xFFE65100), RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "$cost 🪙",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4E342E),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CoinPackRow(
    title: String,
    description: String,
    coinEmoji: String,
    coinsGained: Int,
    priceStr: String,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFFF57C00), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD54F))
                    .border(2.dp, Color(0xFFF57C00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = coinEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
                Text(
                    text = "+$coinsGained Altın Kazandırır",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF57C00),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Purchase Button
            Button(
                onClick = onPurchase,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .width(84.dp)
                    .border(2.dp, Color(0xFF2E7D32), RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = priceStr,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun QuestRow(
    title: String,
    description: String,
    progress: Int,
    target: Int,
    rewardText: String,
    isCompleted: Boolean,
    isClaimed: Boolean,
    onClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFF1B5E20), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reward Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rewardText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar and Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Progress Bar or text
                Column(modifier = Modifier.weight(1f)) {
                    val displayProgress = progress.coerceAtMost(target)
                    Text(
                        text = "İlerleme: $displayProgress / $target",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Simple Box-based progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE))
                            .border(1.dp, Color(0xFFBDBDBD), CircleShape)
                    ) {
                        val fraction = if (target > 0) displayProgress.toFloat() / target else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) Color(0xFF4CAF50) else Color(0xFF2196F3))
                        )
                    }
                }

                // Claim / Status Button
                val buttonBorderColor = when {
                    isClaimed -> Color.Gray
                    isCompleted -> Color(0xFF2E7D32)
                    else -> Color(0xFFFFB300).copy(alpha = 0.5f)
                }
                Button(
                    onClick = onClaim,
                    enabled = isCompleted && !isClaimed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClaimed) Color(0xFFE0E0E0) else Color(0xFF4CAF50),
                        disabledContainerColor = if (isClaimed) Color(0xFFE0E0E0) else Color(0xFFFFB300).copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(96.dp)
                        .border(2.dp, buttonBorderColor, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = when {
                            isClaimed -> "Alındı"
                            isCompleted -> "Ödülü Al"
                            else -> "Yapılıyor"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isClaimed) Color.Gray else if (isCompleted) Color.White else Color(0xFF5D4037),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

