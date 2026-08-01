package com.agriindia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.ui.components.AgriTopAppBar
import com.agriindia.app.ui.components.KisanMitraDialog
import com.agriindia.app.ui.components.YojnaCalculatorDialog
import com.agriindia.app.ui.screens.*
import com.agriindia.app.viewmodel.AgriViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AgriViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriIndiaAppContent(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriIndiaAppContent(viewModel: AgriViewModel) {
    val language by viewModel.language.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val showKisanMitra by viewModel.showKisanMitra.collectAsState()
    val showYojnaCalc by viewModel.showYojnaCalculator.collectAsState()
    val showCartSheet by viewModel.showCartSheet.collectAsState()

    val isHi = language == AppLanguage.HINDI

    val navItems = listOf(
        NavItem(if (isHi) "मौसम" else "Weather", Icons.Default.WbSunny),
        NavItem(if (isHi) "योजनाएं" else "Yojnas", Icons.Default.AccountBalance),
        NavItem(if (isHi) "मंडी भाव" else "Mandi", Icons.Default.Storefront),
        NavItem(if (isHi) "बाज़ार" else "Bazaar", Icons.Default.ShoppingCart),
        NavItem(if (isHi) "चौपाल" else "Chopal", Icons.Default.Groups),
        NavItem(if (isHi) "ज्ञान" else "Gyan", Icons.Default.MenuBook)
    )

    Scaffold(
        topBar = {
            AgriTopAppBar(
                currentLanguage = language,
                cartItemCount = cartItems.sumOf { it.quantity },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenCart = { viewModel.toggleCartSheet(true) },
                onOpenKisanMitra = { viewModel.toggleKisanMitra(true) }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF059669),
                            selectedTextColor = Color(0xFF059669),
                            indicatorColor = Color(0xFFDCFCE7)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> {
                    val weatherData by viewModel.weatherData.collectAsState()
                    val selectedLocation by viewModel.selectedLocation.collectAsState()
                    WeatherScreen(
                        language = language,
                        weatherData = weatherData,
                        locations = viewModel.locations,
                        selectedLocation = selectedLocation,
                        onSelectLocation = { viewModel.setWeatherLocation(it) }
                    )
                }
                1 -> {
                    val yojnas by viewModel.yojnas.collectAsState()
                    YojnaScreen(
                        language = language,
                        yojnas = yojnas,
                        onOpenCalculator = { viewModel.toggleYojnaCalculator(true) }
                    )
                }
                2 -> {
                    val mandiPrices by viewModel.mandiPrices.collectAsState()
                    val searchQuery by viewModel.mandiSearchQuery.collectAsState()
                    val selectedState by viewModel.mandiSelectedState.collectAsState()
                    MandiPricesScreen(
                        language = language,
                        mandiPrices = mandiPrices,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setMandiSearchQuery(it) },
                        selectedState = selectedState,
                        onSelectState = { viewModel.setMandiStateFilter(it) }
                    )
                }
                3 -> {
                    val products by viewModel.bazaarProducts.collectAsState()
                    val selectedCategory by viewModel.bazaarCategory.collectAsState()
                    BazaarScreen(
                        language = language,
                        products = products,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.setBazaarCategory(it) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onOpenSellProduce = { viewModel.toggleSellProduceDialog(true) }
                    )
                }
                4 -> {
                    val posts by viewModel.communityPosts.collectAsState()
                    CommunityScreen(
                        language = language,
                        posts = posts,
                        onToggleLike = { viewModel.togglePostLike(it) },
                        onAddPost = { content, category -> viewModel.addCommunityPost(content, category) }
                    )
                }
                5 -> {
                    val articles by viewModel.articles.collectAsState()
                    val selectedArticle by viewModel.selectedArticle.collectAsState()
                    ArticlesScreen(
                        language = language,
                        articles = articles,
                        selectedArticle = selectedArticle,
                        onSelectArticle = { viewModel.selectArticle(it) },
                        onToggleBookmark = { viewModel.toggleArticleBookmark(it) }
                    )
                }
            }
        }
    }

    // Dialogs & Sheets
    if (showKisanMitra) {
        KisanMitraDialog(
            language = language,
            onDismiss = { viewModel.toggleKisanMitra(false) }
        )
    }

    if (showYojnaCalc) {
        YojnaCalculatorDialog(
            language = language,
            onDismiss = { viewModel.toggleYojnaCalculator(false) }
        )
    }

    if (showCartSheet) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleCartSheet(false) },
            title = { Text(if (isHi) "शॉपिंग कार्ट" else "Shopping Cart") },
            text = {
                Column {
                    if (cartItems.isEmpty()) {
                        Text(if (isHi) "आपका कार्ट खाली है।" else "Your cart is empty.", fontSize = 13.sp)
                    } else {
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.product.name.take(20)}... x${item.quantity}", fontSize = 12.sp)
                                Text("₹${(item.product.price * item.quantity).toInt()}", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Total: ₹${cartItems.sumOf { it.product.price * it.quantity }.toInt()}",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF059669)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.toggleCartSheet(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Text(if (isHi) "ऑर्डर दें" else "Place Order")
                }
            }
        )
    }
}

data class NavItem(val label: String, val icon: ImageVector)
