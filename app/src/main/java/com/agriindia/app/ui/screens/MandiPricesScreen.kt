package com.agriindia.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.agriindia.app.model.MandiPrice

@Composable
fun MandiPricesScreen(
    language: AppLanguage,
    mandiPrices: List<MandiPrice>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedState: String,
    onSelectState: (String) -> Unit,
    onOpenTrend: (MandiPrice) -> Unit = {},
    onTriggerLiveUpdate: () -> Unit = {}
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    val filteredPrices = mandiPrices.filter { item ->
        (searchQuery.isBlank() || item.commodity.contains(searchQuery, ignoreCase = true) || item.commodityHi.contains(searchQuery, ignoreCase = true) || item.mandiName.contains(searchQuery, ignoreCase = true)) &&
        (selectedState == "All States" || item.state.equals(selectedState, ignoreCase = true))
    }

    val topGainer = mandiPrices.maxByOrNull { it.priceChange }
    val topVolume = mandiPrices.maxByOrNull { it.arrivalVolumeToday }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Live Market Ticker & Refresh Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF10B981),
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "थेट बाजार भाव • आवक चालू • Tomato +₹120 • Wheat +₹35"
                            AppLanguage.HINDI -> "लाइव मंडी भाव • आवक सक्रिय • Tomato +₹120 • Wheat +₹35"
                            else -> "Live APMC Ticker • Active Arrivals • Tomato +₹120 • Wheat +₹35"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA7F3D0),
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onTriggerLiveUpdate,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh live rates",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    when (language) {
                        AppLanguage.MARATHI -> "पीक किंवा बाजार समितीचे नाव शोधा..."
                        AppLanguage.HINDI -> "फसल या मंडी का नाम खोजें..."
                        else -> "Search crop or Mandi name..."
                    },
                    fontSize = 13.sp
                )
            },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF059669)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF059669))
        )

        Spacer(modifier = Modifier.height(10.dp))

        // State Filter Chips (Horizontally Scrollable)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            listOf("All States", "Maharashtra", "Punjab", "Karnataka", "Gujarat", "Rajasthan", "Madhya Pradesh").forEach { state ->
                FilterChip(
                    selected = selectedState == state,
                    onClick = { onSelectState(state) },
                    label = {
                        Text(
                            if (state == "All States" && isMr) "सर्व राज्ये"
                            else if (state == "All States" && isHi) "सभी राज्य"
                            else state,
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF059669),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Stats Row (Top Gainer & High Volume)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topGainer?.let { gainer ->
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.weight(1f).clickable { onOpenTrend(gainer) }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(text = if (isMr) "सर्वाधिक तेजी" else if (isHi) "टॉप तेजी" else "Top Gainer", fontSize = 9.sp, color = Color(0xFF047857))
                            Text(text = "${gainer.commodity} +₹${gainer.priceChange}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        }
                    }
                }
            }

            topVolume?.let { vol ->
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.weight(1f).clickable { onOpenTrend(vol) }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(text = if (isMr) "उच्च आवक" else if (isHi) "भारी आवक" else "High Volume", fontSize = 9.sp, color = Color(0xFF1E40AF))
                            Text(text = "${vol.commodity} ${vol.arrivalVolumeToday.toInt()}T", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = when (language) {
                AppLanguage.MARATHI -> "दैनिक कृषी उत्पन्न बाजार समिती भाव (${filteredPrices.size})"
                AppLanguage.HINDI -> "दैनिक मंडी भाव अपडेट्स (${filteredPrices.size})"
                else -> "Daily APMC Mandi Price Updates (${filteredPrices.size})"
            },
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredPrices) { mandi ->
                MandiPriceCard(mandi = mandi, isHi = isHi || isMr, onOpenTrend = { onOpenTrend(mandi) })
            }
        }
    }
}

@Composable
fun MandiPriceCard(
    mandi: MandiPrice,
    isHi: Boolean,
    onOpenTrend: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenTrend() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isHi) mandi.commodityHi else mandi.commodity,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${mandi.mandiName}, ${mandi.state} • ${mandi.lastUpdated}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Price change badge
                Surface(
                    color = if (mandi.priceChange >= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (mandi.priceChange >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (mandi.priceChange >= 0) Color(0xFF166534) else Color(0xFF991B1B),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "₹${kotlin.math.abs(mandi.priceChange)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mandi.priceChange >= 0) Color(0xFF166534) else Color(0xFF991B1B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rates Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (isHi) "न्यूनतम भाव" else "Min Price", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(text = "₹${mandi.minPrice}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (isHi) "मॉडल भाव" else "Modal Rate", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                    Text(text = "₹${mandi.modalPrice}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (isHi) "अधिकतम भाव" else "Max Price", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(text = "₹${mandi.maxPrice}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 30-Day Trend Action Button with Chart Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MSP: ₹${mandi.mspPrice} • ${mandi.marketSentiment}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                TextButton(onClick = onOpenTrend) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHi) "30-दिन भाव ग्राफ" else "30-Day Chart",
                        fontSize = 11.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
