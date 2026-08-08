package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellProduceDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitListing: (String, Double, Double, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    var cropName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var villageLocation by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    // Validation
    val isCropValid = cropName.trim().length >= 2
    val cropError = if (hasAttemptedSubmit && !isCropValid) {
        if (isMr) "कृपया पिकाचे नाव टाका (उदा. सोयाबीन, कांदा)"
        else if (isHi) "कृपया फसल का नाम दर्ज करें (उदा. गेहूं, सोयाबीन)"
        else "Please enter crop name (min 2 chars)"
    } else null

    val isQuantityValid = quantityText.toDoubleOrNull() != null && (quantityText.toDoubleOrNull() ?: 0.0) > 0
    val quantityError = if (hasAttemptedSubmit && !isQuantityValid) {
        if (isMr) "वैध वजन/प्रमाण टाका (> ० क्विंटल)"
        else if (isHi) "वैध मात्रा दर्ज करें (> 0 क्विंटल)"
        else "Enter valid quantity (> 0 Quintals)"
    } else null

    val isPriceValid = priceText.toDoubleOrNull() != null && (priceText.toDoubleOrNull() ?: 0.0) > 0
    val priceError = if (hasAttemptedSubmit && !isPriceValid) {
        if (isMr) "अपेक्षित दर टाका (₹/क्विंटल)"
        else if (isHi) "अपेक्षित मूल्य दर्ज करें (₹/क्विंटल)"
        else "Enter expected rate (₹/Qtl)"
    } else null

    val isLocationValid = villageLocation.trim().length >= 3
    val locationError = if (hasAttemptedSubmit && !isLocationValid) {
        if (isMr) "गावाचे किंवा बाजार समितीचे नाव टाका"
        else if (isHi) "गांव या मंडी का नाम दर्ज करें"
        else "Enter village or Mandi location"
    } else null

    val phoneRegex = "^[6-9]\\d{9}$".toRegex()
    val isPhoneValid = contactPhone.trim().matches(phoneRegex)
    val phoneError = if (hasAttemptedSubmit && !isPhoneValid) {
        if (isMr) "१० अंकी वैध मोबाइल नंबर टाका"
        else if (isHi) "10 अंकों का वैध मोबाइल नंबर दर्ज करें"
        else "Enter valid 10-digit mobile number"
    } else null

    val isFormValid = isCropValid && isQuantityValid && isPriceValid && isLocationValid && isPhoneValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Sell,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isMr) "शेतमाल थेट विक्री नोंदणी" else if (isHi) "फसल प्रत्यक्ष बिक्री पंजीकरण" else "List Produce for Direct Sale",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = if (isMr) "थेट व्यापाऱ्यांना व प्रक्रियादारांना विका" else if (isHi) "सीधे व्यापारियों और मिलों को बेचें" else "Connect direct with verified buyers",
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
                if (isSubmitted) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isMr) "आपला शेतमाल यशस्वीरित्या नोंदवला गेला आहे!" else if (isHi) "आपकी फसल सफलतापूर्वक पंजीकृत हो गई है!" else "Produce listed successfully!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = if (isMr) "जवळपासचे खरेदीदार लवकरच आपल्याशी संपर्क साधतील." else if (isHi) "निकटतम खरीदार जल्द ही आपसे संपर्क करेंगे।" else "Verified buyers in your district will contact you soon.",
                                fontSize = 11.sp,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                } else {
                    if (hasAttemptedSubmit && !isFormValid) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isMr) "कृपया लाल रंगातील त्रुटी तपासून योग्य माहिती भरा" else if (isHi) "कृपया सभी आवश्यक फ़ील्ड सही भरें" else "Please fill all required fields correctly",
                                color = Color(0xFFDC2626),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Crop Name
                    OutlinedTextField(
                        value = cropName,
                        onValueChange = { cropName = it },
                        isError = cropError != null,
                        label = { Text(if (isMr) "पिकाचे नाव *" else if (isHi) "फसल का नाम *" else "Crop Name *") },
                        placeholder = { Text("उदा. सोयाबीन, कांदा, कापूस", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Eco, contentDescription = null, tint = if (cropError != null) Color(0xFFDC2626) else Color(0xFF059669)) },
                        trailingIcon = {
                            if (cropError != null) Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626))
                            else if (isCropValid) Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF059669))
                        },
                        supportingText = { if (cropError != null) Text(cropError, color = Color(0xFFDC2626), fontSize = 10.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quantity and Price Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            isError = quantityError != null,
                            label = { Text(if (isMr) "प्रमाण (क्विंटल) *" else if (isHi) "मात्रा (क्विंटल) *" else "Quantity (Qtl) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = { if (quantityError != null) Text(quantityError, color = Color(0xFFDC2626), fontSize = 9.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            isError = priceError != null,
                            label = { Text(if (isMr) "अपेक्षित दर (₹) *" else if (isHi) "दर (₹/क्विंटल) *" else "Rate (₹/Qtl) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = { if (priceError != null) Text(priceError, color = Color(0xFFDC2626), fontSize = 9.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Village / District location
                    OutlinedTextField(
                        value = villageLocation,
                        onValueChange = { villageLocation = it },
                        isError = locationError != null,
                        label = { Text(if (isMr) "गाव / तालुका / जिल्हा *" else if (isHi) "गांव / तहसील / जिला *" else "Village / Location *") },
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = if (locationError != null) Color(0xFFDC2626) else Color(0xFF059669)) },
                        supportingText = { if (locationError != null) Text(locationError, color = Color(0xFFDC2626), fontSize = 10.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contact phone
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) contactPhone = it },
                        isError = phoneError != null,
                        label = { Text(if (isMr) "संपर्क मोबाइल नंबर *" else if (isHi) "मोबाइल नंबर *" else "Mobile Number *") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = if (phoneError != null) Color(0xFFDC2626) else Color(0xFF059669)) },
                        supportingText = {
                            if (phoneError != null) Text(phoneError, color = Color(0xFFDC2626), fontSize = 10.sp)
                            else Text("${contactPhone.length}/10 digits", fontSize = 10.sp, color = Color(0xFF64748B))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (isSubmitted) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isMr) "पूर्ण झाले" else if (isHi) "ठीक है" else "Done")
                }
            } else {
                Button(
                    onClick = {
                        hasAttemptedSubmit = true
                        if (isFormValid) {
                            val q = quantityText.toDoubleOrNull() ?: 0.0
                            val p = priceText.toDoubleOrNull() ?: 0.0
                            onSubmitListing(cropName.trim(), q, p, villageLocation.trim(), contactPhone.trim())
                            isSubmitted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isMr) "शेतमाल विक्रीसाठी नोंदवा" else if (isHi) "बिक्री के लिए दर्ज करें" else "List Produce")
                }
            }
        },
        dismissButton = {
            if (!isSubmitted) {
                TextButton(onClick = onDismiss) {
                    Text(if (isMr) "रद्द करा" else if (isHi) "रद्द करें" else "Cancel", color = Color(0xFF64748B))
                }
            }
        }
    )
}
