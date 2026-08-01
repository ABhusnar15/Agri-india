package com.agriindia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.WeatherData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    language: AppLanguage,
    weatherData: WeatherData,
    locations: List<String>,
    selectedLocation: String,
    onSelectLocation: (String) -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    var expandedDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Location Selector Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF059669)
                )
                Spacer(modifier = Modifier.width(6.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    Text(
                        text = selectedLocation,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        locations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    onSelectLocation(loc)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            AssistChip(
                onClick = {},
                label = { Text("Live GPS", fontSize = 10.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF059669)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Weather Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF059669)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "${weatherData.temperature}°C",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = weatherData.condition,
                            fontSize = 16.sp,
                            color = Color(0xFFD1FAE5)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Agro Advisory Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF047857)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isHi) "कृषि सलाह" else "Agro Advisory",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )
                            Text(
                                text = if (isHi) weatherData.advisoryHi else weatherData.advisory,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WeatherMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isHi) "आर्द्रता" else "Humidity",
                value = "${weatherData.humidity}%",
                icon = Icons.Default.WaterDrop,
                tint = Color(0xFF0284C7)
            )
            WeatherMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isHi) "वर्षा संभावना" else "Rain Risk",
                value = "${weatherData.rainProbability}%",
                icon = Icons.Default.Umbrella,
                tint = Color(0xFF2563EB)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WeatherMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isHi) "मृदा नमी" else "Soil Moisture",
                value = "${weatherData.soilMoisture}%",
                icon = Icons.Default.Grass,
                tint = Color(0xFF059669)
            )
            WeatherMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isHi) "हवा की गति" else "Wind Speed",
                value = "${weatherData.windSpeed} km/h",
                icon = Icons.Default.Air,
                tint = Color(0xFFD97706)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 7-Day Forecast Section
        Text(
            text = if (isHi) "7-दिवसीय मौसम पूर्वानुमान" else "7-Day Weather Forecast",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(weatherData.weeklyForecast) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.width(90.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHi) item.dayHi else item.day,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Icon(
                            imageVector = if (item.rainChance > 50) Icons.Default.Thunderstorm else Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = if (item.rainChance > 50) Color(0xFF2563EB) else Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${item.tempMax}° / ${item.tempMin}°",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${item.rainChance}% rain",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherMetricTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 11.sp, color = Color(0xFF64748B))
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
        }
    }
}
