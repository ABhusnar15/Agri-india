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
import com.agriindia.app.model.SoilHealthReport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilHealthDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI

    var phValue by remember { mutableFloatStateOf(6.8f) }
    var nitrogenKg by remember { mutableFloatStateOf(210f) }
    var phosphorusKg by remember { mutableFloatStateOf(18f) }
    var potassiumKg by remember { mutableFloatStateOf(190f) }
    var organicCarbon by remember { mutableFloatStateOf(0.48f) }
    var isAnalyzed by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf<SoilHealthReport?>(null) }

    fun calculateSoilReport() {
        val condition = when {
            phValue < 6.0f -> "Acidic Soil (अम्लीय मृदा)"
            phValue > 7.8f -> "Alkaline / Calcareous Soil (क्षारीय मृदा)"
            else -> "Optimal Neutral Soil (उत्तम दोमट मृदा)"
        }

        val nStatus = if (nitrogenKg < 240) "Low (कम)" else if (nitrogenKg < 400) "Medium (मध्यम)" else "High (उच्च)"
        val pStatus = if (phosphorusKg < 15) "Low (कम)" else if (phosphorusKg < 30) "Medium (मध्यम)" else "High (उच्च)"
        val kStatus = if (potassiumKg < 140) "Low (कम)" else if (potassiumKg < 280) "Medium (मध्यम)" else "High (उच्च)"

        val ureaDose = if (nitrogenKg < 240) 65.0 else 45.0
        val dapDose = if (phosphorusKg < 20) 45.0 else 30.0
        val mopDose = if (potassiumKg < 200) 25.0 else 15.0
        val soilAmendment = if (phValue < 6.0f) 200.0 else if (phValue > 7.8f) 250.0 else 0.0

        val recEn = if (phValue < 6.0f) {
            "Soil is moderately acidic. Apply Agricultural Lime (200 kg/acre) before sowing. Use rock phosphate with PSB culture."
        } else if (phValue > 7.8f) {
            "Soil is alkaline. Apply Agricultural Gypsum (250 kg/acre) and incorporate green manure (Dhaincha/Sunhemp) to lower pH."
        } else {
            "Soil health is optimal. Maintain organic matter with 2-3 tonnes Farm Yard Manure (FYM) and balanced NPK application."
        }

        val recHi = if (phValue < 6.0f) {
            "मृदा अम्लीय है। बुवाई से पूर्व कृषि चूना (200 किग्रा/एकड़) डालें तथा रॉक फॉस्फेट व पीएसबी कल्चर का प्रयोग करें।"
        } else if (phValue > 7.8f) {
            "मृदा क्षारीय है। कृषि जिप्सम (250 किग्रा/एकड़) डालें तथा ढैंचा या सनई की हरी खाद मिलाकर पीएच स्तर संतुलित करें।"
        } else {
            "मृदा का स्वास्थ्य उत्तम है। 2-3 टन गोबर की सड़ी खाद (FYM) व संतुलित एनपीके डालकर उर्वरकता बनाए रखें।"
        }

        report = SoilHealthReport(
            id = "SHC-${(10000..99999).random()}",
            ph = phValue.toDouble(),
            nitrogenKgPerHa = nitrogenKg.toDouble(),
            phosphorusKgPerHa = phosphorusKg.toDouble(),
            potassiumKgPerHa = potassiumKg.toDouble(),
            organicCarbonPercent = organicCarbon.toDouble(),
            zincPpm = 0.58,
            sulphurPpm = 9.2,
            soilCondition = condition,
            nitrogenStatus = nStatus,
            phosphorusStatus = pStatus,
            potassiumStatus = kStatus,
            recommendationEn = recEn,
            recommendationHi = recHi,
            ureaKgAcre = ureaDose,
            dapKgAcre = dapDose,
            mopKgAcre = mopDose,
            gypsumOrLimeKgAcre = soilAmendment
        )
        isAnalyzed = true
    }

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
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isHi) "डिजिटल मृदा स्वास्थ्य कार्ड" else "Smart Soil Health Card",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isHi) "एन-पी-के विश्लेषण एवं उर्वरक खुराक" else "NPK Lab Diagnosis & Fertilizer Dosage",
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
                // Soil pH Slider
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isHi) "मृदा पीएच (pH मान):" else "Soil pH Level:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Text(
                        text = "${String.format("%.1f", phValue)} (${if (phValue < 6.5) "Acidic" else if (phValue > 7.5) "Alkaline" else "Neutral"})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (phValue in 6.5f..7.5f) Color(0xFF16A34A) else Color(0xFFDC2626)
                    )
                }
                Slider(
                    value = phValue,
                    onValueChange = { phValue = it; isAnalyzed = false },
                    valueRange = 4.5f..9.5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFD97706), activeTrackColor = Color(0xFFD97706))
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Available Nitrogen (N)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isHi) "नाइट्रोजन (N kg/ha):" else "Available Nitrogen (N kg/ha):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${nitrogenKg.toInt()} kg/ha", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
                Slider(
                    value = nitrogenKg,
                    onValueChange = { nitrogenKg = it; isAnalyzed = false },
                    valueRange = 100f..500f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0284C7), activeTrackColor = Color(0xFF0284C7))
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Available Phosphorus (P)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isHi) "फास्फोरस (P kg/ha):" else "Available Phosphorus (P kg/ha):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${phosphorusKg.toInt()} kg/ha", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }
                Slider(
                    value = phosphorusKg,
                    onValueChange = { phosphorusKg = it; isAnalyzed = false },
                    valueRange = 5f..60f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF16A34A), activeTrackColor = Color(0xFF16A34A))
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Organic Carbon
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isHi) "जैविक कार्बन (% OC):" else "Organic Carbon (% OC):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${String.format("%.2f", organicCarbon)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                }
                Slider(
                    value = organicCarbon,
                    onValueChange = { organicCarbon = it; isAnalyzed = false },
                    valueRange = 0.2f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7C3AED), activeTrackColor = Color(0xFF7C3AED))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { calculateSoilReport() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHi) "मृदा रिपोर्ट व खाद पर्ची तैयार करें" else "Generate Fertilizer Prescription")
                }

                if (isAnalyzed && report != null) {
                    val r = report!!
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = r.soilCondition,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFB45309)
                                )
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = r.id,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isHi) r.recommendationHi else r.recommendationEn,
                                fontSize = 12.sp,
                                color = Color(0xFF451A03),
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFFFDE68A))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isHi) "प्रति एकड़ संस्तुत खाद खुराक (Kg/Acre):" else "Recommended Fertilizer Dosage (Kg/Acre):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Urea / यूरिया", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("${r.ureaKgAcre.toInt()} kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                                    }
                                }
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("DAP / डीएपी", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("${r.dapKgAcre.toInt()} kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    }
                                }
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("MOP / पोटाश", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("${r.mopKgAcre.toInt()} kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    }
                                }
                            }

                            if (r.gypsumOrLimeKgAcre > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color(0xFFFEF2F2),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (r.ph < 6.0) "कृषि चूना (Agricultural Lime): ${r.gypsumOrLimeKgAcre.toInt()} kg/acre" else "कृषि जिप्सम (Agricultural Gypsum): ${r.gypsumOrLimeKgAcre.toInt()} kg/acre",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF991B1B),
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
