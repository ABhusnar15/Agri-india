package com.agriindia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage
import com.agriindia.app.model.AuthState
import com.agriindia.app.model.PaymentResult
import com.agriindia.app.ui.components.AgriTopAppBar
import com.agriindia.app.ui.components.KisanMitraDialog
import com.agriindia.app.ui.components.YojnaCalculatorDialog
import com.agriindia.app.ui.screens.*
import com.agriindia.app.viewmodel.AgriViewModel
import com.agriindia.app.viewmodel.AuthViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {

    private val viewModel: AgriViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pre-fetch Razorpay payment methods
        Checkout.preload(applicationContext)

        setContent {
            AgriIndiaApp(
                viewModel = viewModel,
                authViewModel = authViewModel
            )
        }
    }

    // Razorpay payment callbacks
    override fun onPaymentSuccess(paymentId: String?) {
        val userId = authViewModel.currentUser.value?.uid ?: ""
        viewModel.onPaymentSuccess(paymentId ?: "", userId)
    }

    override fun onPaymentError(code: Int, message: String?) {
        viewModel.onPaymentError(code, message ?: "Payment failed")
    }
}

@Composable
fun AgriIndiaApp(
    viewModel: AgriViewModel,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            // Splash/Loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF059669))
            }
        }
        is AuthState.Authenticated -> {
            AgriIndiaAppContent(viewModel = viewModel, authViewModel = authViewModel)
        }
        is AuthState.Unauthenticated, is AuthState.Error -> {
            AuthScreens(authViewModel = authViewModel)
        }
    }
}

@Composable
fun AuthScreens(authViewModel: AuthViewModel) {
    val showSignUp by authViewModel.showSignUp.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val resetPasswordSent by authViewModel.resetPasswordSent.collectAsState()

    // Reset password dialog
    if (resetPasswordSent) {
        AlertDialog(
            onDismissRequest = { authViewModel.dismissResetPasswordDialog() },
            title = { Text("Password Reset Email Sent") },
            text = { Text("Please check your email inbox for password reset instructions.") },
            confirmButton = {
                Button(
                    onClick = { authViewModel.dismissResetPasswordDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showSignUp) {
        val name by authViewModel.signUpName.collectAsState()
        val email by authViewModel.signUpEmail.collectAsState()
        val phone by authViewModel.signUpPhone.collectAsState()
        val password by authViewModel.signUpPassword.collectAsState()
        val confirmPassword by authViewModel.signUpConfirmPassword.collectAsState()
        val selectedState by authViewModel.signUpState.collectAsState()

        SignUpScreen(
            language = AppLanguage.ENGLISH,
            name = name,
            email = email,
            phone = phone,
            password = password,
            confirmPassword = confirmPassword,
            selectedState = selectedState,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onNameChange = { authViewModel.updateSignUpName(it) },
            onEmailChange = { authViewModel.updateSignUpEmail(it) },
            onPhoneChange = { authViewModel.updateSignUpPhone(it) },
            onPasswordChange = { authViewModel.updateSignUpPassword(it) },
            onConfirmPasswordChange = { authViewModel.updateSignUpConfirmPassword(it) },
            onStateChange = { authViewModel.updateSignUpState(it) },
            onSignUp = { authViewModel.signUp() },
            onNavigateToLogin = { authViewModel.navigateToLogin() },
            onClearError = { authViewModel.clearError() }
        )
    } else {
        val email by authViewModel.loginEmail.collectAsState()
        val password by authViewModel.loginPassword.collectAsState()

        LoginScreen(
            language = AppLanguage.ENGLISH,
            email = email,
            password = password,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onEmailChange = { authViewModel.updateLoginEmail(it) },
            onPasswordChange = { authViewModel.updateLoginPassword(it) },
            onLogin = { authViewModel.login() },
            onNavigateToSignUp = { authViewModel.navigateToSignUp() },
            onForgotPassword = { authViewModel.resetPassword() },
            onClearError = { authViewModel.clearError() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriIndiaAppContent(viewModel: AgriViewModel, authViewModel: AuthViewModel) {
    val language by viewModel.language.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val showKisanMitra by viewModel.showKisanMitra.collectAsState()
    val showYojnaCalc by viewModel.showYojnaCalculator.collectAsState()
    val showCartSheet by viewModel.showCartSheet.collectAsState()
    val showCheckout by viewModel.showCheckout.collectAsState()
    val showProfile by authViewModel.showProfile.collectAsState()
    val showOrderConfirmation by viewModel.showOrderConfirmation.collectAsState()
    val paymentResult by viewModel.paymentResult.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()

    val isHi = language == AppLanguage.HINDI
    val context = LocalContext.current

    val navItems = listOf(
        NavItem(if (isHi) "मौसम" else "Weather", Icons.Default.WbSunny),
        NavItem(if (isHi) "योजनाएं" else "Yojnas", Icons.Default.AccountBalance),
        NavItem(if (isHi) "मंडी भाव" else "Mandi", Icons.Default.Storefront),
        NavItem(if (isHi) "बाज़ार" else "Bazaar", Icons.Default.ShoppingCart),
        NavItem(if (isHi) "चौपाल" else "Chopal", Icons.Default.Groups),
        NavItem(if (isHi) "ज्ञान" else "Gyan", Icons.Default.MenuBook)
    )

    // Profile screen overlay
    if (showProfile) {
        ProfileScreen(
            language = language,
            user = currentUser,
            orders = orderHistory,
            onLogout = { authViewModel.logout() },
            onDismiss = { authViewModel.toggleProfile(false) }
        )
        return
    }

    // Checkout screen overlay
    if (showCheckout) {
        CheckoutScreen(
            language = language,
            cartItems = cartItems,
            appliedCoupon = appliedCoupon,
            onUpdateQuantity = { productId, delta -> viewModel.updateCartQuantity(productId, delta) },
            onApplyCoupon = { code -> viewModel.applyCoupon(code) },
            onProceedPayment = {
                val activity = context as? android.app.Activity
                if (activity != null) {
                    viewModel.initiatePayment(activity, currentUser)
                }
            },
            onDismiss = { viewModel.toggleCheckout(false) }
        )
        return
    }

    // Order confirmation dialog
    if (showOrderConfirmation) {
        val result = paymentResult
        AlertDialog(
            onDismissRequest = { viewModel.dismissOrderConfirmation() },
            icon = {
                Icon(
                    imageVector = if (result is PaymentResult.Success) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (result is PaymentResult.Success) Color(0xFF059669) else Color(0xFFDC2626),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = when (result) {
                        is PaymentResult.Success -> if (isHi) "ऑर्डर सफल!" else "Order Placed!"
                        is PaymentResult.Failed -> if (isHi) "भुगतान विफल" else "Payment Failed"
                        else -> if (isHi) "भुगतान रद्द" else "Payment Cancelled"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (result) {
                        is PaymentResult.Success -> if (isHi) "आपका ऑर्डर सफलतापूर्वक दे दिया गया है।\nPayment ID: ${result.paymentId}" else "Your order has been placed successfully.\nPayment ID: ${result.paymentId}"
                        is PaymentResult.Failed -> if (isHi) "भुगतान प्रक्रिया में त्रुटि: ${result.errorMessage}" else "Payment error: ${result.errorMessage}"
                        else -> if (isHi) "भुगतान रद्द कर दिया गया।" else "Payment was cancelled."
                    },
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissOrderConfirmation() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isHi) "ठीक है" else "OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AgriTopAppBar(
                currentLanguage = language,
                cartItemCount = cartItems.sumOf { it.quantity },
                userName = currentUser?.name?.split(" ")?.firstOrNull(),
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenCart = { viewModel.toggleCartSheet(true) },
                onOpenKisanMitra = { viewModel.toggleKisanMitra(true) },
                onOpenProfile = { authViewModel.toggleProfile(true) }
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF059669)
                        )
                    }
                }
            },
            confirmButton = {
                if (cartItems.isNotEmpty()) {
                    Button(
                        onClick = {
                            viewModel.toggleCartSheet(false)
                            viewModel.toggleCheckout(true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHi) "चेकआउट करें" else "Proceed to Checkout")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleCartSheet(false) }) {
                    Text(if (isHi) "बंद करें" else "Close", color = Color(0xFF64748B))
                }
            }
        )
    }
}

data class NavItem(val label: String, val icon: ImageVector)
