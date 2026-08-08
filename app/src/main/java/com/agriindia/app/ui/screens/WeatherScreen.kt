package com.agriindia.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.HourlyForecast
import com.agriindia.app.model.WeatherData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    language: AppLanguage,
    weatherData: WeatherData,
    locations: List<String>,
    selectedLocation: String,
    isFetchingGps: Boolean = false,
    isRefreshing: Boolean = false,
    onSelectLocation: (String) -> Unit,
    onFetchGpsLocation: () -> Unit = {},
    onRefreshWeather: () -> Unit = {}
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI
    var expandedDropdown by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "refreshAnim")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onFetchGpsLocation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Location Selector & Live Dynamic Tools Header
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

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                // Live Satellite Refresh Button
                IconButton(
                    onClick = onRefreshWeather,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF059669),
                        modifier = if (isRefreshing) Modifier.rotate(rotationAngle) else Modifier
                    )
                }

                // Live GPS Chip
                AssistChip(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    label = {
                        Text(
                            if (isFetchingGps) "GPS..." else if (isMr) "थेट GPS" else if (isHi) "लाइव GPS" else "Live GPS",
                            fontSize = 10.sp
                        )
                    },
                    leadingIcon = {
                        if (isFetchingGps) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFF059669),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color(0xFF059669)
                            )
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFECFDF5))
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Weather Card with Atmospheric Status
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF059669)),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${weatherData.temperature}°",
                                fontSize = 52.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "C",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD1FAE5),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Text(
                            text = weatherData.condition,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD1FAE5)
                        )
                        Text(
                            text = if (isMr) "भासमान: ${weatherData.temperature + 2}°C • ढगाळ: ${weatherData.cloudCoverPercent}%"
                            else if (isHi) "अनुभूत तापमान: ${weatherData.temperature + 2}°C • बादल: ${weatherData.cloudCoverPercent}%"
                            else "Feels Like: ${weatherData.temperature + 2}°C • Clouds: ${weatherData.cloudCoverPercent}%",
                            fontSize = 11.sp,
                            color = Color(0xFFA7F3D0)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0x33FFFFFF),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (weatherData.rainProbability > 50) Icons.Default.Thunderstorm else Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = if (weatherData.rainProbability > 50) Color(0xFF93C5FD) else Color(0xFFFDE047),
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
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
                                text = if (isMr) "शेतकरी हवामान सल्ला" else if (isHi) "कृषि मौसम सलाह" else "Agro Weather Advisory",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )
                            Text(
                                text = if (isMr || isHi) weatherData.advisoryHi else weatherData.advisory,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 24-Hour Hourly Forecast Horizontal Timeline
        Text(
            text = if (isMr) "२४-तास हवामान अंदाज (तासनिहाय):" else if (isHi) "24-घंटे का प्रति घंटा पूर्वानुमान:" else "24-Hour Hourly Weather Forecast:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(weatherData.hourlyForecast) { item ->
                HourlyForecastCard(item = item, isHi = isHi, isMr = isMr)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Agro Spray Window Advisory Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (weatherData.sprayAdvisory.isOptimal) Color(0xFFECFDF5) else Color(0xFFFEF3C7)
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (weatherData.sprayAdvisory.isOptimal) Color(0xFFA7F3D0) else Color(0xFFFDE68A)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (weatherData.sprayAdvisory.isOptimal) Color(0xFF10B981) else Color(0xFFD97706),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (weatherData.sprayAdvisory.isOptimal) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isMr) "अनुकूल फवारणी वेळ: ${weatherData.sprayAdvisory.windowTextMr}"
                        else if (isHi) "सर्वोत्तम छिड़काव समय: ${weatherData.sprayAdvisory.windowTextHi}"
                        else "Optimal Foliar Spray Window: ${weatherData.sprayAdvisory.windowText}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (weatherData.sprayAdvisory.isOptimal) Color(0xFF065F46) else Color(0xFF92400E)
                    )
                    Text(
                        text = if (isMr) weatherData.sprayAdvisory.reasonMr
                        else if (isHi) weatherData.sprayAdvisory.reasonHi
                        else weatherData.sprayAdvisory.reason,
                        fontSize = 11.sp,
                        color = if (weatherData.sprayAdvisory.isOptimal) Color(0xFF047857) else Color(0xFF78350F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // In-Depth Agronomy & Micro-Climate Metrics
        Text(
            text = if (isMr) "माती व सूक्ष्म-हवामान स्थिती:" else if (isHi) "मृदा व सूक्ष्म-मौसम स्थिति:" else "Soil & Micro-Climate Agronomy:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailedMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isMr) "मातीचे तापमान (१०cm)" else if (isHi) "मृदा तापमान (10cm)" else "Soil Temp (10cm)",
                value = "${weatherData.soilAgronomy.soilTemp10cm}°C",
                subText = if (isMr) "सुयोग्य मुळांची वाढ" else if (isHi) "जड़ विकास अनुकूल" else "Root Zone Optimal",
                icon = Icons.Default.Thermostat,
                tint = Color(0xFFD97706)
            )
            DetailedMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isMr) "मातीतील ओलावा" else if (isHi) "मृदा नमी" else "Soil Moisture",
                value = "${weatherData.soilMoisture}%",
                subText = if (isMr) "सिंचन आवश्यक नाही" else if (isHi) "सिंचाई की आवश्यकता नहीं" else "Sufficient Reserve",
                icon = Icons.Default.Grass,
                tint = Color(0xFF059669)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailedMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isMr) "हवेतील आर्द्रता" else if (isHi) "सापेक्ष आर्द्रता" else "Humidity & Dew",
                value = "${weatherData.humidity}%",
                subText = "Dew Point: ${weatherData.soilAgronomy.dewPointC}°C",
                icon = Icons.Default.WaterDrop,
                tint = Color(0xFF0284C7)
            )
            DetailedMetricTile(
                modifier = Modifier.weight(1f),
                title = if (isMr) "वाऱ्याचा वेग" else if (isHi) "हवा की गति" else "Wind & Pressure",
                value = "${weatherData.windSpeed} km/h",
                subText = "${weatherData.pressureHpa} hPa (Stable)",
                icon = Icons.Default.Air,
                tint = Color(0xFF6366F1)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Air Quality & Solar Astronomy Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Air Quality AQI Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isMr) "हवेची गुणवत्ता" else if (isHi) "वायु गुणवत्ता" else "Air Quality (AQI)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(weatherData.airQuality.statusColorHex)
                        ) {
                            Text(
                                text = if (isMr) weatherData.airQuality.categoryMr else if (isHi) weatherData.airQuality.categoryHi else weatherData.airQuality.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${weatherData.airQuality.aqi} AQI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "PM2.5: ${weatherData.airQuality.pm25} • PM10: ${weatherData.airQuality.pm10}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Sunrise & UV Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isMr) "सूर्य वेळ व UV इंडेक्स" else if (isHi) "सूर्योदय व UV इंडेक्स" else "Sun & UV Index",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "UV ${weatherData.sunAstronomy.uvIndex} (${weatherData.sunAstronomy.uvCategory})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "🌅 ${weatherData.sunAstronomy.sunrise} • 🌇 ${weatherData.sunAstronomy.sunset}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 7-Day Forecast Section
        Text(
            text = if (isMr) "७-दिवसीय हवामान अंदाज:" else if (isHi) "7-दिवसीय मौसम पूर्वानुमान:" else "7-Day Weather Forecast:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(weatherData.weeklyForecast) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.width(96.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isMr || isHi) item.dayHi else item.day,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Icon(
                            imageVector = if (item.rainChance > 50) Icons.Default.Thunderstorm else Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = if (item.rainChance > 50) Color(0xFF2563EB) else Color(0xFFD97706),
                            modifier = Modifier.size(26.dp)
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
fun HourlyForecastCard(
    item: HourlyForecast,
    isHi: Boolean,
    isMr: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(82.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isMr || isHi) item.hourHi else item.hour,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
                imageVector = if (item.rainChance > 40) Icons.Default.WaterDrop else Icons.Default.WbSunny,
                contentDescription = null,
                tint = if (item.rainChance > 40) Color(0xFF0284C7) else Color(0xFFF59E0B),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${item.temp}°C",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "${item.rainChance}%",
                fontSize = 10.sp,
                color = if (item.rainChance > 40) Color(0xFF0284C7) else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun DetailedMetricTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subText: String,
    icon: ImageVector,
    tint: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = tint.copy(alpha = 0.12f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(text = subText, fontSize = 9.sp, color = tint, fontWeight = FontWeight.Medium)
            }
        }
    }
}
