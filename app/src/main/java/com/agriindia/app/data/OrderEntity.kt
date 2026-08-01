package com.agriindia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agriindia.app.model.Order

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val userId: String,
    val itemNamesJoined: String, // Items separated by semicolon for storage
    val totalAmount: Double,
    val paymentId: String,
    val paymentStatus: String,
    val timestamp: Long
) {
    fun toOrderModel(): Order = Order(
        orderId = orderId,
        userId = userId,
        items = if (itemNamesJoined.isBlank()) emptyList() else itemNamesJoined.split(";; "),
        totalAmount = totalAmount,
        paymentId = paymentId,
        paymentStatus = paymentStatus,
        timestamp = timestamp
    )

    companion object {
        fun fromOrderModel(order: Order): OrderEntity = OrderEntity(
            orderId = order.orderId,
            userId = order.userId,
            itemNamesJoined = order.items.joinToString(";; "),
            totalAmount = order.totalAmount,
            paymentId = order.paymentId,
            paymentStatus = order.paymentStatus,
            timestamp = order.timestamp
        )
    }
}
