 package com.agriindia.app.model

data class HourlyForecast(
    val hour: String,
    val hourHi: String,
    val temp: Int,
    val rainChance: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int
)

data class AirQualityData(
    val aqi: Int,
    val category: String,
    val categoryHi: String,
    val categoryMr: String,
    val pm25: Double,
    val pm10: Double,
    val statusColorHex: Long = 0xFF10B981
)

data class SprayAdvisory(
    val isOptimal: Boolean,
    val windowText: String,
    val windowTextHi: String,
    val windowTextMr: String,
    val reason: String,
    val reasonHi: String,
    val reasonMr: String
)

data class SoilAgronomyData(
    val soilTemp10cm: Double,
    val soilMoisturePercent: Int,
    val dewPointC: Int,
    val evapotranspirationMm: Double
)

data class SunAstronomy(
    val sunrise: String,
    val sunset: String,
    val dayLength: String,
    val uvIndex: Int,
    val uvCategory: String
)

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
    val weeklyForecast: List<DayForecast>,
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val airQuality: AirQualityData = AirQualityData(45, "Good", "उत्तम", "चांगले", 14.2, 38.5, 0xFF10B981),
    val sprayAdvisory: SprayAdvisory = SprayAdvisory(true, "06:00 AM - 10:30 AM", "सुबह 06:00 से 10:30 बजे", "सकाळी ०६:०० ते १०:३०", "Low wind & minimal evaporation drift", "कम हवा और न्यूनतम वाष्पीकरण जोखिम", "कमी वाऱ्याचा वेग आणि उत्तम फवारणी वेळ"),
    val soilAgronomy: SoilAgronomyData = SoilAgronomyData(23.8, 68, 19, 4.2),
    val sunAstronomy: SunAstronomy = SunAstronomy("05:48 AM", "07:12 PM", "13h 24m", 6, "Moderate-High"),
    val cloudCoverPercent: Int = 28,
    val pressureHpa: Int = 1012,
    val visibilityKm: Double = 9.5
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

enum class MandiTimeframe(val days: Int, val label: String, val labelHi: String, val labelMr: String) {
    SEVEN_DAYS(7, "7 Days", "7 दिन", "७ दिवस"),
    FIFTEEN_DAYS(15, "15 Days", "15 दिन", "१५ दिवस"),
    THIRTY_DAYS(30, "30 Days", "30 दिन", "३० दिवस")
}

data class MandiPriceHistoryPoint(
    val dayIndex: Int,
    val dateLabel: String,
    val price: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val arrivalVolumeTons: Double
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
    val lastUpdated: String,
    val mspPrice: Int = 2275,
    val arrivalVolumeToday: Double = 420.0,
    val marketSentiment: String = "Bullish / तेजी",
    val history30Days: List<MandiPriceHistoryPoint> = emptyList()
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
    ENGLISH, HINDI, MARATHI
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

// ---- Advanced Agri Suite Models ----

data class DroneBooking(
    val id: String,
    val cropName: String,
    val cropNameHi: String,
    val acreage: Double,
    val sprayChemical: String,
    val sprayChemicalHi: String,
    val scheduledDate: String,
    val status: String, // "Confirmed", "In Progress", "Completed", "Pending Pilot"
    val pilotName: String,
    val pilotPhone: String,
    val batterySlotsNeeded: Int,
    val baseRatePerAcre: Double = 600.0,
    val subsidyRate: Double = 0.50, // 50% Govt subsidy
    val totalCost: Double,
    val subsidySaved: Double
)

data class SoilHealthReport(
    val id: String,
    val ph: Double,
    val nitrogenKgPerHa: Double,
    val phosphorusKgPerHa: Double,
    val potassiumKgPerHa: Double,
    val organicCarbonPercent: Double,
    val zincPpm: Double,
    val sulphurPpm: Double,
    val soilCondition: String, // "Acidic", "Alkaline", "Normal/Optimal"
    val nitrogenStatus: String, // "Low", "Medium", "High"
    val phosphorusStatus: String,
    val potassiumStatus: String,
    val recommendationEn: String,
    val recommendationHi: String,
    val ureaKgAcre: Double,
    val dapKgAcre: Double,
    val mopKgAcre: Double,
    val gypsumOrLimeKgAcre: Double
)

data class LivestockDisease(
    val diseaseName: String,
    val diseaseNameHi: String,
    val animalType: String, // "Cow / गाय", "Buffalo / भैंस", "Goat / बकरी"
    val severity: String,
    val symptoms: String,
    val symptomsHi: String,
    val treatment: String,
    val treatmentHi: String,
    val vaccineDue: String,
    val emergencyHelpline: String = "1962 (Toll Free Pashu Chikitsa)"
)

data class SolarPumpConfig(
    val pumpCapacityHp: Double,
    val pumpType: String, // "Submersible DC", "Surface AC", "Submersible AC"
    val recommendedAcreage: String,
    val totalProjectCost: Double,
    val centralSubsidy: Double, // 30%
    val stateSubsidy: Double,   // 30%
    val farmerContribution: Double, // 40%
    val annualDieselSaving: Double, // ₹ saved vs diesel genset
    val dailyDischargeLiters: Int
)

data class FarmExpenseEntry(
    val id: String,
    val season: String, // "Kharif 2026", "Rabi 2025-26", "Zaid 2026"
    val crop: String,
    val entryType: String, // "Expense", "Revenue"
    val category: String, // "Seeds", "Fertilizer", "Pesticide", "Labor", "Diesel/Tractor", "Harvest Sale"
    val amount: Double,
    val date: String,
    val notes: String
)

