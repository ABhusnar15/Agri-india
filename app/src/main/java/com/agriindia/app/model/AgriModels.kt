 package com.agriindia.app.model

data class WeatherData(
    val location: String,
    val temperature: Int,
    val condition: String,
    val humidity: Int,
    val rainProbability: Int,
    val windSpeed: Int,
    val soilMoisture: Int,
    val advisory: String,
    val advisoryHi: String,
    val weeklyForecast: List<DayForecast>
)

data class DayForecast(
    val day: String,
    val dayHi: String,
    val tempMax: Int,
    val tempMin: Int,
    val condition: String,
    val rainChance: Int
)

data class YojnaScheme(
    val id: String,
    val title: String,
    val titleHi: String,
    val category: String,
    val subsidyAmount: String,
    val description: String,
    val descriptionHi: String,
    val eligibility: String,
    val eligibilityHi: String,
    val documents: List<String>,
    val officialLink: String,
    val isCentral: Boolean
)

data class MandiPrice(
    val id: String,
    val commodity: String,
    val commodityHi: String,
    val state: String,
    val district: String,
    val mandiName: String,
    val minPrice: Int,
    val maxPrice: Int,
    val modalPrice: Int,
    val unit: String = "Quintal",
    val priceChange: Int, // positive for increase, negative for decrease
    val lastUpdated: String
)

data class Product(
    val id: String,
    val name: String,
    val nameHi: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val isSubsidyAvailable: Boolean,
    val subsidyTag: String,
    val seller: String,
    val description: String
)

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorState: String,
    val timeAgo: String,
    val category: String,
    val content: String,
    val contentHi: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isVerifiedExpert: Boolean = false,
    val isLiked: Boolean = false,
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: String,
    val author: String,
    val text: String,
    val timeAgo: String
)

data class Article(
    val id: String,
    val title: String,
    val titleHi: String,
    val category: String,
    val readTime: String,
    val author: String,
    val excerpt: String,
    val excerptHi: String,
    val fullContent: String,
    val fullContentHi: String,
    val isBookmarked: Boolean = false
)

enum class AppLanguage {
    ENGLISH, HINDI
}

// ---- Authentication Models ----

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val state: String,
    val profileImageUrl: String? = null
)

sealed class AuthState {
    data object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

// ---- Payment Models ----

sealed class PaymentResult {
    data class Success(val paymentId: String, val orderId: String?) : PaymentResult()
    data class Failed(val errorCode: Int, val errorMessage: String) : PaymentResult()
    data object Cancelled : PaymentResult()
}

data class Order(
    val orderId: String,
    val userId: String,
    val items: List<String>,
    val totalAmount: Double,
    val paymentId: String,
    val paymentStatus: String, // "Paid", "Failed", "Pending"
    val timestamp: Long
)

