package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.Order
import com.agriindia.app.model.User

@Composable
fun ProfileScreen(
    language: AppLanguage,
    user: User?,
    orders: List<Order>,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
            .verticalScroll(rememberScrollState())
    ) {
        // Profile header gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF059669),
                            Color(0xFF047857),
                            Color(0xFF065F46)
                        )
                    )
                )
        ) {
            // Back button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = user?.name?.take(1)?.uppercase() ?: "?",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user?.name ?: (if (isHi) "किसान" else "Farmer"),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = user?.email ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFFD1FAE5)
                )
            }
        }

        // Profile details card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (isHi) "खाता विवरण" else "Account Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoRow(
                    icon = Icons.Outlined.Person,
                    label = if (isHi) "नाम" else "Name",
                    value = user?.name ?: "-"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                ProfileInfoRow(
                    icon = Icons.Outlined.Email,
                    label = if (isHi) "ईमेल" else "Email",
                    value = user?.email ?: "-"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                ProfileInfoRow(
                    icon = Icons.Outlined.Phone,
                    label = if (isHi) "फ़ोन" else "Phone",
                    value = user?.phone?.ifEmpty { if (isHi) "जोड़ा नहीं गया" else "Not added" } ?: "-"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                ProfileInfoRow(
                    icon = Icons.Outlined.LocationOn,
                    label = if (isHi) "राज्य" else "State",
                    value = user?.state?.ifEmpty { if (isHi) "चुना नहीं गया" else "Not selected" } ?: "-"
                )
            }
        }

        // Order History
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "ऑर्डर इतिहास" else "Order History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHi) "अभी तक कोई ऑर्डर नहीं" else "No orders yet",
                                fontSize = 14.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                } else {
                    orders.forEach { order ->
                        OrderHistoryItem(order = order, isHi = isHi)
                        if (order != orders.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFEF2F2),
                contentColor = Color(0xFFDC2626)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHi) "लॉगआउट" else "Logout",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF059669),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun OrderHistoryItem(order: Order, isHi: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.orderId,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "${order.items.size} ${if (isHi) "आइटम" else "items"}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
                    .format(java.util.Date(order.timestamp)),
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "₹${order.totalAmount.toInt()}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF059669)
            )
            Surface(
                color = when (order.paymentStatus) {
                    "Paid" -> Color(0xFFDCFCE7)
                    "Failed" -> Color(0xFFFEF2F2)
                    else -> Color(0xFFFEF3C7)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when {
                        isHi && order.paymentStatus == "Paid" -> "भुगतान हो गया"
                        isHi && order.paymentStatus == "Failed" -> "विफल"
                        isHi -> "बाकी"
                        else -> order.paymentStatus
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (order.paymentStatus) {
                        "Paid" -> Color(0xFF065F46)
                        "Failed" -> Color(0xFFDC2626)
                        else -> Color(0xFFD97706)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
