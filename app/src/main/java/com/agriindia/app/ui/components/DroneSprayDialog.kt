package com.agriindia.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.DroneBooking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroneSprayDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI
    val scope = rememberCoroutineScope()

    var selectedCrop by remember { mutableStateOf("Wheat") }
    var acreage by remember { mutableFloatStateOf(3f) }
    var selectedChemical by remember { mutableStateOf("IFFCO Nano Urea + Bio-Stimulant") }
    var isBooking by remember { mutableStateOf(false) }
    var confirmedBooking by remember { mutableStateOf<DroneBooking?>(null) }

    val chemicals = listOf(
        "IFFCO Nano Urea + Bio-Stimulant" to "इफको नैनो यूरिया + बायो-उत्तेजक",
        "Liquid Zinc 39.5% + Micronutrients" to "तरल जिंक 39.5% + सूक्ष्म पोषक",
        "Bio-Fungicide (Trichoderma + Neem)" to "जैव फफूंदनाशी (ट्राइकोडर्मा + नीम)",
        "Prophylactic Insecticide Spray" to "कीट रोकथाम कीटनाशक स्प्रे"
    )

    val crops = listOf(
        "Wheat" to if (isMr) "गहू" else "गेहूं",
        "Paddy" to if (isMr) "भात" else "धान",
        "Cotton" to if (isMr) "कापूस" else "कपास",
        "Mustard" to if (isMr) "मोहरी" else "सरसों",
        "Sugarcane" to if (isMr) "ऊस" else "गन्ना",
        "Soybean" to if (isMr) "सोयाबीन" else "सोयाबीन"
    )

    val baseRate = 600.0 // Rs per acre
    val subsidyDiscount = 0.50 // 50% Subsidy under Sub-Mission on Agricultural Mechanization (SMAM)
    val totalCost = acreage * baseRate * (1 - subsidyDiscount)
    val subsidySaved = acreage * baseRate * subsidyDiscount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2FE),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "ड्रोन फवारणी सेवा बुकिंग"
                                AppLanguage.HINDI -> "ड्रोन स्प्रे सेवा बुकिंग"
                                else -> "Kisan Drone Spray Booking"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "५०% शासकीय अनुदानासह अचूक फवारणी"
                                AppLanguage.HINDI -> "50% सरकारी सब्सिडी सहित सटीक छिड़काव"
                                else -> "Precision Foliar Spray with 50% Subsidy"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (confirmedBooking == null) {
                    // Subsidized banner
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHi) "SMAM योजना: ₹300/एकड़ सरकारी सब्सिडी लागू" else "SMAM Scheme: ₹300/Acre Govt Subsidy Applied",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF065F46)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Crop Selection
                    Text(
                        text = if (isHi) "फसल चुनें:" else "Select Crop:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        crops.take(3).forEach { (cropEn, cropHi) ->
                            FilterChip(
                                selected = selectedCrop == cropEn,
                                onClick = { selectedCrop = cropEn },
                                label = { Text(if (isHi) cropHi else cropEn, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        crops.drop(3).forEach { (cropEn, cropHi) ->
                            FilterChip(
                                selected = selectedCrop == cropEn,
                                onClick = { selectedCrop = cropEn },
                                label = { Text(if (isHi) cropHi else cropEn, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Acreage Slider
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isHi) "खेत का क्षेत्रफल (एकड़):" else "Farm Acreage (Acres):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "${acreage.toInt()} Acres (${(acreage * 0.4047).toString().take(4)} Ha)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                    Slider(
                        value = acreage,
                        onValueChange = { acreage = it },
                        valueRange = 1f..20f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0284C7),
                            activeTrackColor = Color(0xFF0284C7)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chemical Type
                    Text(
                        text = if (isHi) "छिड़काव सामग्री चुनें:" else "Select Foliar Spray Solution:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    chemicals.forEach { (chemEn, chemHi) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedChemical == chemEn) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedChemical == chemEn) Color(0xFF3B82F6) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedChemical == chemEn,
                                    onClick = { selectedChemical = chemEn },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHi) chemHi else chemEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedChemical == chemEn) FontWeight.SemiBold else FontWeight.Normal,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calculation Summary Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isHi) "मूल शुल्क (₹600/एकड़)" else "Standard Fee (₹600/Acre)", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text("₹${(acreage * baseRate).toInt()}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isHi) "सरकारी सब्सिडी छूट (-50%)" else "Govt Subsidy (-50%)", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                Text("-₹${subsidySaved.toInt()}", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                            }
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFCBD5E1))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (isHi) "कुल देय राशि:" else "Total Farmer Payable:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    "₹${totalCost.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isBooking = true
                                delay(1800)
                                isBooking = false
                                confirmedBooking = DroneBooking(
                                    id = "DRONE-IN-${(1000..9999).random()}",
                                    cropName = selectedCrop,
                                    cropNameHi = crops.firstOrNull { it.first == selectedCrop }?.second ?: selectedCrop,
                                    acreage = acreage.toDouble(),
                                    sprayChemical = selectedChemical,
                                    sprayChemicalHi = chemicals.firstOrNull { it.first == selectedChemical }?.second ?: selectedChemical,
                                    scheduledDate = "Tomorrow, 06:30 AM (Morning Slot)",
                                    status = "Confirmed & Pilot Assigned",
                                    pilotName = "Vikramjit Singh (DGCA Certified)",
                                    pilotPhone = "+91 98765 43210",
                                    batterySlotsNeeded = (acreage / 2).toInt().coerceAtLeast(2),
                                    totalCost = totalCost,
                                    subsidySaved = subsidySaved
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isBooking
                    ) {
                        if (isBooking) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHi) "ड्रोन पायलट खोज रहे हैं..." else "Assigning Certified Drone Pilot...", fontSize = 12.sp)
                        } else {
                            Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isHi) "ड्रोन स्प्रे स्लॉट बुक करें (₹${totalCost.toInt()})" else "Confirm Drone Booking (₹${totalCost.toInt()})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Confirmed Booking Card
                    val b = confirmedBooking!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isHi) "ड्रोन बुकिंग सफल!" else "Drone Spray Booking Confirmed!",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534)
                                    )
                                    Text(text = "Booking ID: ${b.id}", fontSize = 11.sp, color = Color(0xFF15803D))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pilot info
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Pilot: ${b.pilotName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Text(text = "Contact: ${b.pilotPhone}", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(text = "Slot: ${b.scheduledDate}", fontSize = 11.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.SemiBold)
                                    Text(text = "Crop & Area: ${b.cropName} (${b.acreage} Acres)", fontSize = 11.sp, color = Color(0xFF334155))
                                    Text(text = "Solution: ${b.sprayChemical}", fontSize = 11.sp, color = Color(0xFF334155))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isHi) "कुल भुगतान राशि:" else "Total Amount Paid:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("₹${b.totalCost.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isHi) "पूर्ण / बंद करें" else "Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
