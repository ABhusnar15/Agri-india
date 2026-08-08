package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    val isMr = language == AppLanguage.MARATHI

    var couponCode by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf(false) }
    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryPincode by remember { mutableStateOf("") }
    var deliveryPhone by remember { mutableStateOf("") }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val discount = if (appliedCoupon != null) subtotal * 0.1 else 0.0
    val deliveryCharge = if (subtotal > 500) 0.0 else 49.0
    val total = subtotal - discount + deliveryCharge

    // Validation rules
    val isAddressValid = deliveryAddress.trim().length >= 5
    val addressError = if (hasAttemptedSubmit && !isAddressValid) {
        if (isMr) "कृपया संपूर्ण पत्ता प्रविष्ट करा (किमान ५ अक्षरे)"
        else if (isHi) "कृपया पूरा पता दर्ज करें (कम से कम 5 अक्षर)"
        else "Please enter a complete delivery address (min 5 chars)"
    } else null

    val pincodeRegex = "^[1-9][0-9]{5}$".toRegex()
    val isPincodeValid = deliveryPincode.trim().matches(pincodeRegex)
    val pincodeError = if (hasAttemptedSubmit && !isPincodeValid) {
        if (isMr) "६ अंकी वैध पिनकोड टाका"
        else if (isHi) "6 अंकों का वैध पिनकोड दर्ज करें"
        else "Enter a valid 6-digit Pincode"
    } else null

    val phoneRegex = "^[6-9]\\d{9}$".toRegex()
    val isPhoneValid = deliveryPhone.trim().matches(phoneRegex)
    val phoneError = if (hasAttemptedSubmit && !isPhoneValid) {
        if (isMr) "१० अंकी वैध संपर्क नंबर टाका"
        else if (isHi) "10 अंकों का वैध संपर्क नंबर दर्ज करें"
        else "Enter a valid 10-digit phone number"
    } else null

    val isFormValid = isAddressValid && isPincodeValid && isPhoneValid && cartItems.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = if (isMr) "ऑर्डर तपासणी व देयक" else if (isHi) "चेकआउट" else "Checkout",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Error banner if submit clicked with errors
            if (hasAttemptedSubmit && !isFormValid) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMr) "कृपया पत्ता, पिनकोड व फोन नंबर बरोबर भरा" else if (isHi) "कृपया पता, पिनकोड व फोन नंबर सही भरें" else "Please complete required delivery details accurately",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

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
                            text = if (isMr) "ऑर्डर केलेल्या वस्तू (${cartItems.size})" else if (isHi) "ऑर्डर की गई वस्तुएं (${cartItems.size})" else "Order Items (${cartItems.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    cartItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "₹${item.product.price.toInt()} (${item.product.category})",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Quantity controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.product.id, -1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "${item.quantity}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = { onUpdateQuantity(item.product.id, 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (index < cartItems.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Address with Field-by-Field Live Validation
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
                            text = if (isMr) "वितरण पत्ता व संपर्क *" else if (isHi) "डिलीवरी पता व संपर्क *" else "Delivery Address & Contact *",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address field
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        isError = addressError != null,
                        label = { Text(if (isMr) "गावाचे नाव / संपूर्ण पत्ता *" else if (isHi) "पूरा पता / गांव का नाम *" else "Full Delivery Address *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Home, contentDescription = null, tint = if (addressError != null) Color(0xFFDC2626) else Color(0xFF059669))
                        },
                        trailingIcon = {
                            if (addressError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626))
                            } else if (isAddressValid) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF059669))
                            }
                        },
                        supportingText = {
                            if (addressError != null) {
                                Text(addressError, color = Color(0xFFDC2626), fontSize = 11.sp)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pincode & Contact Phone Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = deliveryPincode,
                            onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) deliveryPincode = it },
                            isError = pincodeError != null,
                            label = { Text(if (isMr) "पिनकोड *" else if (isHi) "पिनकोड *" else "Pincode *") },
                            leadingIcon = {
                                Icon(Icons.Outlined.PinDrop, contentDescription = null, tint = if (pincodeError != null) Color(0xFFDC2626) else Color(0xFF059669))
                            },
                            supportingText = {
                                if (pincodeError != null) {
                                    Text(pincodeError, color = Color(0xFFDC2626), fontSize = 10.sp)
                                } else {
                                    Text("${deliveryPincode.length}/6", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF059669),
                                errorBorderColor = Color(0xFFDC2626)
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = deliveryPhone,
                            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) deliveryPhone = it },
                            isError = phoneError != null,
                            label = { Text(if (isMr) "फोन नंबर *" else if (isHi) "फोन नंबर *" else "Phone *") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Phone, contentDescription = null, tint = if (phoneError != null) Color(0xFFDC2626) else Color(0xFF059669))
                            },
                            supportingText = {
                                if (phoneError != null) {
                                    Text(phoneError, color = Color(0xFFDC2626), fontSize = 10.sp)
                                } else {
                                    Text("${deliveryPhone.length}/10", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF059669),
                                errorBorderColor = Color(0xFFDC2626)
                            ),
                            modifier = Modifier.weight(1.3f),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Coupon code with validation
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
                            text = if (isMr) "सूट कूपन कोड" else if (isHi) "कूपन कोड" else "Coupon Code",
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
                                    text = if (isMr) "कूपन \"$appliedCoupon\" लागू झाले! 10% सूट" else if (isHi) "कूपन \"$appliedCoupon\" लागू! 10% छूट" else "Coupon \"$appliedCoupon\" applied! 10% off",
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
                                placeholder = { Text(if (isMr) "कूपन कोड टाका" else if (isHi) "कूपन कोड डालें" else "Enter coupon code") },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD97706),
                                    cursorColor = Color(0xFFD97706)
                                ),
                                isError = couponError,
                                supportingText = if (couponError) {
                                    { Text(if (isMr) "अवैध कूपन कोड" else if (isHi) "अमान्य कूपन" else "Invalid coupon code", color = Color(0xFFDC2626)) }
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
                                Text(if (isMr) "लागू करा" else if (isHi) "लागू" else "Apply")
                            }
                        }
                        Text(
                            text = if (isMr) "कोड \"KISAN50\" वापरा" else if (isHi) "कोड \"KISAN50\" आज़माएं" else "Try code \"KISAN50\"",
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
                        text = if (isMr) "रक्कम तपशील" else if (isHi) "मूल्य विवरण" else "Price Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PriceRow(
                        label = if (isMr) "उप-एकूण (${cartItems.sumOf { it.quantity }} वस्तू)" else if (isHi) "उप-योग (${cartItems.sumOf { it.quantity }} आइटम)" else "Subtotal (${cartItems.sumOf { it.quantity }} items)",
                        amount = "₹${subtotal.toInt()}"
                    )
                    if (discount > 0) {
                        PriceRow(
                            label = if (isMr) "कूपन सूट" else if (isHi) "कूपन छूट" else "Coupon Discount",
                            amount = "-₹${discount.toInt()}",
                            isDiscount = true
                        )
                    }
                    PriceRow(
                        label = if (isMr) "डिलिव्हरी शुल्क" else if (isHi) "डिलीवरी शुल्क" else "Delivery Charges",
                        amount = if (deliveryCharge == 0.0) {
                            if (isMr) "मोफत" else if (isHi) "निःशुल्क" else "FREE"
                        } else "₹${deliveryCharge.toInt()}",
                        isFree = deliveryCharge == 0.0
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isMr) "एकूण देय रक्कम" else if (isHi) "कुल राशि" else "Total Amount",
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
                            text = if (isMr) "₹५०० पेक्षा जास्त ऑर्डर्सवर मोफत डिलिव्हरी!" else if (isHi) "₹500 से ऊपर के ऑर्डर पर मुफ्त डिलीवरी!" else "Free delivery on orders above ₹500!",
                            fontSize = 11.sp,
                            color = Color(0xFF059669)
                        )
                    }
                }
            }
        }

        // Bottom pay button with validation gate
        Surface(
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = {
                    hasAttemptedSubmit = true
                    if (isFormValid) {
                        onProceedPayment()
                    }
                },
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
                    text = if (isMr) "₹${total.toInt()} सुरक्षित पेमेंट करा" else if (isHi) "₹${total.toInt()} भुगतान करें" else "Pay ₹${total.toInt()} Now",
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
