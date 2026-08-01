package com.agriindia.app.repository

import android.app.Activity
import com.agriindia.app.model.CartItem
import com.agriindia.app.model.Order
import com.agriindia.app.model.User
import com.razorpay.Checkout

class PaymentRepository {

    companion object {
        // TODO: Replace with your actual Razorpay test/live key
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

    fun createOrder(
        items: List<CartItem>,
        totalAmount: Double,
        paymentId: String,
        userId: String
    ): Order {
        return Order(
            orderId = "ORD-${System.currentTimeMillis()}",
            userId = userId,
            items = items.map { "${it.product.name} x${it.quantity}" },
            totalAmount = totalAmount,
            paymentId = paymentId,
            paymentStatus = "Paid",
            timestamp = System.currentTimeMillis()
        )
    }
}
