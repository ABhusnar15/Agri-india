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
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

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
                // App icon
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
                    text = if (isHi) "एग्री इंडिया" else "AgriIndia",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isHi) "किसान का डिजिटल साथी" else "Farmer's Digital Companion",
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
                    .offset(y = (-24).dp)
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
                        text = if (isHi) "लॉगिन करें" else "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = if (isHi) "अपने खाते में लॉगिन करें" else "Sign in to your account",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Error message
                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(12.dp),
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
                                    text = errorMessage,
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

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text(if (isHi) "ईमेल" else "Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = Color(0xFF059669)
                            )
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
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text(if (isHi) "पासवर्ड" else "Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF059669)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onLogin() }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Forgot password
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (isHi) "पासवर्ड भूल गए?" else "Forgot Password?",
                            fontSize = 13.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onForgotPassword() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login button
                    Button(
                        onClick = onLogin,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF059669),
                            disabledContainerColor = Color(0xFF059669).copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHi) "लॉगिन करें" else "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign up link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHi) "नया खाता बनाएं? " else "Don't have an account? ",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = if (isHi) "रजिस्टर करें" else "Sign Up",
                fontSize = 14.sp,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
        }
    }
}
