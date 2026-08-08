package com.agriindia.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    language: AppLanguage,
    email: String,
    password: String,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    onClearError: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    val isMr = language == AppLanguage.MARATHI

    var passwordVisible by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Validation
    val emailTrimmed = email.trim()
    val isEmailFormat = emailTrimmed.contains("@") && emailTrimmed.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex())
    val isPhoneFormat = emailTrimmed.matches("^[6-9]\\d{9}$".toRegex())
    val isUserIdentifierValid = emailTrimmed.isNotEmpty() && (isEmailFormat || isPhoneFormat)

    val emailError = if (hasAttemptedSubmit && !isUserIdentifierValid) {
        if (emailTrimmed.isEmpty()) {
            if (isMr) "कृपया ईमेल किंवा १० अंकी मोबाइल नंबर टाका"
            else if (isHi) "कृपया ईमेल या 10 अंकों का मोबाइल नंबर दर्ज करें"
            else "Please enter email or 10-digit phone number"
        } else {
            if (isMr) "अवैध ईमेल किंवा मोबाइल नंबर स्वरूप"
            else if (isHi) "अवैध ईमेल या मोबाइल नंबर प्रारूप"
            else "Invalid email or 10-digit mobile number"
        }
    } else null

    val isPasswordValid = password.length >= 6
    val passwordError = if (hasAttemptedSubmit && !isPasswordValid) {
        if (password.isEmpty()) {
            if (isMr) "कृपया पासवर्ड टाका"
            else if (isHi) "कृपया पासवर्ड दर्ज करें"
            else "Please enter your password"
        } else {
            if (isMr) "पासवर्ड किमान ६ वर्णांचा असावा"
            else if (isHi) "पासवर्ड कम से कम 6 अक्षरों का होना चाहिए"
            else "Password must be at least 6 characters"
        }
    } else null

    val isFormValid = isUserIdentifierValid && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
            .verticalScroll(rememberScrollState())
    ) {
        // Green gradient header with branding
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF059669),
                            Color(0xFF047857),
                            Color(0xFF065F46)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isMr) "अ‍ॅग्री इंडिया" else if (isHi) "एग्री इंडिया" else "AgriIndia",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isMr) "शेतकऱ्यांचा डिजिटल सोबती" else if (isHi) "किसान का डिजिटल साथी" else "Farmer's Digital Companion",
                    fontSize = 14.sp,
                    color = Color(0xFFD1FAE5)
                )
            }
        }

        // Login form card
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isMr) "आपल्या खात्यात प्रवेश करा" else if (isHi) "अपने खाते में प्रवेश करें" else "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = if (isMr) "लॉगिन करण्यासाठी खालील माहिती भरा" else if (isHi) "लॉग इन करने के लिए विवरण दर्ज करें" else "Sign in to continue to your account",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error banner
                    if (errorMessage != null || (hasAttemptedSubmit && !isFormValid)) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: if (isMr) "कृपया वैध ईमेल/मोबाइल व पासवर्ड टाका" else if (isHi) "कृपया वैध ईमेल/मोबाइल व पासवर्ड दर्ज करें" else "Please enter valid credentials",
                                    fontSize = 12.sp,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onClearError() }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Email / Phone field with real-time validation
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        isError = emailError != null,
                        label = { Text(if (isMr) "ईमेल किंवा मोबाइल नंबर *" else if (isHi) "ईमेल या मोबाइल नंबर *" else "Email or Mobile Number *") },
                        placeholder = { Text("farmer@agri.in / 9876543210", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = if (emailError != null) Color(0xFFDC2626) else Color(0xFF059669)
                            )
                        },
                        trailingIcon = {
                            if (emailError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626))
                            } else if (isUserIdentifierValid) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF059669))
                            }
                        },
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError, color = Color(0xFFDC2626), fontSize = 11.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password field with real-time validation
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        isError = passwordError != null,
                        label = { Text(if (isMr) "पासवर्ड *" else if (isHi) "पासवर्ड *" else "Password *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = if (passwordError != null) Color(0xFFDC2626) else Color(0xFF059669)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        supportingText = {
                            if (passwordError != null) {
                                Text(passwordError, color = Color(0xFFDC2626), fontSize = 11.sp)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                hasAttemptedSubmit = true
                                if (isFormValid) onLogin()
                            }
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Forgot password link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = if (isMr) "पासवर्ड विसरलात?" else if (isHi) "पासवर्ड भूल गए?" else "Forgot Password?",
                                fontSize = 13.sp,
                                color = Color(0xFF059669)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Login button with validation gate
                    Button(
                        onClick = {
                            hasAttemptedSubmit = true
                            if (isFormValid) {
                                onLogin()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF059669),
                            disabledContainerColor = Color(0xFF94A3B8)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isMr) "लॉगिन करा" else if (isHi) "लॉग इन करें" else "Log In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign up prompt
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isMr) "खाते नाही का? " else if (isHi) "खाता नहीं है? " else "Don't have an account? ",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = if (isMr) "नोंदणी करा" else if (isHi) "साइन अप करें" else "Sign Up",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            modifier = Modifier.clickable { onNavigateToSignUp() }
                        )
                    }
                }
            }
        }
    }
}
