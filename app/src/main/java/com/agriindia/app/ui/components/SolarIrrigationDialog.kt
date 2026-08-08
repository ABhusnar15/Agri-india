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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.SolarPumpConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarIrrigationDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    var selectedHp by remember { mutableDoubleStateOf(5.0) }
    var selectedPumpType by remember { mutableStateOf("Submersible DC") }
    var landAcres by remember { mutableFloatStateOf(4f) }

    // PM-KUSUM calculation (Component B)
    // 3 HP: ~₹1,65,000, 5 HP: ~₹2,40,000, 7.5 HP: ~₹3,30,000
    val costBase = when (selectedHp) {
        3.0 -> 165000.0
        5.0 -> 240000.0
        7.5 -> 330000.0
        else -> 410000.0
    }

    val centralSubsidy = costBase * 0.30 // 30% MNRE
    val stateSubsidy = costBase * 0.30   // 30% State Govt
    val bankLoan = costBase * 0.30       // 30% NABARD / Soft loan
    val farmerShare = costBase * 0.10    // Only 10% direct farmer contribution
    val annualDieselSaving = selectedHp * 14500.0 // ~₹72,500/year for 5 HP
    val dailyWaterDischarge = (selectedHp * 18000).toInt()

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
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SolarPower,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "पीएम-कुसुम सौर कृषी पंप"
                                AppLanguage.HINDI -> "पीएम-कुसुम सोलर पंप योजना"
                                else -> "PM-KUSUM Solar Pump Planner"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "६०% शासकीय अनुदान + ३०% बँक कर्ज"
                                AppLanguage.HINDI -> "60% सरकारी सब्सिडी + 30% बैंक ऋण"
                                else -> "60% Govt Subsidy & Diesel Replacement"
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
                // Subsidy guarantee badge
                Surface(
                    color = Color(0xFFFEF9C3),
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
                            tint = Color(0xFFCA8A04),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "किसान अंशदान मात्र 10%! 60% केंद्र व राज्य अनुदान" else "Farmer Pays Just 10%! 60% Central + State Subsidy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF854D0E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pump Capacity (HP)
                Text(
                    text = if (isHi) "सोलर पंप क्षमता (HP):" else "Select Solar Pump Capacity (HP):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(3.0 to "3 HP", 5.0 to "5 HP", 7.5 to "7.5 HP", 10.0 to "10 HP").forEach { (hp, label) ->
                        FilterChip(
                            selected = selectedHp == hp,
                            onClick = { selectedHp = hp },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pump Type
                Text(
                    text = if (isHi) "पंप का प्रकार:" else "Select Pump Type:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Submersible DC" to "सबमर्सिबल डीसी", "Surface AC" to "सतही एसी").forEach { (typeEn, typeHi) ->
                        FilterChip(
                            selected = selectedPumpType == typeEn,
                            onClick = { selectedPumpType = typeEn },
                            label = { Text(if (isHi) typeHi else typeEn, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cost & Subsidy Breakdown Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isHi) "कुल बेंचमार्क लागत:" else "Total Benchmark Cost:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("₹${costBase.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isHi) "केंद्र सरकार सब्सिडी (30%):" else "Central Subsidy (30%):", fontSize = 11.sp, color = Color(0xFF16A34A))
                            Text("-₹${centralSubsidy.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isHi) "राज्य सरकार सब्सिडी (30%):" else "State Govt Subsidy (30%):", fontSize = 11.sp, color = Color(0xFF16A34A))
                            Text("-₹${stateSubsidy.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isMr) "बँक कर्ज सुविधा (३०%):" else if (isHi) "बैंक ऋण सुविधा (30%):" else "Bank Loan / Soft Financing (30%):", fontSize = 11.sp, color = Color(0xFF0284C7))
                            Text("₹${bankLoan.toInt()}", fontSize = 11.sp, color = Color(0xFF0284C7))
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFCBD5E1))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (isMr) "शेतकरी थेट देय रक्कम (१०%):" else if (isHi) "किसान प्रत्यक्ष भुगतान (10%):" else "Farmer Direct Share (10%):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                "₹${farmerShare.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Annual Savings card
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "वार्षिक डीजल बचत: ₹${annualDieselSaving.toInt()} प्रति वर्ष" else "Estimated Annual Diesel Savings: ₹${annualDieselSaving.toInt()}/yr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                        }
                        Text(
                            text = if (isHi) "दैनिक जल प्रवाह: ~${dailyWaterDischarge} लीटर/दिन" else "Estimated Water Discharge: ~${dailyWaterDischarge} Liters/Day",
                            fontSize = 11.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
