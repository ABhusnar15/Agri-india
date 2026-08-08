package com.agriindia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun FertilizerCalculatorDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    var acresText by remember { mutableStateOf("2") }
    var selectedCrop by remember { mutableStateOf("Paddy / धान") }
    var expandedCrop by remember { mutableStateOf(false) }

    val cropList = listOf(
        "Paddy / धान",
        "Wheat / गेहूं",
        "Cotton / कपास",
        "Sugarcane / गन्ना",
        "Maize / मक्का",
        "Mustard / सरसों"
    )

    val acres = acresText.toDoubleOrNull() ?: 1.0

    // Fertilizer dosage multipliers per Acre
    val (ureaBags, dapBags, mopBags, zincKg) = when (selectedCrop) {
        "Wheat / गेहूं" -> Quad(1.5 * acres, 0.75 * acres, 0.4 * acres, 5.0 * acres)
        "Cotton / कपास" -> Quad(2.0 * acres, 1.0 * acres, 0.6 * acres, 8.0 * acres)
        "Sugarcane / गन्ना" -> Quad(3.5 * acres, 1.5 * acres, 1.0 * acres, 10.0 * acres)
        "Maize / मक्का" -> Quad(1.8 * acres, 0.8 * acres, 0.5 * acres, 6.0 * acres)
        else -> Quad(1.6 * acres, 0.8 * acres, 0.5 * acres, 6.0 * acres) // Paddy default
    }

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
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHi) "N-P-K उर्वरक कैलकुलेटर" else "N-P-K Fertilizer Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = if (isHi) "सटीक खाद की मात्रा और एकड़ हिसाब" else "Exact Nutrient & Bag Requirements",
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
                // Select Crop Dropdown
                Text(
                    text = if (isHi) "फसल चुनें:" else "Select Crop:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedCrop,
                    onExpandedChange = { expandedCrop = !expandedCrop }
                ) {
                    OutlinedTextField(
                        value = selectedCrop,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCrop) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCrop,
                        onDismissRequest = { expandedCrop = false }
                    ) {
                        cropList.forEach { crop ->
                            DropdownMenuItem(
                                text = { Text(crop) },
                                onClick = {
                                    selectedCrop = crop
                                    expandedCrop = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Acreage Input
                OutlinedTextField(
                    value = acresText,
                    onValueChange = { acresText = it },
                    label = { Text(if (isHi) "खेत का क्षेत्रफल (एकड़ में)" else "Land Area (in Acres)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.SquareFoot, contentDescription = null, tint = Color(0xFFD97706))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Required Bags Output Card
                Text(
                    text = if (isHi) "आवश्यक खाद मात्रा (${acres} एकड़ के लिए):" else "Calculated Nutrient Bags (${acres} Acres):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FertilizerBagTile(
                    name = "Urea (Nitrogen 46%)",
                    nameHi = "यूरिया (नाइट्रोजन 46%)",
                    quantity = "%.1f Bags (45 kg)".format(ureaBags),
                    color = Color(0xFFF0FDF4),
                    tint = Color(0xFF059669)
                )

                Spacer(modifier = Modifier.height(6.dp))

                FertilizerBagTile(
                    name = "DAP (Phosphorus 46% + N 18%)",
                    nameHi = "डीएपी (फास्फोरस + नाइट्रोजन)",
                    quantity = "%.1f Bags (50 kg)".format(dapBags),
                    color = Color(0xFFEFF6FF),
                    tint = Color(0xFF2563EB)
                )

                Spacer(modifier = Modifier.height(6.dp))

                FertilizerBagTile(
                    name = "MOP (Potash 60%)",
                    nameHi = "एमओपी (पोटाश 60%)",
                    quantity = "%.1f Bags (50 kg)".format(mopBags),
                    color = Color(0xFFFEF2F2),
                    tint = Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.height(6.dp))

                FertilizerBagTile(
                    name = "Zinc Sulphate (Micro-nutrient)",
                    nameHi = "जिंक सल्फेट (सूक्ष्म पोषक)",
                    quantity = "%.1f kg".format(zincKg),
                    color = Color(0xFFFEF3C7),
                    tint = Color(0xFFD97706)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isHi) "ठीक है" else "Got It")
            }
        }
    )
}

@Composable
fun FertilizerBagTile(
    name: String,
    nameHi: String,
    quantity: String,
    color: Color,
    tint: Color
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(text = nameHi, fontSize = 10.sp, color = Color(0xFF64748B))
            }
            Text(text = quantity, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = tint)
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
