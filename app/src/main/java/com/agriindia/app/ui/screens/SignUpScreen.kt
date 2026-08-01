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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriindia.app.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    language: AppLanguage,
    name: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String,
    selectedState: String,
    isLoading: Boolean,
    errorMessage: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onSignUp: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    val isHi = language == AppLanguage.HINDI
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val indianStates = listOf(
        "Andhra Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
        "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka",
        "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
        "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan",
        "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh",
        "Uttarakhand", "West Bengal"
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Password strength calculation
    val passwordStrength = remember(password) {
        when {
            password.isEmpty() -> 0
            password.length < 6 -> 1
            password.length < 8 -> 2
            password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 4
            password.any { it.isDigit() } || password.any { it.isUpperCase() } -> 3
            else -> 2
        }
    }
    val strengthLabel = when (passwordStrength) {
        0 -> ""
        1 -> if (isHi) "बहुत कमज़ोर" else "Too Weak"
        2 -> if (isHi) "कमज़ोर" else "Weak"
        3 -> if (isHi) "अच्छा" else "Good"
        4 -> if (isHi) "मज़बूत" else "Strong"
        else -> ""
    }
    val strengthColor = when (passwordStrength) {
        1 -> Color(0xFFDC2626)
        2 -> Color(0xFFEA580C)
        3 -> Color(0xFFD97706)
        4 -> Color(0xFF059669)
        else -> Color.Transparent
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
            .verticalScroll(rememberScrollState())
    ) {
        // Compact green header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isHi) "नया खाता बनाएं" else "Create Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isHi) "किसान समुदाय से जुड़ें" else "Join the Farmer Community",
                    fontSize = 13.sp,
                    color = Color(0xFFD1FAE5)
                )
            }
        }

        // Sign up form card
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text(if (isHi) "पूरा नाम *" else "Full Name *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF059669))
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text(if (isHi) "ईमेल *" else "Email Address *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = Color(0xFF059669))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = onPhoneChange,
                        label = { Text(if (isHi) "मोबाइल नंबर" else "Phone Number") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = Color(0xFF059669))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // State dropdown
                    ExposedDropdownMenuBox(
                        expanded = stateDropdownExpanded,
                        onExpandedChange = { stateDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedState,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isHi) "राज्य" else "State") },
                            leadingIcon = {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF059669))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateDropdownExpanded) },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF059669),
                                focusedLabelColor = Color(0xFF059669),
                                cursorColor = Color(0xFF059669)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = stateDropdownExpanded,
                            onDismissRequest = { stateDropdownExpanded = false }
                        ) {
                            indianStates.forEach { state ->
                                DropdownMenuItem(
                                    text = { Text(state, fontSize = 14.sp) },
                                    onClick = {
                                        onStateChange(state)
                                        stateDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text(if (isHi) "पासवर्ड *" else "Password *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF059669))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password strength indicator
                    if (password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Strength bars
                            repeat(4) { index ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .padding(horizontal = 2.dp)
                                        .background(
                                            if (index < passwordStrength) strengthColor else Color(0xFFE2E8F0),
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strengthLabel,
                                fontSize = 11.sp,
                                color = strengthColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = { Text(if (isHi) "पासवर्ड पुष्टि *" else "Confirm Password *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.LockReset, contentDescription = null, tint = Color(0xFF059669))
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { if (agreedToTerms) onSignUp() }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            focusedLabelColor = Color(0xFF059669),
                            cursorColor = Color(0xFF059669)
                        ),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        supportingText = if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                            { Text(if (isHi) "पासवर्ड मेल नहीं खाते" else "Passwords don't match", color = Color(0xFFDC2626)) }
                        } else null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Terms checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF059669),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = if (isHi) "मैं नियम और शर्तों से सहमत हूँ" else "I agree to the Terms & Conditions",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign Up button
                    Button(
                        onClick = onSignUp,
                        enabled = !isLoading && agreedToTerms,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF059669),
                            disabledContainerColor = Color(0xFF059669).copy(alpha = 0.4f)
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
                                imageVector = Icons.Default.HowToReg,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHi) "रजिस्टर करें" else "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Login link
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHi) "पहले से खाता है? " else "Already have an account? ",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = if (isHi) "लॉगिन करें" else "Sign In",
                fontSize = 14.sp,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
