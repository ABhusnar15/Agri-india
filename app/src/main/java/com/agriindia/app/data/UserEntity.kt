package com.agriindia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agriindia.app.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val passwordHash: String, // Store simple credentials for offline SQLite test
    val name: String,
    val phone: String,
    val state: String,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toUserModel(): User = User(
        uid = uid,
        name = name,
        email = email,
        phone = phone,
        state = state,
        profileImageUrl = profileImageUrl
    )
}
