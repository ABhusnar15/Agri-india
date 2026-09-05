package com.agriindia.app.repository

import android.app.Activity
import android.content.Context
import com.agriindia.app.data.AppDatabase
import com.agriindia.app.data.OrderEntity
import com.agriindia.app.model.CartItem
import com.agriindia.app.model.Order
import com.agriindia.app.model.User
import com.razorpay.Checkout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository(private val context: Context? = null) {

    private val orderDao by lazy { context?.let { AppDatabase.getDatabase(it).orderDao() } }

    companion object {
        var RAZORPAY_KEY = "rzp_test_XXXXXXXXX"
    }

    fun initiatePayment(
        activity: Activity,
        amount: Double,
        orderId: String,
        user: User?,
        description: String = "AgriIndia Bazaar Purchase",
        customKey: String? = null
    ) {
        val checkout = Checkout()
        val keyToUse = customKey?.takeIf { it.isNotBlank() } ?: RAZORPAY_KEY
        checkout.setKeyID(keyToUse)

        try {
            val options = org.json.JSONObject().apply {
                put("name", "AgriIndia Bazaar")
                put("description", description)
                put("currency", "INR")
                put("amount", (amount * 100).toLong()) // Razorpay expects amount in paise

                val prefill = org.json.JSONObject().apply {
                    val emailStr = user?.email?.takeIf { it.isNotBlank() } ?: "farmer@agriindia.app"
                    val phoneStr = user?.phone?.takeIf { it.isNotBlank() } ?: "9876543210"
                    put("email", emailStr)
                    put("contact", phoneStr)
                }
                put("prefill", prefill)

                val theme = org.json.JSONObject().apply {
                    put("color", "#059669")
                }
                put("theme", theme)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createOrder(
        items: List<CartItem>,
        totalAmount: Double,
        paymentId: String,
        userId: String
    ): Order = withContext(Dispatchers.IO) {
        val order = Order(
            orderId = "ORD-${System.currentTimeMillis()}",
            userId = userId,
            items = items.map { "${it.product.name} x${it.quantity}" },
            totalAmount = totalAmount,
            paymentId = paymentId,
            paymentStatus = "Paid",
            timestamp = System.currentTimeMillis()
        )
        orderDao?.insertOrder(OrderEntity.fromOrderModel(order))
        order
    }

    suspend fun getOrdersByUser(userId: String): List<Order> = withContext(Dispatchers.IO) {
        orderDao?.getOrdersByUser(userId)?.map { it.toOrderModel() } ?: emptyList()
    }
}
