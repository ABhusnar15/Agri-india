package com.agriindia.app.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.agriindia.app.model.*
import com.agriindia.app.repository.AgriRepository
import com.agriindia.app.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgriViewModel(
    private val repository: AgriRepository = AgriRepository(),
    private val paymentRepository: PaymentRepository = PaymentRepository()
) : ViewModel() {

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

    // AI Helper
    private val _showKisanMitra = MutableStateFlow(false)
    val showKisanMitra: StateFlow<Boolean> = _showKisanMitra.asStateFlow()

    // Actions
    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setWeatherLocation(loc: String) {
        _selectedLocation.value = loc
        _weatherData.value = repository.getWeatherData(loc)
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
}
