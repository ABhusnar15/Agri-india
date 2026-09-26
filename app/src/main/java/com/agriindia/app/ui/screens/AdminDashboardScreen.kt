package com.agriindia.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    language: AppLanguage,
    mandiPrices: List<MandiPrice>,
    products: List<Product>,
    yojnas: List<YojnaScheme>,
    orders: List<Order>,
    communityPosts: List<CommunityPost>,
    onAddMandiPrice: (MandiPrice) -> Unit,
    onDeleteMandiPrice: (String) -> Unit,
    onAddProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onAddYojna: (YojnaScheme) -> Unit,
    onDeleteYojna: (String) -> Unit,
    onDeleteCommunityPost: (String) -> Unit,
    onUpdateOrderStatus: ((String, String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    var selectedAdminTab by remember { mutableIntStateOf(0) }
    var pinVerified by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    // Forms state
    var showAddMandiDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddYojnaDialog by remember { mutableStateOf(false) }

    if (!pinVerified) {
        // Admin Security PIN Dialog Modal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color(0xFFDCFCE7)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isMr) "प्रशासक प्रवेश (Admin Access)" else if (isHi) "प्रशासक पहुँच (Admin Access)" else "AgriIndia Admin Portal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = if (isHi) "कृपया प्रशासक पासवर्ड दर्ज करें (Admin: abhusnar15@g-mail.com | Pass: Pass@123)" else "Enter Admin Passcode for abhusnar15@g-mail.com (Pass: Pass@123)",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        label = { Text("Admin Password / PIN") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = pinError,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF059669))
                        }
                    )

                    if (pinError) {
                        Text(
                            text = if (isHi) "गलत पासवर्ड! (Use: Pass@123)" else "Incorrect Admin Password! (Use: Pass@123)",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isHi) "रद्द करें" else "Cancel", color = Color(0xFF64748B))
                        }

                        Button(
                            onClick = {
                                if (enteredPin == "Pass@123" || enteredPin == "abhusnar15@g-mail.com" || enteredPin == "1234" || enteredPin == "AGRI2026" || enteredPin == "admin") {
                                    pinVerified = true
                                } else {
                                    pinError = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                        ) {
                            Text(if (isHi) "सत्यापित करें" else "Verify Access")
                        }
                    }
                }
            }
        }
        return
    }

    // Main Admin Dashboard UI after PIN Verification
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = Color(0xFF064E3B)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHi) "कृषि-इंडिया एडमिन कंट्रोल" else "AgriIndia Admin Control Center",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Logged in as Super Admin • Live Cloud Status",
                                fontSize = 11.sp,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF047857),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34D399))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Horizontal Admin Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.White,
                contentColor = Color(0xFF059669),
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = Color(0xFFE2E8F0)) }
            ) {
                val tabs = listOf(
                    "Overview" to Icons.Default.Dashboard,
                    "Mandi Rates" to Icons.Default.TrendingUp,
                    "Agri-Bazaar" to Icons.Default.Storefront,
                    "Schemes (Yojna)" to Icons.Default.AccountBalance,
                    "Orders" to Icons.Default.Receipt,
                    "Moderation" to Icons.Default.Shield,
                    "Farmers CRM" to Icons.Default.People
                )
                tabs.forEachIndexed { index, (label, icon) ->
                    Tab(
                        selected = selectedAdminTab == index,
                        onClick = { selectedAdminTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(label, fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Medium)
                            }
                        }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedAdminTab) {
                    0 -> AdminOverviewTab(
                        mandiCount = mandiPrices.size,
                        productCount = products.size,
                        yojnaCount = yojnas.size,
                        orderCount = orders.size,
                        onNavigateTab = { selectedAdminTab = it }
                    )
                    1 -> AdminMandiTab(
                        mandiPrices = mandiPrices,
                        onOpenAddDialog = { showAddMandiDialog = true },
                        onDeleteMandiPrice = onDeleteMandiPrice
                    )
                    2 -> AdminBazaarTab(
                        products = products,
                        onOpenAddDialog = { showAddProductDialog = true },
                        onDeleteProduct = onDeleteProduct
                    )
                    3 -> AdminYojnaTab(
                        yojnas = yojnas,
                        onOpenAddDialog = { showAddYojnaDialog = true },
                        onDeleteYojna = onDeleteYojna
                    )
                    4 -> AdminOrdersTab(
                        orders = orders,
                        onUpdateOrderStatus = onUpdateOrderStatus
                    )
                    5 -> AdminModerationTab(
                        communityPosts = communityPosts,
                        onDeletePost = onDeleteCommunityPost
                    )
                    6 -> AdminFarmersTab()
                }
            }
        }
    }

    // Modal Dialogs for Creating Items
    if (showAddMandiDialog) {
        AddMandiDialog(
            onDismiss = { showAddMandiDialog = false },
            onAdd = { item ->
                onAddMandiPrice(item)
                showAddMandiDialog = false
            }
        )
    }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAdd = { prod ->
                onAddProduct(prod)
                showAddProductDialog = false
            }
        )
    }

    if (showAddYojnaDialog) {
        AddYojnaDialog(
            onDismiss = { showAddYojnaDialog = false },
            onAdd = { scheme ->
                onAddYojna(scheme)
                showAddYojnaDialog = false
            }
        )
    }
}

// ---- Admin Overview Tab ----

@Composable
private fun AdminOverviewTab(
    mandiCount: Int,
    productCount: Int,
    yojnaCount: Int,
    orderCount: Int,
    onNavigateTab: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF059669), Color(0xFF047857), Color(0xFF065F46))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "System Performance & Metrics Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Real-time monitoring of crop prices, farmer engagement, and digital bazaar orders across India.",
                        fontSize = 12.sp,
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Platform Statistics",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Stat Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Total Revenue",
                value = "₹4,28,500",
                subtitle = "+18% this month",
                icon = Icons.Default.Payments,
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Registered Farmers",
                value = "14,850+",
                subtitle = "Active across 12 States",
                icon = Icons.Default.Groups,
                color = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Mandi Records",
                value = "$mandiCount Live",
                subtitle = "Updated continuously",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(1) }
            )
            StatMetricCard(
                title = "Bazaar Products",
                value = "$productCount Items",
                subtitle = "Seeds, Fertilizer, Tools",
                icon = Icons.Default.Storefront,
                color = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(2) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Admin Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                QuickActionRow(
                    title = "Broadcast Mandi Price Update",
                    desc = "Publish new daily market commodity rates for mandis",
                    icon = Icons.Default.AddBusiness,
                    onClick = { onNavigateTab(1) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                QuickActionRow(
                    title = "Add Product to Agri-Bazaar Catalog",
                    desc = "List new seed varieties, organic pesticides or farm equipment",
                    icon = Icons.Default.AddShoppingCart,
                    onClick = { onNavigateTab(2) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                QuickActionRow(
                    title = "Publish Govt Scheme (Yojna)",
                    desc = "Add new central or state subsidy scheme with calculator parameters",
                    icon = Icons.Default.PostAdd,
                    onClick = { onNavigateTab(3) }
                )
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
            Text(text = subtitle, fontSize = 10.sp, color = color, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun QuickActionRow(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFFECFDF5),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(22.dp))
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            Text(text = desc, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
    }
}

// ---- Admin Mandi Tab ----

@Composable
private fun AdminMandiTab(
    mandiPrices: List<MandiPrice>,
    onOpenAddDialog: () -> Unit,
    onDeleteMandiPrice: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Mandi Price Intelligence", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("${mandiPrices.size} Mandi commodities listed", fontSize = 12.sp, color = Color(0xFF64748B))
            }

            Button(
                onClick = onOpenAddDialog,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Price")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            mandiPrices.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.commodity, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(item.state, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text("${item.mandiName}, ${item.district}", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text("Min: ₹${item.minPrice} | Max: ₹${item.maxPrice}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${item.modalPrice}/Q", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            IconButton(
                                onClick = { onDeleteMandiPrice(item.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---- Admin Bazaar Tab ----

@Composable
private fun AdminBazaarTab(
    products: List<Product>,
    onOpenAddDialog: () -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Agri-Bazaar Inventory", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("${products.size} products in catalog", fontSize = 12.sp, color = Color(0xFF64748B))
            }

            Button(
                onClick = onOpenAddDialog,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Product")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            products.forEach { prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("Category: ${prod.category} • Seller: ${prod.seller}", fontSize = 11.sp, color = Color(0xFF64748B))
                            if (prod.isSubsidyAvailable) {
                                Text(prod.subsidyTag, fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹${prod.price.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            IconButton(
                                onClick = { onDeleteProduct(prod.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---- Admin Yojna Tab ----

@Composable
private fun AdminYojnaTab(
    yojnas: List<YojnaScheme>,
    onOpenAddDialog: () -> Unit,
    onDeleteYojna: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Government Schemes (Yojna)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("${yojnas.size} active agricultural schemes", fontSize = 12.sp, color = Color(0xFF64748B))
            }

            Button(
                onClick = onOpenAddDialog,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Scheme")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            yojnas.forEach { scheme ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(scheme.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("Category: ${scheme.category} • Aid: ${scheme.subsidyAmount}", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                            Text(scheme.description, fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 2)
                        }

                        IconButton(
                            onClick = { onDeleteYojna(scheme.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ---- Admin Orders Tab ----

@Composable
private fun AdminOrdersTab(
    orders: List<Order>,
    onUpdateOrderStatus: ((String, String) -> Unit)? = null
) {
    var orderFilter by remember { mutableStateOf("All") }

    val filteredOrders = when (orderFilter) {
        "Paid" -> orders.filter { it.paymentStatus.contains("Paid", ignoreCase = true) || it.paymentStatus.contains("Success", ignoreCase = true) }
        "Dispatched" -> orders.filter { it.paymentStatus.contains("Dispatched", ignoreCase = true) }
        "Delivered" -> orders.filter { it.paymentStatus.contains("Delivered", ignoreCase = true) }
        else -> orders
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Bazaar Order Fulfillment & Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text("Monitor Razorpay & COD orders placed by farmers across India", fontSize = 12.sp, color = Color(0xFF64748B))

        Spacer(modifier = Modifier.height(12.dp))

        // Order status filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Paid", "Dispatched", "Delivered").forEach { filter ->
                FilterChip(
                    selected = orderFilter == filter,
                    onClick = { orderFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF059669),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredOrders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No orders match the selected filter.", color = Color(0xFF64748B), fontSize = 13.sp)
                    }
                }
            }
        } else {
            filteredOrders.forEach { order ->
                val statusColor = when {
                    order.paymentStatus.contains("Delivered", true) -> Color(0xFF059669)
                    order.paymentStatus.contains("Dispatched", true) -> Color(0xFF0284C7)
                    order.paymentStatus.contains("Paid", true) -> Color(0xFFD97706)
                    else -> Color(0xFF64748B)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(order.orderId, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                    Text("Ref ID: ${order.paymentId.take(18)}...", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                            }

                            Surface(
                                color = statusColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = order.paymentStatus.ifBlank { "Paid" },
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                        Text("Items Purchased:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                        Text(order.items.joinToString(", ").ifBlank { "High Quality Certified Seeds" }, fontSize = 12.sp, color = Color(0xFF1E293B))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF059669), fontSize = 16.sp)

                            // Admin Action Status Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { onUpdateOrderStatus?.invoke(order.orderId, "Dispatched") },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Dispatch", fontSize = 10.sp, color = Color(0xFF0284C7))
                                }

                                Button(
                                    onClick = { onUpdateOrderStatus?.invoke(order.orderId, "Delivered") },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                ) {
                                    Text("Mark Delivered", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---- Admin Farmers CRM Tab ----

@Composable
private fun AdminFarmersTab() {
    var searchQuery by remember { mutableStateOf("") }

    val sampleFarmers = remember {
        listOf(
            Triple("Ramesh Patil", "Maharashtra", "9823012345 to 5.5 Acres"),
            Triple("Gurpreet Singh", "Punjab", "9814098765 to 12.0 Acres"),
            Triple("Subhash Sharma", "Uttar Pradesh", "9415054321 to 3.2 Acres"),
            Triple("Vikram Rathore", "Rajasthan", "9829033445 to 8.0 Acres"),
            Triple("Aniket Deshmukh", "Maharashtra", "9766011223 to 4.0 Acres")
        )
    }

    val filtered = sampleFarmers.filter {
        it.first.contains(searchQuery, true) || it.second.contains(searchQuery, true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Registered Farmer CRM & Demographics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text("Manage farmer directory, state distribution & verified Krishi accounts", fontSize = 12.sp, color = Color(0xFF64748B))

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search farmer by name or state...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF059669)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        filtered.forEach { (name, state, details) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Color(0xFFDCFCE7)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(name.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF059669), fontSize = 18.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified Farmer", tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                            }
                            Text("State: $state • Land: $details", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}


// ---- Admin Moderation Tab ----

@Composable
private fun AdminModerationTab(
    communityPosts: List<CommunityPost>,
    onDeletePost: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Community Moderation Queue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text("Review farmer posts, remove spam, or feature expert posts", fontSize = 12.sp, color = Color(0xFF64748B))

        Spacer(modifier = Modifier.height(16.dp))

        communityPosts.forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("(${post.authorState})", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text(post.content, fontSize = 12.sp, color = Color(0xFF334155), maxLines = 2)
                    }

                    IconButton(onClick = { onDeletePost(post.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Post", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

// ---- Dialogs for Adding Content ----

@Composable
private fun AddMandiDialog(
    onDismiss: () -> Unit,
    onAdd: (MandiPrice) -> Unit
) {
    var commodity by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("Maharashtra") }
    var mandiName by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var modalPrice by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Mandi Commodity Rate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = commodity, onValueChange = { commodity = it }, label = { Text("Commodity Name (e.g. Wheat)") })
                OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") })
                OutlinedTextField(value = mandiName, onValueChange = { mandiName = it }, label = { Text("Mandi Market Name") })
                OutlinedTextField(value = minPrice, onValueChange = { minPrice = it }, label = { Text("Min Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = maxPrice, onValueChange = { maxPrice = it }, label = { Text("Max Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = modalPrice, onValueChange = { modalPrice = it }, label = { Text("Modal Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (commodity.isNotBlank() && modalPrice.isNotBlank()) {
                        val modal = modalPrice.toIntOrNull() ?: 2200
                        val item = MandiPrice(
                            id = "mandi_${System.currentTimeMillis()}",
                            commodity = commodity,
                            commodityHi = commodity,
                            state = state,
                            district = mandiName,
                            mandiName = mandiName.ifBlank { "Central Mandi" },
                            minPrice = minPrice.toIntOrNull() ?: (modal - 200),
                            maxPrice = maxPrice.toIntOrNull() ?: (modal + 200),
                            modalPrice = modal,
                            priceChange = 15,
                            lastUpdated = "Admin updated"
                        )
                        onAdd(item)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text("Add Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Seeds") }
    var price by remember { mutableStateOf("") }
    var seller by remember { mutableStateOf("AgriIndia Certified") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Agri-Bazaar Product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Seeds, Fertilizer, Tools)") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = seller, onValueChange = { seller = it }, label = { Text("Seller / Brand") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank()) {
                        val pVal = price.toDoubleOrNull() ?: 499.0
                        val prod = Product(
                            id = "prod_${System.currentTimeMillis()}",
                            name = name,
                            nameHi = name,
                            category = category,
                            price = pVal,
                            originalPrice = pVal * 1.25,
                            rating = 4.8,
                            reviewCount = 12,
                            isSubsidyAvailable = true,
                            subsidyTag = "20% Govt Subsidy Applicable",
                            seller = seller,
                            description = "High quality agricultural input certified for optimal crop yield."
                        )
                        onAdd(prod)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text("Save Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AddYojnaDialog(
    onDismiss: () -> Unit,
    onAdd: (YojnaScheme) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Subsidy") }
    var subsidyAmount by remember { mutableStateOf("50% Aid") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Government Scheme") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Scheme Name (Yojna Title)") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                OutlinedTextField(value = subsidyAmount, onValueChange = { subsidyAmount = it }, label = { Text("Subsidy / Benefit Amount") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val scheme = YojnaScheme(
                            id = "yojna_${System.currentTimeMillis()}",
                            title = title,
                            titleHi = title,
                            category = category,
                            subsidyAmount = subsidyAmount,
                            description = description,
                            descriptionHi = description,
                            eligibility = "Small and Marginal Farmers with land ownership documents.",
                            eligibilityHi = "सीमांत किसान जिनके पास भूमि दस्तावेज़ हैं।",
                            documents = listOf("Aadhaar Card", "7/12 Uttara Land Document", "Bank Passbook"),
                            officialLink = "https://pmkisan.gov.in",
                            isCentral = true
                        )
                        onAdd(scheme)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text("Publish Scheme")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
