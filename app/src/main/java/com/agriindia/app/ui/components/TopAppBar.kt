package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Language
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriTopAppBar(
    currentLanguage: AppLanguage,
    cartItemCount: Int,
    onToggleLanguage: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenKisanMitra: () -> Unit
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
                        text = if (currentLanguage == AppLanguage.HINDI) "भारतीय किसानों का साथी" else "One-Stop Farmers App",
                        fontSize = 11.sp,
                        color = Color(0xFFD1FAE5)
                    )
                }
            }
        },
        actions = {
            // Language switch button
            FilledTonalButton(
                onClick = onToggleLanguage,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF047857),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFDE047)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "हिंदी" else "ENG",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onOpenKisanMitra) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Kisan Mitra AI",
                    tint = Color(0xFFFDE047)
                )
            }

            IconButton(onClick = onOpenCart) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = Color(0xFFD97706),
                                contentColor = Color.White
                            ) {
                                Text(cartItemCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart",
                        tint = Color.White
                    )
                }
            }
        }
    )
}
