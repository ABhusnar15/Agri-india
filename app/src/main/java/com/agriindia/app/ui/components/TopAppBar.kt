package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage

import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science

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
    onOpenFertilizerCalc: () -> Unit = {}
) {
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
                        text = if (currentLanguage == AppLanguage.HINDI) "एग्री इंडिया" else "Agri India",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (userName != null) {
                            if (currentLanguage == AppLanguage.HINDI) "नमस्ते, $userName" else "Hello, $userName"
                        } else {
                            if (currentLanguage == AppLanguage.HINDI) "भारतीय किसानों का साथी" else "One-Stop Farmers App"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFD1FAE5)
                    )
                }
            }
        },
        actions = {
            // Fertilizer Calc Quick Action
            IconButton(
                onClick = onOpenFertilizerCalc,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Fertilizer Calculator",
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(20.dp)
                )
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
