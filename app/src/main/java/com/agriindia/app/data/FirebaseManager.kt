package com.agriindia.app.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

/**
 * Centralized Firebase Provider for AgriIndia Application.
 * Exposes Auth, Firestore, Cloud Storage, Analytics, and Cloud Functions.
 */
object FirebaseManager {

    private var isInitialized = false

    /**
     * Initializes Firebase App if not already initialized.
     */
    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    FirebaseApp.initializeApp(context)
                }
                isInitialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Firebase Authentication instance
     */
    val auth: FirebaseAuth? by lazy {
        try {
            Firebase.auth
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Cloud Firestore instance
     */
    val db: FirebaseFirestore? by lazy {
        try {
            Firebase.firestore
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Cloud Storage instance
     */
    val storage: FirebaseStorage? by lazy {
        try {
            Firebase.storage
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Firebase Analytics instance
     */
    val analytics: FirebaseAnalytics? by lazy {
        try {
            Firebase.analytics
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Cloud Functions instance
     */
    val functions: FirebaseFunctions? by lazy {
        try {
            Firebase.functions
        } catch (e: Exception) {
            null
        }
    }
}
