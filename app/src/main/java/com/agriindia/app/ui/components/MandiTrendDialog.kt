package com.agriindia.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.MandiPrice
import com.agriindia.app.model.MandiPriceHistoryPoint
import com.agriindia.app.model.MandiTimeframe

@Composable
fun MandiTrendDialog(
    language: AppLanguage,
    item: MandiPrice,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    // Timeframe filter state
    var selectedTimeframe by remember { mutableStateOf(MandiTimeframe.THIRTY_DAYS) }
    var selectedPointIndex by remember { mutableIntStateOf(-1) }
    var showVolumeOverlay by remember { mutableStateOf(true) }

    val rawHistory = remember(item) {
        if (item.history30Days.isNotEmpty()) item.history30Days else {
            // Generate 30 days fallback if empty
            (0 until 30).map { i ->
                val base = item.modalPrice - 120 + (i * 8)
                MandiPriceHistoryPoint(
                    dayIndex = i + 1,
                    dateLabel = "Day ${i + 1}",
                    price = base + (if (i % 2 == 0) 25 else -15),
                    minPrice = base - 50,
                    maxPrice = base + 60,
                    arrivalVolumeTons = 300.0 + (i * 12)
                )
            }
        }
    }

    val filteredPoints = remember(selectedTimeframe, rawHistory) {
        rawHistory.takeLast(selectedTimeframe.days)
    }

    val prices = filteredPoints.map { it.price }
    val highestPrice = prices.maxOrNull() ?: item.modalPrice
    val lowestPrice = prices.minOrNull() ?: item.modalPrice
    val averagePrice = if (prices.isNotEmpty()) prices.average().toInt() else item.modalPrice
    val isBullish = item.priceChange >= 0

    val activePoint = if (selectedPointIndex in filteredPoints.indices) {
        filteredPoints[selectedPointIndex]
    } else {
        filteredPoints.lastOrNull()
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
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "${item.commodityHi} - बाजार भाव विश्लेषण"
                                AppLanguage.HINDI -> "${item.commodityHi} - मंडी भाव रुझान"
                                else -> "${item.commodity} - Mandi Trend Analysis"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${item.mandiName}, ${item.state} (${selectedTimeframe.label})",
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
                // Interactive Timeframe Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MandiTimeframe.values().forEach { tf ->
                        val isSelected = selectedTimeframe == tf
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF059669) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedTimeframe = tf
                                    selectedPointIndex = -1
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> tf.labelMr
                                        AppLanguage.HINDI -> tf.labelHi
                                        else -> tf.label
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active Point Inspection Callout
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (activePoint != null) "${activePoint.dateLabel} Rate" else "Live APMC Rate",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "₹${activePoint?.price ?: item.modalPrice}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "/ ${item.unit}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isBullish) Color(0xFF065F46) else Color(0xFF7F1D1D)
                            ) {
                                Text(
                                    text = if (isBullish) "▲ +₹${item.priceChange}" else "▼ ₹${item.priceChange}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBullish) Color(0xFF6EE7B7) else Color(0xFFFCA5A5),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Vol: ${activePoint?.arrivalVolumeTons?.toInt() ?: item.arrivalVolumeToday.toInt()} MT",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Canvas Interactive Chart
                Text(
                    text = if (isMr) "किंमत कल आलेख (टॅप करून तपासा):" else if (isHi) "मूल्य ग्राफ (स्पर्श करके विवरण देखें):" else "Price Trend Graph (Tap/Drag to Inspect):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        MandiTrendCanvasGraph(
                            points = filteredPoints,
                            selectedIndex = selectedPointIndex,
                            showVolume = showVolumeOverlay,
                            onSelectIndex = { selectedPointIndex = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 30-Day Metrics Summary Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricStatBox(
                        modifier = Modifier.weight(1f),
                        title = if (isMr) "कमाल भाव" else if (isHi) "उच्चतम भाव" else "High Rate",
                        value = "₹$highestPrice",
                        tint = Color(0xFF059669)
                    )
                    MetricStatBox(
                        modifier = Modifier.weight(1f),
                        title = if (isMr) "किमान भाव" else if (isHi) "न्यूनतम भाव" else "Low Rate",
                        value = "₹$lowestPrice",
                        tint = Color(0xFFDC2626)
                    )
                    MetricStatBox(
                        modifier = Modifier.weight(1f),
                        title = if (isMr) "सरासरी भाव" else if (isHi) "औसत भाव" else "Average",
                        value = "₹$averagePrice",
                        tint = Color(0xFF0284C7)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // MSP Comparison & Market Momentum Pill
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isMr) "हमीभाव (MSP) तुलना:" else if (isHi) "न्यूनतम समर्थन मूल्य (MSP) तुलना:" else "Govt MSP Benchmark:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            val diff = item.modalPrice - item.mspPrice
                            Text(
                                text = if (diff >= 0) {
                                    if (isMr) "सध्याचा भाव हमीभावापेक्षा +₹$diff/क्विंटल जास्त आहे." else if (isHi) "वर्तमान भाव MSP से +₹$diff/क्विंटल अधिक है।" else "Trading +₹$diff/Qtl above Govt MSP (₹${item.mspPrice})"
                                } else {
                                    if (isMr) "सध्याचा भाव हमीभावापेक्षा ₹${-diff} कमी आहे." else if (isHi) "वर्तमान भाव MSP से ₹${-diff} कम है।" else "Trading ₹${-diff}/Qtl below Govt MSP (₹${item.mspPrice})"
                                },
                                fontSize = 11.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Trading Advisory Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isMr) "बाजार तज्ज्ञ सल्ला (Market Advisory)" else if (isHi) "बाजार विशेषज्ञ सलाह (Market Advisory)" else "Market Expert Advisory",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = if (isBullish) {
                                    if (isMr) "आवक मध्यम असून मागणी वाढती आहे. पुढील ३-५ दिवसांत शेतमाल विकणे फायद्याचे ठरेल."
                                    else if (isHi) "आवक स्थिर है व मांग बढ़ रही है। अगले 3-5 दिनों में फसल बिक्री करना लाभकारी होगा।"
                                    else "Steady demand from processors & mills. Favorable selling window over the next 3-5 days."
                                } else {
                                    if (isMr) "आवक जास्त असल्याने दरात अल्पशी घट आहे. योग्य प्रतवारी करून साठवणूक करावी."
                                    else if (isHi) "भारी आवक के कारण कीमतों में हल्का दबाव है। ग्रेडिंग करके माल रोकें।"
                                    else "High arrival pressure. Consider graded storage or selling in tranches."
                                },
                                fontSize = 11.sp,
                                color = Color(0xFF047857),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(if (isMr) "पूर्ण झाले" else if (isHi) "ठीक है" else "Close")
            }
        }
    )
}

@Composable
fun MandiTrendCanvasGraph(
    points: List<MandiPriceHistoryPoint>,
    selectedIndex: Int,
    showVolume: Boolean,
    onSelectIndex: (Int) -> Unit
) {
    if (points.isEmpty()) return

    val prices = points.map { it.price }
    val minVal = (prices.minOrNull() ?: 1000) * 0.96f
    val maxVal = (prices.maxOrNull() ?: 3000) * 1.04f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    val volumes = points.map { it.arrivalVolumeTons }
    val maxVol = (volumes.maxOrNull() ?: 100.0).toFloat().coerceAtLeast(1f)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(points) {
                detectTapGestures { offset ->
                    val width = size.width
                    val count = points.size
                    if (count > 1) {
                        val index = ((offset.x / width) * count).toInt().coerceIn(0, count - 1)
                        onSelectIndex(index)
                    }
                }
            }
            .pointerInput(points) {
                detectDragGestures { change, _ ->
                    val width = size.width
                    val count = points.size
                    if (count > 1) {
                        val index = ((change.position.x / width) * count).toInt().coerceIn(0, count - 1)
                        onSelectIndex(index)
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val count = points.size
        val stepX = w / (count - 1).coerceAtLeast(1)

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = h * (i.toFloat() / gridLines)
            drawLine(
                color = Color(0xFFF1F5F9),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw Volume Bars in background
        if (showVolume) {
            val barWidth = (stepX * 0.55f).coerceAtLeast(3f)
            points.forEachIndexed { i, pt ->
                val x = i * stepX
                val barH = (pt.arrivalVolumeTons.toFloat() / maxVol) * (h * 0.35f)
                drawRect(
                    color = Color(0x3338BDF8),
                    topLeft = Offset(x - barWidth / 2, h - barH),
                    size = androidx.compose.ui.geometry.Size(barWidth, barH)
                )
            }
        }

        // Build Price Curve Path
        val path = Path()
        val fillPath = Path()

        val coordinates = points.mapIndexed { i, pt ->
            val x = i * stepX
            val normY = (pt.price - minVal) / range
            val y = h - (normY * (h * 0.85f)) - (h * 0.08f)
            Offset(x, y)
        }

        if (coordinates.isNotEmpty()) {
            path.moveTo(coordinates[0].x, coordinates[0].y)
            fillPath.moveTo(coordinates[0].x, h)
            fillPath.lineTo(coordinates[0].x, coordinates[0].y)

            for (i in 0 until coordinates.size - 1) {
                val p0 = coordinates[i]
                val p1 = coordinates[i + 1]
                val midX = (p0.x + p1.x) / 2f
                path.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                fillPath.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }

            fillPath.lineTo(coordinates.last().x, h)
            fillPath.close()

            // Fill gradient under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x6610B981), Color(0x0510B981))
                )
            )

            // Draw line stroke
            drawPath(
                path = path,
                color = Color(0xFF059669),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Draw Interactive Indicator if point selected
        val activeI = if (selectedIndex in coordinates.indices) selectedIndex else coordinates.size - 1
        if (activeI in coordinates.indices) {
            val ptOffset = coordinates[activeI]

            // Vertical dashed line
            drawLine(
                color = Color(0xFF059669),
                start = Offset(ptOffset.x, 0f),
                end = Offset(ptOffset.x, h),
                strokeWidth = 1.2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Outer glow ring
            drawCircle(
                color = Color(0x3310B981),
                radius = 9.dp.toPx(),
                center = ptOffset
            )

            // Center solid dot
            drawCircle(
                color = Color(0xFF059669),
                radius = 5.dp.toPx(),
                center = ptOffset
            )

            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = ptOffset
            )
        }
    }
}

@Composable
fun MetricStatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    tint: Color
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = tint)
        }
    }
}
