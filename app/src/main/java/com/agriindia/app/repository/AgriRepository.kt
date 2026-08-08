package com.agriindia.app.repository

import com.agriindia.app.model.*
import com.google.firebase.dataconnect.FirebaseDataConnect
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AgriRepository {

    private val dataConnect: Any? by lazy {
        try {
            val clazz = Class.forName("com.google.firebase.dataconnect.FirebaseDataConnect")
            clazz.getMethod("getInstance").invoke(null)
        } catch (e: Exception) {
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun getWeatherLocations(): List<String> = listOf(
        "Ludhiana, Punjab",
        "Nashik, Maharashtra",
        "Varanasi, Uttar Pradesh",
        "Nagpur, Madhya Pradesh",
        "Mysuru, Karnataka"
    )

    fun getWeatherData(location: String): WeatherData {
        return when (location) {
            "Nashik, Maharashtra" -> WeatherData(
                location = "Nashik, Maharashtra",
                temperature = 31,
                condition = "Partly Cloudy",
                humidity = 72,
                rainProbability = 40,
                windSpeed = 14,
                soilMoisture = 68,
                advisory = "Moderate humidity. Spray fungicide on onion and grape crops before expected rain.",
                advisoryHi = "मध्यम आर्द्रता। बारिश की संभावना से पहले प्याज और अंगूर की फसल पर कवकनाशी का छिड़काव करें।",
                weeklyForecast = listOf(
                    DayForecast("Mon", "सोम", 32, 24, "Sunny", 10),
                    DayForecast("Tue", "मंगल", 31, 23, "Partly Cloudy", 40),
                    DayForecast("Wed", "बुध", 29, 22, "Rain Showers", 80),
                    DayForecast("Thu", "गुरु", 30, 23, "Thunderstorm", 65),
                    DayForecast("Fri", "शुक्र", 32, 24, "Sunny", 15),
                    DayForecast("Sat", "शनि", 33, 25, "Clear", 5),
                    DayForecast("Sun", "रवि", 34, 25, "Sunny", 0)
                )
            )
            "Varanasi, Uttar Pradesh" -> WeatherData(
                location = "Varanasi, Uttar Pradesh",
                temperature = 34,
                condition = "Humid & Clear",
                humidity = 80,
                rainProbability = 20,
                windSpeed = 10,
                soilMoisture = 75,
                advisory = "Ensure proper field drainage for Paddy (Dhan) crops after recent showers.",
                advisoryHi = "हाल ही में हुई बारिश के बाद धान की फसल के लिए खेत में जल निकासी की उचित व्यवस्था सुनिश्चित करें।",
                weeklyForecast = listOf(
                    DayForecast("Mon", "सोम", 34, 26, "Sunny", 20),
                    DayForecast("Tue", "मंगल", 35, 27, "Hot & Humid", 15),
                    DayForecast("Wed", "बुध", 33, 25, "Light Rain", 50),
                    DayForecast("Thu", "गुरु", 32, 24, "Rain Showers", 70),
                    DayForecast("Fri", "शुक्र", 33, 25, "Partly Cloudy", 30),
                    DayForecast("Sat", "शनि", 34, 26, "Sunny", 10),
                    DayForecast("Sun", "रवि", 35, 27, "Sunny", 0)
                )
            )
            else -> WeatherData(
                location = location,
                temperature = 32,
                condition = "Sunny & Warm",
                humidity = 68,
                rainProbability = 20,
                windSpeed = 12,
                soilMoisture = 72,
                advisory = "Live GPS Location ($location): Optimal time for irrigation and crop inspection. Maintain soil moisture levels.",
                advisoryHi = "लाइव जीपीएस स्थिति ($location): सिंचाई और फसल निरीक्षण का सही समय। मिट्टी की नमी बनाए रखें।",
                weeklyForecast = listOf(
                    DayForecast("Mon", "सोम", 33, 24, "Sunny", 15),
                    DayForecast("Tue", "मंगल", 34, 25, "Clear", 10),
                    DayForecast("Wed", "बुध", 32, 23, "Partly Cloudy", 30),
                    DayForecast("Thu", "गुरु", 31, 23, "Rain Showers", 60),
                    DayForecast("Fri", "शुक्र", 33, 24, "Sunny", 20),
                    DayForecast("Sat", "शनि", 35, 26, "Sunny", 5),
                    DayForecast("Sun", "रवि", 36, 26, "Sunny", 0)
                )
            )
        }
    }

    fun getGovernmentSchemes(): List<YojnaScheme> = listOf(
        YojnaScheme(
            id = "y1",
            title = "PM-Kisan Samman Nidhi Yojana",
            titleHi = "पीएम-किसान सम्मान निधि योजना",
            category = "Financial Assistance",
            subsidyAmount = "₹6,000 / year",
            description = "Direct income support of ₹6,000 per year in 3 equal installments to small and marginal farmer families across India.",
            descriptionHi = "भारत भर के छोटे और सीमांत किसान परिवारों को 3 समान किस्तों में प्रति वर्ष ₹6,000 की प्रत्यक्ष आय सहायता।",
            eligibility = "All landholding farmers' families possessing cultivable landholding up to 2 hectares.",
            eligibilityHi = "2 हेक्टेयर तक कृषि योग्य भूमि वाले सभी भूमिधारक किसान परिवार।",
            documents = listOf("Aadhaar Card", "Land Khata/Khasra Extract", "Bank Passbook with NPCI linking"),
            officialLink = "https://pmkisan.gov.in",
            isCentral = true
        ),
        YojnaScheme(
            id = "y2",
            title = "Pradhan Mantri Fasal Bima Yojana (PMFBY)",
            titleHi = "प्रधानमंत्री फसल बीमा योजना (PMFBY)",
            category = "Crop Insurance",
            subsidyAmount = "Up to 90% Premium Subsidy",
            description = "Comprehensive crop insurance cover against non-preventable natural risks from pre-sowing to post-harvest stages.",
            descriptionHi = "बुआई से पहले और कटाई के बाद के चरणों तक अपरिहार्य प्राकृतिक जोखिमों के खिलाफ व्यापक फसल बीमा कवर।",
            eligibility = "All farmers including sharecroppers and tenant farmers growing notified crops in notified areas.",
            eligibilityHi = "अधिसूचित क्षेत्रों में अधिसूचित फसलें उगाने वाले सभी किसान, बटाईदार और पट्टेदार किसान।",
            documents = listOf("Sowing Certificate", "Land Rent Agreement", "Aadhaar Card", "Bank Account"),
            officialLink = "https://pmfby.gov.in",
            isCentral = true
        ),
        YojnaScheme(
            id = "y3",
            title = "Soil Health Card Scheme",
            titleHi = "मृदा स्वास्थ्य कार्ड योजना",
            category = "Soil Health & Testing",
            subsidyAmount = "100% Free Soil Testing",
            description = "Provides crop-wise recommendations of nutrients and fertilizers required for individual farm soils to optimize yield.",
            descriptionHi = "उपज को अनुकूलित करने के लिए व्यक्तिगत कृषि मिट्टी के लिए आवश्यक पोषक तत्वों और उर्वरकों की फसल-वार सिफारिशें प्रदान करता है।",
            eligibility = "Open to all agricultural landholders across India.",
            eligibilityHi = "भारत भर के सभी कृषि भूमिधारकों के लिए खुला है।",
            documents = listOf("Aadhaar Card", "Soil Sample Application Form"),
            officialLink = "https://soilhealth.dac.gov.in",
            isCentral = true
        ),
        YojnaScheme(
            id = "y4",
            title = "Kisan Credit Card (KCC) Scheme",
            titleHi = "किसान क्रेडिट कार्ड (KCC) योजना",
            category = "Agricultural Credit",
            subsidyAmount = "Loan up to ₹3 Lakh at 4% Interest",
            description = "Provides farmers with timely & affordable short-term credit for crop cultivation, farm maintenance, and equipment.",
            descriptionHi = "किसानों को फसल की खेती, खेत के रखरखाव और उपकरणों के लिए समय पर और किफायती अल्पकालिक ऋण प्रदान करता है।",
            eligibility = "Individual / Joint borrowers, tenant farmers, self-help groups, and oral lessees.",
            eligibilityHi = "व्यक्तिगत / संयुक्त उधारकर्ता, पट्टेदार किसान, स्वयं सहायता समूह और मौखिक पट्टेदार।",
            documents = listOf("Application Form", "Identity & Address Proof", "Land Ownership Record"),
            officialLink = "https://hb.pmkisan.gov.in",
            isCentral = true
        ),
        YojnaScheme(
            id = "y5",
            title = "Sub-Mission on Agricultural Mechanization (SMAM)",
            titleHi = "कृषि यांत्रीकरण पर उप-मिशन (SMAM)",
            category = "Farm Machinery Subsidy",
            subsidyAmount = "40% to 80% Machinery Subsidy",
            description = "Financial assistance for purchasing tractors, power tillers, rotavators, combines, and setting up Custom Hiring Centers.",
            descriptionHi = "ट्रैक्टर, पावर टिलर, रोटावेटर, कंबाइन खरीदने और कस्टम हायरिंग सेंटर स्थापित करने के लिए वित्तीय सहायता।",
            eligibility = "Small & marginal farmers, women farmers, SC/ST farmers.",
            eligibilityHi = "छोटे और सीमांत किसान, महिला किसान, अनुसूचित जाति/अनुसूचित जनजाति के किसान।",
            documents = listOf("Aadhaar Card", "Quotation of Machinery", "Land Record", "Bank Passbook"),
            officialLink = "https://agrimachinery.nic.in",
            isCentral = true
        )
    )

    fun getMandiPrices(): List<MandiPrice> = listOf(
        MandiPrice("m1", "Wheat", "गेहूं", "Punjab", "Ludhiana", "Ludhiana Central", 2275, 2450, 2380, "Quintal", 35, "Today, 09:30 AM"),
        MandiPrice("m2", "Paddy / Rice", "धान / चावल", "Punjab", "Amritsar", "Amritsar Mandi", 2183, 2350, 2290, "Quintal", 15, "Today, 10:15 AM"),
        MandiPrice("m3", "Onion", "प्याज", "Maharashtra", "Nashik", "Lasalgaon Mandi", 1400, 2400, 2100, "Quintal", -80, "Today, 11:00 AM"),
        MandiPrice("m4", "Tomato", "टमाटर", "Karnataka", "Kolar", "Kolar APMC", 1200, 2800, 2200, "Quintal", 120, "Today, 10:45 AM"),
        MandiPrice("m5", "Cotton", "कपास", "Gujarat", "Rajkot", "Rajkot Market Yard", 6800, 7550, 7250, "Quintal", 50, "Today, 09:50 AM"),
        MandiPrice("m6", "Mustard", "सरसों", "Rajasthan", "Bharatpur", "Bharatpur Mandi", 5200, 5700, 5480, "Quintal", -40, "Today, 08:30 AM"),
        MandiPrice("m7", "Soyabean", "सोयाबीन", "Madhya Pradesh", "Ujjain", "Ujjain Mandi", 4200, 4850, 4600, "Quintal", 25, "Today, 10:30 AM")
    )

    fun getBazaarProducts(): List<Product> = listOf(
        Product(
            id = "p1",
            name = "HD-3086 Certified Hybrid Wheat Seed (40 kg)",
            nameHi = "HD-3086 प्रमाणित हाइब्रिड गेहूं बीज (40 किग्रा)",
            category = "Seeds",
            price = 1450.0,
            originalPrice = 1800.0,
            rating = 4.8,
            reviewCount = 342,
            isSubsidyAvailable = true,
            subsidyTag = "Govt Subsidized (20%)",
            seller = "National Seeds Corporation (NSC)",
            description = "High yield rust-resistant certified wheat seeds suitable for irrigated timely sowing in North-Western plains."
        ),
        Product(
            id = "p2",
            name = "Organic Bio-NPK Liquid Fertilizer (1 Liter)",
            nameHi = "जैविक बायो-एनपीके तरल उर्वरक (1 लीटर)",
            category = "Fertilizers",
            price = 420.0,
            originalPrice = 550.0,
            rating = 4.7,
            reviewCount = 189,
            isSubsidyAvailable = false,
            subsidyTag = "Eco-Friendly",
            seller = "IFFCO Organic Ltd",
            description = "Contains nitrogen fixing bacteria, phosphate solubilizing microorganism, and potash mobilizers to improve soil fertility."
        ),
        Product(
            id = "p3",
            name = "Cold-Pressed Pure Neem Oil Pesticide (500 ml)",
            nameHi = "कोल्ड-प्रेस शुद्ध नीम तेल कीटनाशक (500 मिली)",
            category = "Pesticides",
            price = 290.0,
            originalPrice = 380.0,
            rating = 4.6,
            reviewCount = 215,
            isSubsidyAvailable = false,
            subsidyTag = "100% Organic",
            seller = "Kisan Krishi Organics",
            description = "Effective natural pest repellent targeting aphids, whiteflies, thrips, and caterpillars without soil toxicity."
        ),
        Product(
            id = "p4",
            name = "1-Acre Drip Irrigation Kit with Inline Droppers",
            nameHi = "1-एकड़ ड्रिप सिंचाई किट इनलाइन ड्रिपर्स के साथ",
            category = "Irrigation",
            price = 12500.0,
            originalPrice = 18000.0,
            rating = 4.9,
            reviewCount = 98,
            isSubsidyAvailable = true,
            subsidyTag = "PMKSY 45% Subsidy Eligible",
            seller = "Jain Irrigation Systems",
            description = "Complete drip irrigation kit including main pipe, sub-main, driplines, venturi injector, and filter for 1 acre land."
        ),
        Product(
            id = "p5",
            name = "Heavy Duty Battery Operated Knapsack Sprayer (16L)",
            nameHi = "हैवी ड्यूटी बैटरी चालित नैपसैप स्प्रेयर (16 लीटर)",
            category = "Machinery",
            price = 2490.0,
            originalPrice = 3200.0,
            rating = 4.5,
            reviewCount = 412,
            isSubsidyAvailable = true,
            subsidyTag = "SMAM Scheme Eligible",
            seller = "AgriPower Tools India",
            description = "Dual mode manual & 12V 12Ah battery sprayer with stainless steel lance and 4 variant nozzles."
        )
    )

    fun getCommunityPosts(): List<CommunityPost> = listOf(
        CommunityPost(
            id = "cp1",
            authorName = "Ramesh Patel",
            authorState = "Gujarat",
            timeAgo = "2 hours ago",
            category = "Pest Control",
            content = "My cotton crop is showing yellowing leaves on top shoots. Is this Jassid attack or deficiency? Any organic treatment suggestions?",
            contentHi = "मेरी कपास की फसल के ऊपरी पत्तों में पीलापन दिख रहा है। क्या यह जैसिड का हमला है या पोषक तत्वों की कमी? क्या कोई जैविक उपचार का सुझाव दे सकता है?",
            likesCount = 24,
            commentsCount = 8,
            isVerifiedExpert = false,
            isLiked = false,
            comments = listOf(
                Comment("c1", "Dr. S. K. Verma (KVK Scientist)", "This is early Jassid nymph infestation due to humidity. Spray Neem oil (10,000 PPM) @ 3ml/liter immediately.", "1 hour ago")
            )
        ),
        CommunityPost(
            id = "cp2",
            authorName = "Gurpreet Singh",
            authorState = "Punjab",
            timeAgo = "5 hours ago",
            category = "Organic Farming",
            content = "Switched to Jeevamrut organic fertilizer for my paddy field this season. Tillers count per hill increased from 18 to 26! Sharing recipe in comments.",
            contentHi = "इस सीजन में अपने धान के खेत के लिए जीवामृत जैविक उर्वरक का उपयोग शुरू किया। प्रति पौधा कल्लों की संख्या 18 से बढ़कर 26 हो गई!",
            likesCount = 89,
            commentsCount = 15,
            isVerifiedExpert = true,
            isLiked = true,
            comments = emptyList()
        )
    )

    fun getArticles(): List<Article> = listOf(
        Article(
            id = "a1",
            title = "Modern Drip Irrigation: How to Save 50% Water and Increase Crop Yield",
            titleHi = "आधुनिक ड्रिप सिंचाई: 50% पानी कैसे बचाएं और फसल उपज बढ़ाएं",
            category = "Irrigation & Water",
            readTime = "4 min read",
            author = "Krishi Expert Team",
            excerpt = "Learn how drip irrigation delivers water and nutrients directly to plant roots, minimizing weed growth and fertilizer runoff.",
            excerptHi = "जानें कि कैसे ड्रिप सिंचाई सीधे पौधे की जड़ों तक पानी और पोषक तत्व पहुंचाती है, जिससे खरपतवार और उर्वरक की बर्बादी कम होती है।",
            fullContent = "Drip irrigation is the most efficient water delivery system for agriculture. Water is applied drop by drop directly to the root zone of crops through a network of valves, pipes, tubing, and emitters. By delivering moisture right where plants need it, water loss through evaporation and deep percolation is reduced by up to 50%. Under government schemes like PM Krishi Sinchayee Yojana (PMKSY), farmers can claim up to 45% to 55% subsidy on installation costs.",
            fullContentHi = "ड्रिप सिंचाई कृषि के लिए सबसे कुशल जल वितरण प्रणाली है। वाल्व, पाइप और ड्रिपर्स के नेटवर्क के माध्यम से पानी को बूंद-बूंद करके सीधे फसलों के जड़ क्षेत्र में लागू किया जाता है। इससे वाष्पीकरण और पानी की बर्बादी 50% तक कम हो जाती है। पीएम कृषि सिंचाई योजना (PMKSY) के तहत किसान 45% से 55% तक सब्सिडी का दावा कर सकते हैं।"
        ),
        Article(
            id = "a2",
            title = "Step-by-Step Guide to Claim Pradhan Mantri Fasal Bima Yojana (PMFBY)",
            titleHi = "प्रधानमंत्री फसल बीमा योजना (PMFBY) का दावा करने के लिए चरण-दर-चरण मार्गदर्शिका",
            category = "Government Yojna",
            readTime = "6 min read",
            author = "Agri Legal Cell",
            excerpt = "Key deadlines, 72-hour crop damage reporting process, and document verification steps for insurance claims.",
            excerptHi = "बीमा दावों के लिए मुख्य समय सीमा, 72-घंटे की फसल क्षति रिपोर्टिंग प्रक्रिया और दस्तावेज़ सत्यापन चरण।",
            fullContent = "In case of unseasonal heavy rainfall, hailstorm, or flood causing localized crop loss, farmers must inform the insurance company or agriculture officer within 72 hours via the Crop Insurance App or toll-free number 14447. Provide land details, crop type, and photo evidence to initiate field survey.",
            fullContentHi = "बेमौसम भारी बारिश, ओलावृष्टि या बाढ़ के कारण फसल के नुकसान की स्थिति में, किसानों को 72 घंटे के भीतर क्रॉप इंश्योरेंस ऐप या टोल-फ्री नंबर 14447 के माध्यम से बीमा कंपनी या कृषि अधिकारी को सूचित करना होगा।"
        )
    )

    suspend fun getMandiPricesAsync(): List<MandiPrice> {
        val fs = firestore
        if (fs != null) {
            try {
                val snapshot = fs.collection("mandi_prices").get().await()
                if (!snapshot.isEmpty) {
                    val prices = snapshot.documents.mapNotNull { doc ->
                        MandiPrice(
                            id = doc.id,
                            commodity = doc.getString("commodity") ?: doc.getString("cropName") ?: "",
                            commodityHi = doc.getString("commodityHi") ?: doc.getString("cropNameHi") ?: "",
                            state = doc.getString("state") ?: "",
                            district = doc.getString("district") ?: "",
                            mandiName = doc.getString("mandiName") ?: doc.getString("marketName") ?: "",
                            minPrice = doc.getLong("minPrice")?.toInt() ?: 0,
                            maxPrice = doc.getLong("maxPrice")?.toInt() ?: 0,
                            modalPrice = doc.getLong("modalPrice")?.toInt() ?: 0,
                            unit = doc.getString("unit") ?: doc.getString("priceUnit") ?: "Quintal",
                            priceChange = doc.getLong("priceChange")?.toInt() ?: doc.getLong("dailyChange")?.toInt() ?: 0,
                            lastUpdated = doc.getString("lastUpdated") ?: doc.getString("updatedTime") ?: "Recently"
                        )
                    }
                    if (prices.isNotEmpty()) return prices
                }
            } catch (e: Exception) {
                // Fallback to static seed data
            }
        }
        return getMandiPrices()
    }

    suspend fun getCommunityPostsAsync(): List<CommunityPost> {
        val fs = firestore
        if (fs != null) {
            try {
                val snapshot = fs.collection("community_posts").get().await()
                if (!snapshot.isEmpty) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        CommunityPost(
                            id = doc.id,
                            authorName = doc.getString("authorName") ?: "Farmer",
                            authorState = doc.getString("authorState") ?: "India",
                            timeAgo = doc.getString("timeAgo") ?: "Just now",
                            category = doc.getString("category") ?: "General",
                            content = doc.getString("content") ?: "",
                            contentHi = doc.getString("contentHi") ?: "",
                            likesCount = doc.getLong("likesCount")?.toInt() ?: 0,
                            commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0,
                            isVerifiedExpert = doc.getBoolean("isVerifiedExpert") ?: false,
                            isLiked = false,
                            comments = emptyList()
                        )
                    }
                    if (posts.isNotEmpty()) return posts
                }
            } catch (e: Exception) {
                // Fallback to static seed data
            }
        }
        return getCommunityPosts()
    }

    suspend fun publishCommunityPostToFirestore(post: CommunityPost) {
        val fs = firestore ?: return
        try {
            val data = mapOf(
                "authorName" to post.authorName,
                "authorState" to post.authorState,
                "timeAgo" to post.timeAgo,
                "category" to post.category,
                "content" to post.content,
                "contentHi" to post.contentHi,
                "likesCount" to post.likesCount,
                "commentsCount" to post.commentsCount,
                "isVerifiedExpert" to post.isVerifiedExpert,
                "createdAt" to System.currentTimeMillis()
            )
            fs.collection("community_posts").document(post.id).set(data).await()
        } catch (e: Exception) {
            // Ignore if offline
        }
    }
}
