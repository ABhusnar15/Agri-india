package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriTopAppBar(
    currentLanguage: AppLanguage,
    cartItemCount: Int,
    userName: String?,
    onToggleLanguage: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenKisanMitra: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenFertilizerCalc: () -> Unit = {},
    onOpenCropDoctor: () -> Unit = {},
    onOpenDroneSpray: () -> Unit = {},
    onOpenSoilHealth: () -> Unit = {},
    onOpenPashuChikitsa: () -> Unit = {},
    onOpenSolarPump: () -> Unit = {},
    onOpenKrishiKhata: () -> Unit = {}
) {
    var showToolsMenu by remember { mutableStateOf(false) }
    val isHi = currentLanguage == AppLanguage.HINDI

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF059669),
            titleContentColor = Color.White
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null,
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isHi) "एग्री इंडिया" else "Agri India",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (userName != null) {
                            if (isHi) "नमस्ते, $userName" else "Hello, $userName"
                        } else {
                            if (isHi) "उन्नत भारतीय किसान ऐप" else "Smart Farmers App"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFD1FAE5)
                    )
                }
            }
        },
        actions = {
            // Advanced Smart Krishi Tools Menu
            Box {
                IconButton(
                    onClick = { showToolsMenu = !showToolsMenu },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Smart Tools Hub",
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showToolsMenu,
                    onDismissRequest = { showToolsMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "🚀 ड्रोन स्प्रे बुकिंग" else "🚀 Kisan Drone Spray", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenDroneSpray()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "🧪 मृदा स्वास्थ्य कार्ड (NPK)" else "🧪 Smart Soil Health Card", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenSoilHealth()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "🐄 पशु चिकित्सा व डेयरी" else "🐄 Pashu Chikitsa & Dairy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenPashuChikitsa()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SolarPower, contentDescription = null, tint = Color(0xFFCA8A04), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "☀️ पीएम-कुसुम सोलर पंप" else "☀️ PM-KUSUM Solar Pump", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenSolarPump()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "📒 कृषि बही-खाता" else "📒 Smart Krishi Khata", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenKrishiKhata()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "🩺 एआई फसल डॉक्टर" else "🩺 AI Crop Doctor", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenCropDoctor()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHi) "🌿 उर्वरक व खाद कैलकुलेटर" else "🌿 Fertilizer Dosage Calc", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        onClick = {
                            showToolsMenu = false
                            onOpenFertilizerCalc()
                        }
                    )
                }
            }

            // Kisan Mitra AI Quick Action
            IconButton(
                onClick = onOpenKisanMitra,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Kisan Mitra AI",
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Cart Button
            IconButton(
                onClick = onOpenCart,
                modifier = Modifier.size(36.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = Color(0xFFD97706),
                                contentColor = Color.White
                            ) {
                                Text(cartItemCount.toString(), fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Language Switcher Pill
            Surface(
                onClick = onToggleLanguage,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF047857),
                modifier = Modifier.height(28.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFFFDE047)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "हिं" else "EN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // User Profile Avatar
            IconButton(
                onClick = onOpenProfile,
                modifier = Modifier.size(36.dp)
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    color = Color(0xFF047857)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (userName != null) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}
