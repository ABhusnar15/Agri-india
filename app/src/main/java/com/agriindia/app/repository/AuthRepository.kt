package com.agriindia.app.repository

import android.content.Context
import android.content.SharedPreferences
import com.agriindia.app.data.AppDatabase
import com.agriindia.app.data.UserEntity
import com.agriindia.app.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository(private val context: Context) {

    private val userDao by lazy { AppDatabase.getDatabase(context).userDao() }
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("agri_auth_prefs", Context.MODE_PRIVATE)
    }

    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        val firebaseUser = try { firebaseAuth?.currentUser } catch (e: Exception) { null }
        if (firebaseUser != null) {
            val localUser = userDao.getUserById(firebaseUser.uid)
            if (localUser != null) {
                return@withContext localUser.toUserModel()
            }
            return@withContext User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "Kisan Member",
                email = firebaseUser.email ?: "",
                phone = firebaseUser.phoneNumber ?: "",
                state = "Punjab"
            )
        }

        val uid = prefs.getString("logged_in_uid", null) ?: return@withContext null
        userDao.getUserById(uid)?.toUserModel()
    }

    fun isLoggedIn(): Boolean {
        val hasFirebaseUser = try { firebaseAuth?.currentUser != null } catch (e: Exception) { false }
        return hasFirebaseUser || prefs.getString("logged_in_uid", null) != null
    }

    suspend fun signUp(
        name: String,
        email: String,
        phone: String,
        state: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        // Try Firebase Auth first
        val auth = firebaseAuth
        if (auth != null) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val entity = UserEntity(
                        uid = firebaseUser.uid,
                        email = email,
                        passwordHash = password,
                        name = name,
                        phone = phone,
                        state = state,
                        profileImageUrl = null
                    )
                    userDao.insertUser(entity)
                    prefs.edit().putString("logged_in_uid", firebaseUser.uid).apply()
                    return@withContext Result.success(entity.toUserModel())
                }
            } catch (e: Exception) {
                // If email already exists in Firebase, return error; else fallback to Room
                if (e.message?.contains("email address is already in use", ignoreCase = true) == true) {
                    return@withContext Result.failure(Exception("EMAIL_EXISTS"))
                }
            }
        }

        // Fallback to Room DB
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
        // Try Firebase Auth first
        val auth = firebaseAuth
        if (auth != null) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val localUser = userDao.getUserById(firebaseUser.uid)
                    val userModel = localUser?.toUserModel() ?: User(
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Kisan Member",
                        email = email,
                        phone = "",
                        state = "Punjab"
                    )
                    prefs.edit().putString("logged_in_uid", firebaseUser.uid).apply()
                    return@withContext Result.success(userModel)
                }
            } catch (e: Exception) {
                if (e.message?.contains("password is invalid", ignoreCase = true) == true ||
                    e.message?.contains("no user record", ignoreCase = true) == true) {
                    return@withContext Result.failure(Exception("INVALID_LOGIN_CREDENTIALS"))
                }
            }
        }

        // Fallback to Room DB
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
        val auth = firebaseAuth
        if (auth != null) {
            try {
                auth.sendPasswordResetEmail(email).await()
                return@withContext Result.success(Unit)
            } catch (e: Exception) {
                // Fall back to offline check if needed
            }
        }

        try {
            if (userDao.getUserByEmail(email) == null) {
                return@withContext Result.failure(Exception("INVALID_EMAIL"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            // Ignore
        }
        prefs.edit().remove("logged_in_uid").apply()
    }
}
