package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onSelectState: (String) -> Unit
) {
    val isHi = language == AppLanguage.HINDI

    val filteredPrices = mandiPrices.filter { item ->
        (searchQuery.isBlank() || item.commodity.contains(searchQuery, ignoreCase = true) || item.mandiName.contains(searchQuery, ignoreCase = true)) &&
        (selectedState == "All States" || item.state.equals(selectedState, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(if (isHi) "फसल या मंडी का नाम खोजें..." else "Search crop or Mandi name...", fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF059669)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF059669))
        )

        Spacer(modifier = Modifier.height(12.dp))

        // State Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("All States", "Punjab", "Maharashtra", "Karnataka", "Gujarat", "Rajasthan").forEach { state ->
                FilterChip(
                    selected = selectedState == state,
                    onClick = { onSelectState(state) },
                    label = { Text(state, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF059669),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isHi) "दैनिक मंडी भाव अपडेट्स (${filteredPrices.size})" else "Daily APMC Mandi Price Updates (${filteredPrices.size})",
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
                MandiPriceCard(mandi = mandi, isHi = isHi)
            }
        }
    }
}

@Composable
fun MandiPriceCard(mandi: MandiPrice, isHi: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
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
                            text = "${mandi.mandiName}, ${mandi.state}",
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
        }
    }
}
