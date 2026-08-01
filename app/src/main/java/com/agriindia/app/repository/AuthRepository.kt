package com.agriindia.app.repository

import android.content.Context
import android.content.SharedPreferences
import com.agriindia.app.data.AppDatabase
import com.agriindia.app.data.UserEntity
import com.agriindia.app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository(private val context: Context) {

    private val userDao by lazy { AppDatabase.getDatabase(context).userDao() }
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("agri_auth_prefs", Context.MODE_PRIVATE)
    }

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        val uid = prefs.getString("logged_in_uid", null) ?: return@withContext null
        userDao.getUserById(uid)?.toUserModel()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getString("logged_in_uid", null) != null
    }

    suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        state: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return@withContext Result.failure(Exception("EMAIL_EXISTS"))
            }

            val newUid = UUID.randomUUID().toString()
            val entity = UserEntity(
                uid = newUid,
                email = email,
                passwordHash = password,
                name = name,
                phone = phone,
                state = state,
                profileImageUrl = null
            )
            userDao.insertUser(entity)
            prefs.edit().putString("logged_in_uid", newUid).apply()
            Result.success(entity.toUserModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser == null || existingUser.passwordHash != password) {
                return@withContext Result.failure(Exception("INVALID_LOGIN_CREDENTIALS"))
            }
            prefs.edit().putString("logged_in_uid", existingUser.uid).apply()
            Result.success(existingUser.toUserModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = userDao.getUserByEmail(email)
                ?: return@withContext Result.failure(Exception("INVALID_EMAIL"))
            // Simulate successful password reset email for offline SQLite test
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        prefs.edit().remove("logged_in_uid").apply()
    }
}
