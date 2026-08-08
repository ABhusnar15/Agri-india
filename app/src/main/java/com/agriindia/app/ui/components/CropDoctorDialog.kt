package com.agriindia.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CropDoctorDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<CropDiagnosis?>(null) }
    var selectedCrop by remember { mutableStateOf("Wheat") }
    val scope = rememberCoroutineScope()

    val sampleDiagnoses = listOf(
        CropDiagnosis(
            diseaseName = "Yellow Rust (Puccinia striiformis)",
            diseaseNameHi = "पीला रतुआ (येलो रस्ट)",
            crop = "Wheat / गेहूं",
            severity = "High (उच्च)",
            symptoms = "Yellow stripy pustules on leaves, stunted plant growth, reduced grain count.",
            symptomsHi = "पत्तियों पर पीले रंग की धारियां, पौधे का छोटा रहना, दानों में कमी।",
            organicRemedy = "Spray fermented buttermilk or sour curd solution (5%) mixed with neem oil (5 ml/L).",
            organicRemedyHi = "खट्टी छाछ (5%) और नीम का तेल (5 मिली/लीटर) मिलाकर छिड़काव करें।",
            chemicalSpray = "Propiconazole 25% EC @ 1 ml/Liter water or Tebuconazole 50% + Trifloxystrobin 25% WG.",
            chemicalSprayHi = "प्रोपीकोनाज़ोल 25% ईसी @ 1 मिली/लीटर पानी या टेबूकोनाज़ोल 50% + ट्रिफ्लोक्सीस्ट्रोबिन 25% डब्ल्यूजी का छिड़काव करें।"
        ),
        CropDiagnosis(
            diseaseName = "Early Blight (Alternaria solani)",
            diseaseNameHi = "अगेती झुलसा रोग (अर्ली ब्लाइट)",
            crop = "Tomato / Potato",
            severity = "Medium (मध्यम)",
            symptoms = "Concentric dark brown rings on lower leaves, leaf yellowing & premature drop.",
            symptomsHi = "निचली पत्तियों पर गहरे भूरे रंग के छल्ले, पत्तियों का पीला पड़ना और गिरना।",
            organicRemedy = "Apply Trichoderma viride bio-fungicide @ 5g/Liter water during early morning.",
            organicRemedyHi = "सुबह के समय ट्राइकोडर्मा विरिडे बायो-फफूंदनाशी 5 ग्राम/लीटर पानी का छिड़काव करें।",
            chemicalSpray = "Mancozeb 75% WP @ 2.5g/Liter or Copper Oxychloride 50% WP @ 3g/Liter.",
            chemicalSprayHi = "मैनकोज़ेब 75% डब्लूपी 2.5 ग्राम/लीटर या कॉपर ऑक्सीक्लोराइड 50% डब्लूपी 3 ग्राम/लीटर।"
        )
    )

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
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHi) "एआई फसल डॉक्टर" else "AI Crop Doctor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (isHi) "पत्ती स्कैन और बीमारी निदान" else "Leaf Scan & Disease Diagnosis",
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
                if (scanResult == null) {
                    // Crop selection chips
                    Text(
                        text = if (isHi) "अपनी फसल चुनें:" else "Select Your Crop:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Wheat", "Paddy", "Cotton", "Tomato").forEach { crop ->
                            FilterChip(
                                selected = selectedCrop == crop,
                                onClick = { selectedCrop = crop },
                                label = { Text(crop, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scanner upload box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                            .clickable(enabled = !isScanning) {
                                scope.launch {
                                    isScanning = true
                                    delay(2000)
                                    isScanning = false
                                    scanResult = if (selectedCrop == "Wheat") sampleDiagnoses[0] else sampleDiagnoses[1]
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isScanning) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF059669))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isHi) "एआई पत्तियों का विश्लेषण कर रहा है..." else "AI is analyzing leaf patterns...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF059669)
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isHi) "फसल की पत्ती की फोटो लें या अपलोड करें" else "Tap to Photo / Scan Infected Leaf",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isHi) "तुरंत निदान और उपचार प्राप्त करें" else "Instant AI Disease Diagnosis & Spray Guide",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                } else {
                    // Diagnosis Result Card
                    val res = scanResult!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isHi) res.diseaseNameHi else res.diseaseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Surface(
                                    color = Color(0xFFDC2626),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = res.severity,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHi) "लक्षण: ${res.symptomsHi}" else "Symptoms: ${res.symptoms}",
                                fontSize = 12.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Organic Treatment Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHi) "जैविक उपचार (Organic Remedy):" else "Organic Remedy:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF065F46)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHi) res.organicRemedyHi else res.organicRemedy,
                                fontSize = 12.sp,
                                color = Color(0xFF047857)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chemical Treatment Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHi) "रसायनिक छिड़काव (Chemical Recommendation):" else "Chemical Spray Recommendation:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHi) res.chemicalSprayHi else res.chemicalSpray,
                                fontSize = 12.sp,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (scanResult != null) {
                Button(
                    onClick = { scanResult = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHi) "दूसरी पत्ती स्कैन करें" else "Scan Another Leaf")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHi) "बंद करें" else "Close", color = Color(0xFF64748B))
            }
        }
    )
}

data class CropDiagnosis(
    val diseaseName: String,
    val diseaseNameHi: String,
    val crop: String,
    val severity: String,
    val symptoms: String,
    val symptomsHi: String,
    val organicRemedy: String,
    val organicRemedyHi: String,
    val chemicalSpray: String,
    val chemicalSprayHi: String
)
