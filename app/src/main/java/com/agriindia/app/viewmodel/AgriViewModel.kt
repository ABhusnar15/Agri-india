package com.agriindia.app.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agriindia.app.model.*
import com.agriindia.app.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgriViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: AgriRepository = AgriRepository()
    private val paymentRepository: PaymentRepository = PaymentRepository(application)
    private val locationRepository: LocationRepository = LocationRepository(application)

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Weather State
    val locations = repository.getWeatherLocations()
    private val _selectedLocation = MutableStateFlow(locations[0])
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _weatherData = MutableStateFlow(repository.getWeatherData(locations[0]))
    val weatherData: StateFlow<WeatherData> = _weatherData.asStateFlow()

    private val _isFetchingGps = MutableStateFlow(false)
    val isFetchingGps: StateFlow<Boolean> = _isFetchingGps.asStateFlow()

    private val _gpsLocation = MutableStateFlow<GpsLocation?>(null)
    val gpsLocation: StateFlow<GpsLocation?> = _gpsLocation.asStateFlow()

    // Yojna State
    private val _yojnas = MutableStateFlow(repository.getGovernmentSchemes())
    val yojnas: StateFlow<List<YojnaScheme>> = _yojnas.asStateFlow()

    private val _showYojnaCalculator = MutableStateFlow(false)
    val showYojnaCalculator: StateFlow<Boolean> = _showYojnaCalculator.asStateFlow()

    // Mandi Rates State
    private val _mandiPrices = MutableStateFlow(repository.getMandiPrices())
    val mandiPrices: StateFlow<List<MandiPrice>> = _mandiPrices.asStateFlow()

    private val _mandiSearchQuery = MutableStateFlow("")
    val mandiSearchQuery: StateFlow<String> = _mandiSearchQuery.asStateFlow()

    private val _mandiSelectedState = MutableStateFlow("All States")
    val mandiSelectedState: StateFlow<String> = _mandiSelectedState.asStateFlow()

    // Bazaar E-Commerce State
    private val _bazaarProducts = MutableStateFlow(repository.getBazaarProducts())
    val bazaarProducts: StateFlow<List<Product>> = _bazaarProducts.asStateFlow()

    private val _bazaarCategory = MutableStateFlow("All")
    val bazaarCategory: StateFlow<String> = _bazaarCategory.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _showCartSheet = MutableStateFlow(false)
    val showCartSheet: StateFlow<Boolean> = _showCartSheet.asStateFlow()

    private val _showSellProduceDialog = MutableStateFlow(false)
    val showSellProduceDialog: StateFlow<Boolean> = _showSellProduceDialog.asStateFlow()

    // Checkout & Orders State
    private val _showCheckout = MutableStateFlow(false)
    val showCheckout: StateFlow<Boolean> = _showCheckout.asStateFlow()

    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory: StateFlow<List<Order>> = _orderHistory.asStateFlow()

    private val _paymentResult = MutableStateFlow<PaymentResult?>(null)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult.asStateFlow()

    private val _showOrderConfirmation = MutableStateFlow(false)
    val showOrderConfirmation: StateFlow<Boolean> = _showOrderConfirmation.asStateFlow()

    // Community State
    private val _communityPosts = MutableStateFlow(repository.getCommunityPosts())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // Articles State
    private val _articles = MutableStateFlow(repository.getArticles())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _selectedArticle = MutableStateFlow<Article?>(null)
    val selectedArticle: StateFlow<Article?> = _selectedArticle.asStateFlow()

    // AI Helper & Diagnostics
    private val _showKisanMitra = MutableStateFlow(false)
    val showKisanMitra: StateFlow<Boolean> = _showKisanMitra.asStateFlow()

    private val _showCropDoctor = MutableStateFlow(false)
    val showCropDoctor: StateFlow<Boolean> = _showCropDoctor.asStateFlow()

    private val _showFertilizerCalc = MutableStateFlow(false)
    val showFertilizerCalc: StateFlow<Boolean> = _showFertilizerCalc.asStateFlow()

    private val _showDroneSpray = MutableStateFlow(false)
    val showDroneSpray: StateFlow<Boolean> = _showDroneSpray.asStateFlow()

    private val _showSoilHealth = MutableStateFlow(false)
    val showSoilHealth: StateFlow<Boolean> = _showSoilHealth.asStateFlow()

    private val _showPashuChikitsa = MutableStateFlow(false)
    val showPashuChikitsa: StateFlow<Boolean> = _showPashuChikitsa.asStateFlow()

    private val _showSolarPump = MutableStateFlow(false)
    val showSolarPump: StateFlow<Boolean> = _showSolarPump.asStateFlow()

    private val _showKrishiKhata = MutableStateFlow(false)
    val showKrishiKhata: StateFlow<Boolean> = _showKrishiKhata.asStateFlow()

    private val _selectedTrendCommodity = MutableStateFlow<MandiPrice?>(null)
    val selectedTrendCommodity: StateFlow<MandiPrice?> = _selectedTrendCommodity.asStateFlow()

    init {
        viewModelScope.launch {
            val remotePrices = repository.getMandiPricesAsync()
            if (remotePrices.isNotEmpty()) {
                _mandiPrices.value = remotePrices
            }
        }
        viewModelScope.launch {
            val remotePosts = repository.getCommunityPostsAsync()
            if (remotePosts.isNotEmpty()) {
                _communityPosts.value = remotePosts
            }
        }
    }

    // Actions
    fun toggleLanguage() {
        _language.value = when (_language.value) {
            AppLanguage.ENGLISH -> AppLanguage.HINDI
            AppLanguage.HINDI -> AppLanguage.MARATHI
            AppLanguage.MARATHI -> AppLanguage.ENGLISH
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setWeatherLocation(loc: String) {
        _selectedLocation.value = loc
        _weatherData.value = repository.getWeatherData(loc)
    }

    fun fetchLiveGpsLocation() {
        viewModelScope.launch {
            _isFetchingGps.value = true
            val loc = locationRepository.fetchCurrentGpsLocation()
            _isFetchingGps.value = false
            if (loc != null) {
                _gpsLocation.value = loc
                setWeatherLocation(loc.displayLocation)
                setMandiStateFilter(loc.stateName)
            }
        }
    }

    fun toggleYojnaCalculator(show: Boolean) {
        _showYojnaCalculator.value = show
    }

    fun setMandiSearchQuery(query: String) {
        _mandiSearchQuery.value = query
    }

    fun setMandiStateFilter(stateName: String) {
        _mandiSelectedState.value = stateName
    }

    fun setBazaarCategory(cat: String) {
        _bazaarCategory.value = cat
    }

    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id }
        if (existingIndex >= 0) {
            current[existingIndex] = current[existingIndex].copy(quantity = current[existingIndex].quantity + 1)
        } else {
            current.add(CartItem(product = product, quantity = 1))
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val newQty = current[index].quantity + delta
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = newQty)
            }
            _cartItems.value = current
        }
    }

    fun applyCoupon(code: String): Boolean {
        return if (code.trim().uppercase() == "KISAN50") {
            _appliedCoupon.value = "KISAN50"
            true
        } else {
            false
        }
    }

    fun toggleCartSheet(show: Boolean) {
        _showCartSheet.value = show
    }

    fun toggleSellProduceDialog(show: Boolean) {
        _showSellProduceDialog.value = show
    }

    fun toggleCheckout(show: Boolean) {
        _showCheckout.value = show
    }

    fun loadOrderHistory(userId: String) {
        viewModelScope.launch {
            _orderHistory.value = paymentRepository.getOrdersByUser(userId)
        }
    }

    fun initiatePayment(activity: Activity, user: User?) {
        val total = calculateCartTotal()
        paymentRepository.initiatePayment(
            activity = activity,
            amount = total,
            orderId = "ORD-${System.currentTimeMillis()}",
            user = user
        )
    }

    fun onPaymentSuccess(paymentId: String, userId: String) {
        viewModelScope.launch {
            val total = calculateCartTotal()
            val order = paymentRepository.createOrder(
                items = _cartItems.value,
                totalAmount = total,
                paymentId = paymentId,
                userId = userId
            )
            _orderHistory.value = listOf(order) + _orderHistory.value
            _paymentResult.value = PaymentResult.Success(paymentId, order.orderId)
            _cartItems.value = emptyList()
            _appliedCoupon.value = null
            _showCheckout.value = false
            _showOrderConfirmation.value = true
        }
    }

    fun onPaymentError(code: Int, message: String) {
        _paymentResult.value = PaymentResult.Failed(code, message)
    }

    fun onPaymentCancelled() {
        _paymentResult.value = PaymentResult.Cancelled
    }

    fun dismissOrderConfirmation() {
        _showOrderConfirmation.value = false
        _paymentResult.value = null
    }

    private fun calculateCartTotal(): Double {
        val subtotal = _cartItems.value.sumOf { it.product.price * it.quantity }
        val discount = if (_appliedCoupon.value != null) subtotal * 0.1 else 0.0
        val delivery = if (subtotal > 500) 0.0 else 49.0
        return subtotal - discount + delivery
    }

    fun togglePostLike(postId: String) {
        val current = _communityPosts.value.toMutableList()
        val index = current.indexOfFirst { it.id == postId }
        if (index >= 0) {
            val post = current[index]
            val newIsLiked = !post.isLiked
            val newLikes = if (newIsLiked) post.likesCount + 1 else post.likesCount - 1
            current[index] = post.copy(isLiked = newIsLiked, likesCount = newLikes)
            _communityPosts.value = current
        }
    }

    fun addCommunityPost(content: String, category: String) {
        val newPost = CommunityPost(
            id = "cp_${System.currentTimeMillis()}",
            authorName = "You (Farmer)",
            authorState = _selectedLocation.value.split(",").lastOrNull()?.trim() ?: "India",
            timeAgo = "Just now",
            category = category,
            content = content,
            contentHi = content,
            likesCount = 0,
            commentsCount = 0
        )
        _communityPosts.value = listOf(newPost) + _communityPosts.value
        viewModelScope.launch {
            repository.publishCommunityPostToFirestore(newPost)
        }
    }

    fun toggleArticleBookmark(articleId: String) {
        val current = _articles.value.toMutableList()
        val index = current.indexOfFirst { it.id == articleId }
        if (index >= 0) {
            val art = current[index]
            current[index] = art.copy(isBookmarked = !art.isBookmarked)
            _articles.value = current
        }
    }

    fun selectArticle(article: Article?) {
        _selectedArticle.value = article
    }

    fun toggleKisanMitra(show: Boolean) {
        _showKisanMitra.value = show
    }

    fun toggleCropDoctor(show: Boolean) {
        _showCropDoctor.value = show
    }

    fun toggleFertilizerCalculator(show: Boolean) {
        _showFertilizerCalc.value = show
    }

    fun toggleDroneSpray(show: Boolean) {
        _showDroneSpray.value = show
    }

    fun toggleSoilHealth(show: Boolean) {
        _showSoilHealth.value = show
    }

    fun togglePashuChikitsa(show: Boolean) {
        _showPashuChikitsa.value = show
    }

    fun toggleSolarPump(show: Boolean) {
        _showSolarPump.value = show
    }

    fun toggleKrishiKhata(show: Boolean) {
        _showKrishiKhata.value = show
    }

    fun selectTrendCommodity(item: MandiPrice?) {
        _selectedTrendCommodity.value = item
    }
}
