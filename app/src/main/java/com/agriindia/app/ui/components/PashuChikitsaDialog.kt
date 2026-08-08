package com.agriindia.app.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.LivestockDisease

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PashuChikitsaDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    var selectedAnimal by remember { mutableStateOf("Cow / गाय / गाय") }
    var selectedSymptom by remember { mutableStateOf("Mastitis / थनैला / स्तनदाह") }
    var diagnosedDisease by remember { mutableStateOf<LivestockDisease?>(null) }

    val diseases = listOf(
        LivestockDisease(
            diseaseName = "Mastitis (थन का रोग / थनैला / स्तनदाह)",
            diseaseNameHi = "थनैला रोग (स्तन शोथ / स्तनदाह)",
            animalType = "Cow / Buffalo / गाय / म्हैस",
            severity = "High (उच्च / गंभीर)",
            symptoms = "Swollen, hard and hot udder; blood or clots in milk; drop in milk yield and high fever.",
            symptomsHi = "थन में सूजन, लाली व कड़ापन; दूध में रक्त या छिछड़े आना; दूध उत्पादन में भारी गिरावट।",
            treatment = "Clean udder with Potassium Permanganate (1:1000). Apply Mastilep herbal gel. Administer Intramammary Ceftiofur/Amoxicillin under vet guidance.",
            treatmentHi = "पोटेशियम परमैंगनेट के हल्के घोल से थन धोएं। मैस्टिलेप जेल लगाएं। तुरंत पशु चिकित्सक से एंटीबायोटिक इन्फ्यूजन लगवाएं।",
            vaccineDue = "N/A - Regular hygiene & teat dipping after milking."
        ),
        LivestockDisease(
            diseaseName = "Foot & Mouth Disease / FMD (खुरपका-मुंहपका / लाळ्या खुरकूत)",
            diseaseNameHi = "खुरपका-मुंहपका रोग (लाळ्या खुरकूत)",
            animalType = "Cow / Buffalo / Goat",
            severity = "Critical (गंभीर)",
            symptoms = "High fever, excessive salivation, blisters on tongue, lips, and feet lesions causing severe lameness.",
            symptomsHi = "तेज बुखार, मुंह से लगातार लार टपकना, जीभ व मसूड़ों पर छाले, खुरों के बीच घाव से लंगड़ाना।",
            treatment = "Wash mouth with 1% Alum (फिटकरी) water and feet with 1% Copper Sulphate. Provide soft gruel (दलिया).",
            treatmentHi = "मुंह को 1% फिटकरी पानी से और खुरों को नीला थोथा घोल से धोएं। सुपाच्य दलिया व गुड़ दें।",
            vaccineDue = "FMD Vaccine (राष्‍ट्रीय पशुरोग नियंत्रण): Due in September & March (Biannual)."
        ),
        LivestockDisease(
            diseaseName = "Lumpy Skin Disease / LSD (लम्पी त्वचा रोग)",
            diseaseNameHi = "लम्पी त्वचा रोग (LSD)",
            animalType = "Cow / Buffalo",
            severity = "High (उच्च / गंभीर)",
            symptoms = "Nodules (2-5 cm) all over body, swollen lymph nodes, watery eyes, nasal discharge, and drop in milk.",
            symptomsHi = "पूरे शरीर की त्वचा पर 2-5 सेमी की कठोर गांठें, आंखों व नाक से पानी, बुखार एवं कमजोरी।",
            treatment = "Isolate infected animal. Apply Neem oil + Camphor (कपूर) on nodules. Feed Turmeric (50g) + Jaggery + Black pepper balls daily.",
            treatmentHi = "संक्रमित पशु को अलग रखें। गांठों पर नीम तेल व कपूर लगाएं। हल्दी 50 ग्राम + काली मिर्च + गुड़ का लड्डू रोज खिलाएं।",
            vaccineDue = "Goat Pox Vaccine (गोपॉक्स टीका): Annual single dose."
        ),
        LivestockDisease(
            diseaseName = "Bloat / Tympany (अफरा / पोट फुगणे)",
            diseaseNameHi = "अफरा (गैस बनना / पेट फूलना / पोट फुगणे)",
            animalType = "Cow / Buffalo / Goat",
            severity = "Urgent (तत्काल / तातडीने)",
            symptoms = "Distended left flank drum-tight, respiratory distress, restlessness, refusal to eat.",
            symptomsHi = "बाईं कोख का ढोल की तरह फूलना, सांस लेने में तकलीफ, बेचैनी और चारा न खाना।",
            treatment = "Drench with 500ml Mustard Oil + 50g Hing (Asafoetida) + 20g Turpentine oil. Use stomach tube or trocar cannula in extreme cases.",
            treatmentHi = "500 मिली सरसों का तेल + 50 ग्राम हींग + 20 ग्राम तारपीन का तेल पिलाएं। पशु को लगातार चलाएं।",
            vaccineDue = "Dietary management: Avoid sudden lush green legume feeding."
        )
    )

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
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "पशुवैद्यकीय व दुग्धसेवा मित्र"
                                AppLanguage.HINDI -> "पशु चिकित्सा एवं डेयरी मित्र"
                                else -> "Pashu Chikitsa & Dairy Care"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "पशु रोग निदान, लसीकरण व दुग्ध संवर्धन"
                                AppLanguage.HINDI -> "पशु रोग निदान, टीकाकरण व दुग्ध संवर्धन"
                                else -> "Livestock Disease Diagnosis & Vaccine Tracker"
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
                // Emergency Helpline 1962 Banner
                Surface(
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneInTalk,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHi) "24x7 पशु एम्बुलेंस व टोल-फ्री हेल्पलाइन" else "24x7 Pashu Ambulance Helpline",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "Dial 1962 (Toll Free / निःशुल्क)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animal Selector
                Text(
                    text = if (isHi) "पशु का प्रकार चुनें:" else "Select Livestock Animal:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Cow / गाय", "Buffalo / भैंस", "Goat / बकरी").forEach { anim ->
                        FilterChip(
                            selected = selectedAnimal == anim,
                            onClick = { selectedAnimal = anim },
                            label = { Text(anim, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Disease / Symptom List
                Text(
                    text = if (isHi) "लक्षण या रोग का चयन करें:" else "Select Observed Symptoms / Condition:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))

                diseases.forEach { d ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (diagnosedDisease?.diseaseName == d.diseaseName) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (diagnosedDisease?.diseaseName == d.diseaseName) Color(0xFF22C55E) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { diagnosedDisease = d }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = diagnosedDisease?.diseaseName == d.diseaseName,
                                onClick = { diagnosedDisease = d },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF16A34A))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (isHi) d.diseaseNameHi else d.diseaseName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Severity: ${d.severity}",
                                    fontSize = 10.sp,
                                    color = if (d.severity.contains("Critical") || d.severity.contains("गंभीर")) Color(0xFFDC2626) else Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }

                if (diagnosedDisease != null) {
                    val d = diagnosedDisease!!
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MedicalInformation,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHi) "उपचार एवं देसी नुस्खे:" else "Treatment & First Aid Care:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isHi) d.treatmentHi else d.treatment,
                                fontSize = 12.sp,
                                color = Color(0xFF14532D),
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFBBF7D0))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Vaccines,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHi) "टीकाकरण अनुसूची:" else "Vaccine Schedule:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = d.vaccineDue,
                                fontSize = 11.sp,
                                color = Color(0xFF0C4A6E)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
