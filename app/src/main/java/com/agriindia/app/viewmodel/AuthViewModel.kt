package com.agriindia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agriindia.app.model.AuthState
import com.agriindia.app.model.User
import com.agriindia.app.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepository(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Form fields
    private val _loginEmail = MutableStateFlow("")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _signUpName = MutableStateFlow("")
    val signUpName: StateFlow<String> = _signUpName.asStateFlow()

    private val _signUpEmail = MutableStateFlow("")
    val signUpEmail: StateFlow<String> = _signUpEmail.asStateFlow()

    private val _signUpPhone = MutableStateFlow("")
    val signUpPhone: StateFlow<String> = _signUpPhone.asStateFlow()

    private val _signUpPassword = MutableStateFlow("")
    val signUpPassword: StateFlow<String> = _signUpPassword.asStateFlow()

    private val _signUpConfirmPassword = MutableStateFlow("")
    val signUpConfirmPassword: StateFlow<String> = _signUpConfirmPassword.asStateFlow()

    private val _signUpState = MutableStateFlow("")
    val signUpState: StateFlow<String> = _signUpState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _resetPasswordSent = MutableStateFlow(false)
    val resetPasswordSent: StateFlow<Boolean> = _resetPasswordSent.asStateFlow()

    // Navigation state
    private val _showSignUp = MutableStateFlow(false)
    val showSignUp: StateFlow<Boolean> = _showSignUp.asStateFlow()

    private val _showProfile = MutableStateFlow(false)
    val showProfile: StateFlow<Boolean> = _showProfile.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    // Form update methods
    fun updateLoginEmail(email: String) { _loginEmail.value = email }
    fun updateLoginPassword(password: String) { _loginPassword.value = password }
    fun updateSignUpName(name: String) { _signUpName.value = name }
    fun updateSignUpEmail(email: String) { _signUpEmail.value = email }
    fun updateSignUpPhone(phone: String) { _signUpPhone.value = phone }
    fun updateSignUpPassword(password: String) { _signUpPassword.value = password }
    fun updateSignUpConfirmPassword(password: String) { _signUpConfirmPassword.value = password }
    fun updateSignUpState(state: String) { _signUpState.value = state }

    fun navigateToSignUp() { _showSignUp.value = true }
    fun navigateToLogin() { _showSignUp.value = false }
    fun toggleProfile(show: Boolean) { _showProfile.value = show }
    fun clearError() { _errorMessage.value = null }

    fun login() {
        val email = _loginEmail.value.trim()
        val password = _loginPassword.value

        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }

        if (!email.equals("admin", ignoreCase = true) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signIn(email, password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    _loginEmail.value = ""
                    _loginPassword.value = ""
                },
                onFailure = { e ->
                    _errorMessage.value = formatAuthError(e.message ?: "Login failed")
                    _authState.value = AuthState.Error(_errorMessage.value ?: "Login failed")
                }
            )
            _isLoading.value = false
        }
    }

    fun signUp() {
        val name = _signUpName.value.trim()
        val email = _signUpEmail.value.trim()
        val phone = _signUpPhone.value.trim()
        val state = _signUpState.value.trim()
        val password = _signUpPassword.value
        val confirmPassword = _signUpConfirmPassword.value

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Please fill in all required fields"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signUp(name, email, phone, state, password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    // Clear form
                    _signUpName.value = ""
                    _signUpEmail.value = ""
                    _signUpPhone.value = ""
                    _signUpPassword.value = ""
                    _signUpConfirmPassword.value = ""
                    _signUpState.value = ""
                    _showSignUp.value = false
                },
                onFailure = { e ->
                    _errorMessage.value = formatAuthError(e.message ?: "Sign up failed")
                }
            )
            _isLoading.value = false
        }
    }

    fun resetPassword() {
        val email = _loginEmail.value.trim()
        if (email.isEmpty()) {
            _errorMessage.value = "Please enter your email address first"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.resetPassword(email)
            result.fold(
                onSuccess = {
                    _resetPasswordSent.value = true
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = formatAuthError(e.message ?: "Failed to send reset email")
                }
            )
            _isLoading.value = false
        }
    }

    fun dismissResetPasswordDialog() {
        _resetPasswordSent.value = false
    }

    fun logout() {
        authRepository.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
        _showProfile.value = false
    }

    private fun formatAuthError(message: String): String {
        return when {
            message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            message.contains("INVALID_EMAIL", ignoreCase = true) ||
            message.contains("wrong password", ignoreCase = true) ->
                "Invalid email or password. Please try again."
            message.contains("EMAIL_EXISTS", ignoreCase = true) ||
            message.contains("already in use", ignoreCase = true) ->
                "An account with this email already exists."
            message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Password is too weak. Use at least 6 characters."
            message.contains("TOO_MANY_ATTEMPTS", ignoreCase = true) ->
                "Too many failed attempts. Please try again later."
            message.contains("NETWORK", ignoreCase = true) ->
                "Network error. Please check your internet connection."
            else -> message
        }
    }
}
