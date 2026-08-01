package com.agriindia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getOrdersByUser(userId: String): List<OrderEntity>

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<OrderEntity>
}
