package com.agriindia.app.repository

import com.agriindia.app.model.AuthState
import com.agriindia.app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun getCurrentUser(): User? {
        val fbUser = firebaseAuth.currentUser ?: return null
        return User(
            uid = fbUser.uid,
            name = fbUser.displayName ?: "",
            email = fbUser.email ?: "",
            phone = fbUser.phoneNumber ?: "",
            state = "",
            profileImageUrl = fbUser.photoUrl?.toString()
        )
    }

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun signUp(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val fbUser = authResult.user ?: return Result.failure(Exception("User creation failed"))

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            fbUser.updateProfile(profileUpdates).await()

            val user = User(
                uid = fbUser.uid,
                name = name,
                email = email,
                phone = "",
                state = "",
                profileImageUrl = null
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val fbUser = authResult.user ?: return Result.failure(Exception("Sign in failed"))

            val user = User(
                uid = fbUser.uid,
                name = fbUser.displayName ?: "",
                email = fbUser.email ?: "",
                phone = fbUser.phoneNumber ?: "",
                state = "",
                profileImageUrl = fbUser.photoUrl?.toString()
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
