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
        const val RAZORPAY_KEY = "rzp_test_XXXXXXXXX"
    }

    fun initiatePayment(
        activity: Activity,
        amount: Double,
        orderId: String,
        user: User?,
        description: String = "AgriIndia Bazaar Purchase"
    ) {
        val checkout = Checkout()
        checkout.setKeyID(RAZORPAY_KEY)

        try {
            val options = org.json.JSONObject().apply {
                put("name", "AgriIndia Bazaar")
                put("description", description)
                put("order_id", "") // Server-generated order ID in production
                put("currency", "INR")
                put("amount", (amount * 100).toLong()) // Razorpay expects paise

                val prefill = org.json.JSONObject().apply {
                    put("email", user?.email ?: "")
                    put("contact", user?.phone ?: "")
                }
                put("prefill", prefill)

                val theme = org.json.JSONObject().apply {
                    put("color", "#059669")
                }
                put("theme", theme)

                put("image", "https://i.imgur.com/3g7nmJC.png") // App logo placeholder
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
