package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.MandiPrice

@Composable
fun MandiTrendDialog(
    language: AppLanguage,
    item: MandiPrice,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    // Simulated 30-Day price trends
    val priceHistory = listOf(
        item.modalPrice - 180,
        item.modalPrice - 140,
        item.modalPrice - 90,
        item.modalPrice - 60,
        item.modalPrice - 30,
        item.modalPrice + 10,
        item.modalPrice
    )
    val highestPrice = priceHistory.maxOrNull() ?: item.modalPrice
    val lowestPrice = priceHistory.minOrNull() ?: item.modalPrice
    val isBullish = item.priceChange >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isMr || isHi) "${item.commodityHi} - बाजार भाव विश्लेषण" else "${item.commodity} - Mandi Trend Analysis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = if (isMr) "${item.mandiName}, ${item.state} (३० दिवसांचा ट्रेंड)" else if (isHi) "${item.mandiName}, ${item.state} (30-दिवसीय रुझान)" else "${item.mandiName}, ${item.state} (30-Day Trend)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Current Modal Price Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isHi) "वर्तमान मॉडल भाव" else "Current Modal Rate",
                                fontSize = 11.sp,
                                color = Color(0xFFD1FAE5)
                            )
                            Text(
                                text = "₹${item.modalPrice} / ${item.unit}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = if (isBullish) Color(0xFF166534) else Color(0xFF991B1B),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isBullish) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "₹${Math.abs(item.priceChange)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Market Trend Analysis Card
                Text(
                    text = if (isHi) "30-दिन बाजार रुझान विश्लेषण:" else "30-Day Market Trend Analysis:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(if (isHi) "30-दिन उच्चतम" else "30-Day High", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("₹$highestPrice", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(if (isHi) "30-दिन न्यूनतम" else "30-Day Low", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("₹$lowestPrice", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Bar Chart Visual
                Text(
                    text = if (isHi) "साप्ताहिक मूल्य गति (Weekly Price Bar)" else "Weekly Price Trend History:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    priceHistory.forEachIndexed { idx, price ->
                        val ratio = ((price - lowestPrice + 50).toFloat() / (highestPrice - lowestPrice + 100).toFloat()).coerceIn(0.2f, 1.0f)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .fillMaxHeight(ratio)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (idx == priceHistory.size - 1) Color(0xFF059669) else Color(0xFFCBD5E1))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "W${idx + 1}", fontSize = 9.sp, color = Color(0xFF64748B))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Krishi Expert Selling Advice
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBullish)
                                if (isHi) "बाजार में तेजी का रुख है। अगले 10 दिनों में फसल बेचने से बेहतर लाभ संभव है।" else "Market sentiment is Bullish. Holding produce for 7-10 days may yield better returns."
                            else
                                if (isHi) "आवक बढ़ने से दाम घटे हैं। नजदीकी मंडी में तुरंत बिक्री की सलाह दी जाती है।" else "Increased mandi arrivals causing rate drop. Selling nearby is recommended.",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isHi) "समझ गए" else "Close Trend")
            }
        }
    )
}
