package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    language: AppLanguage,
    cartItems: List<CartItem>,
    appliedCoupon: String?,
    onUpdateQuantity: (String, Int) -> Unit,
    onApplyCoupon: (String) -> Boolean,
    onProceedPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    var couponCode by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf(false) }
    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryPincode by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val discount = if (appliedCoupon != null) subtotal * 0.1 else 0.0
    val deliveryCharge = if (subtotal > 500) 0.0 else 49.0
    val total = subtotal - discount + deliveryCharge

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = if (isHi) "चेकआउट" else "Checkout",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF059669),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Cart Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "आपका ऑर्डर (${cartItems.size} आइटम)" else "Your Order (${cartItems.size} items)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHi) item.product.nameHi.take(35) else item.product.name.take(35),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A),
                                    maxLines = 2
                                )
                                Text(
                                    text = "₹${item.product.price.toInt()} ${if (isHi) "प्रति इकाई" else "each"}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Quantity controls
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0FDF4)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onUpdateQuantity(item.product.id, -1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = "Decrease",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF059669)
                                        )
                                    }
                                    Text(
                                        text = "${item.quantity}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { onUpdateQuantity(item.product.id, 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Increase",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF059669)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "₹${(item.product.price * item.quantity).toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }

                        if (item != cartItems.last()) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "डिलीवरी पता" else "Delivery Address",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text(if (isHi) "पूरा पता" else "Full Address") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Home, contentDescription = null, tint = Color(0xFF059669))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deliveryPincode,
                        onValueChange = { if (it.length <= 6) deliveryPincode = it },
                        label = { Text(if (isHi) "पिनकोड" else "Pincode") },
                        leadingIcon = {
                            Icon(Icons.Outlined.PinDrop, contentDescription = null, tint = Color(0xFF059669))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Coupon code
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "कूपन कोड" else "Coupon Code",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (appliedCoupon != null) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHi) "कूपन \"$appliedCoupon\" लागू! 10% छूट" else "Coupon \"$appliedCoupon\" applied! 10% off",
                                    fontSize = 13.sp,
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponCode,
                                onValueChange = {
                                    couponCode = it.uppercase()
                                    couponError = false
                                },
                                placeholder = { Text(if (isHi) "कूपन कोड डालें" else "Enter coupon code") },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD97706),
                                    cursorColor = Color(0xFFD97706)
                                ),
                                isError = couponError,
                                supportingText = if (couponError) {
                                    { Text(if (isHi) "अमान्य कूपन" else "Invalid coupon", color = Color(0xFFDC2626)) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (couponCode.isNotEmpty()) {
                                        val success = onApplyCoupon(couponCode)
                                        couponError = !success
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text(if (isHi) "लागू" else "Apply")
                            }
                        }
                        Text(
                            text = if (isHi) "कोड \"KISAN50\" आज़माएं" else "Try code \"KISAN50\"",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHi) "मूल्य विवरण" else "Price Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PriceRow(
                        label = if (isHi) "उप-योग (${cartItems.sumOf { it.quantity }} आइटम)" else "Subtotal (${cartItems.sumOf { it.quantity }} items)",
                        amount = "₹${subtotal.toInt()}"
                    )
                    if (discount > 0) {
                        PriceRow(
                            label = if (isHi) "कूपन छूट" else "Coupon Discount",
                            amount = "-₹${discount.toInt()}",
                            isDiscount = true
                        )
                    }
                    PriceRow(
                        label = if (isHi) "डिलीवरी शुल्क" else "Delivery Charges",
                        amount = if (deliveryCharge == 0.0) {
                            if (isHi) "निःशुल्क" else "FREE"
                        } else "₹${deliveryCharge.toInt()}",
                        isFree = deliveryCharge == 0.0
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isHi) "कुल राशि" else "Total Amount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "₹${total.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }

                    if (subtotal > 500) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHi) "₹500 से ऊपर के ऑर्डर पर मुफ्त डिलीवरी!" else "Free delivery on orders above ₹500!",
                            fontSize = 11.sp,
                            color = Color(0xFF059669)
                        )
                    }
                }
            }
        }

        // Bottom pay button
        Surface(
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onProceedPayment,
                enabled = cartItems.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF059669),
                    disabledContainerColor = Color(0xFF059669).copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isHi) "₹${total.toInt()} भुगतान करें" else "Pay ₹${total.toInt()} Now",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, amount: String, isDiscount: Boolean = false, isFree: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = amount,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                isDiscount -> Color(0xFF059669)
                isFree -> Color(0xFF059669)
                else -> Color(0xFF0F172A)
            }
        )
    }
}
