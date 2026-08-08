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
    val isMr = language == AppLanguage.MARATHI

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
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

    // Validation Rules
    val isNameValid = name.trim().length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
    val nameError = if (hasAttemptedSubmit && !isNameValid) {
        if (isMr) "नाव किमान २ अक्षरांचे आणि फक्त अक्षरे असावे"
        else if (isHi) "नाम कम से कम 2 अक्षरों का और केवल अक्षर होना चाहिए"
        else "Name must be at least 2 characters with letters only"
    } else null

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val isEmailValid = email.trim().matches(emailRegex)
    val emailError = if (hasAttemptedSubmit && !isEmailValid) {
        if (isMr) "कृपया वैध ईमेल पत्ता टाका (उदा. user@domain.com)"
        else if (isHi) "कृपया वैध ईमेल पता दर्ज करें (उदा. user@domain.com)"
        else "Please enter a valid email address"
    } else null

    val phoneRegex = "^[6-9]\\d{9}$".toRegex()
    val isPhoneValid = phone.trim().matches(phoneRegex)
    val phoneError = if (hasAttemptedSubmit && !isPhoneValid) {
        if (isMr) "१० अंकी वैध मोबाइल नंबर टाका (६-९ ने सुरू)"
        else if (isHi) "10 अंकों का वैध मोबाइल नंबर दर्ज करें (6-9 से शुरू)"
        else "Enter a valid 10-digit mobile number (starts with 6-9)"
    } else null

    val isPasswordValid = password.length >= 6
    val passwordError = if (hasAttemptedSubmit && !isPasswordValid) {
        if (isMr) "पासवर्ड किमान ६ वर्णांचा असावा"
        else if (isHi) "पासवर्ड कम से कम 6 अक्षरों का होना चाहिए"
        else "Password must be at least 6 characters"
    } else null

    val isConfirmPasswordValid = confirmPassword == password && confirmPassword.isNotEmpty()
    val confirmPasswordError = if (hasAttemptedSubmit && !isConfirmPasswordValid) {
        if (isMr) "पासवर्ड जुळत नाहीत"
        else if (isHi) "पासवर्ड मेल नहीं खा रहे हैं"
        else "Passwords do not match"
    } else null

    val isTermsValid = agreedToTerms
    val termsError = if (hasAttemptedSubmit && !isTermsValid) {
        if (isMr) "कृपया नियम व अटी स्वीकारा"
        else if (isHi) "कृपया नियम एवं शर्तों को स्वीकार करें"
        else "Please agree to terms and conditions"
    } else null

    val isFormValid = isNameValid && isEmailValid && isPhoneValid && isPasswordValid && isConfirmPasswordValid && isTermsValid

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
        1 -> if (isMr) "अतिशय कमकुवत" else if (isHi) "बहुत कमज़ोर" else "Too Weak"
        2 -> if (isMr) "कमकुवत" else if (isHi) "कमज़ोर" else "Weak"
        3 -> if (isMr) "चांगला" else if (isHi) "अच्छा" else "Good"
        4 -> if (isMr) "मजबूत" else if (isHi) "मज़बूत" else "Strong"
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
                    text = if (isMr) "नवीन खाते तयार करा" else if (isHi) "नया खाता बनाएं" else "Create Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isMr) "शेतकरी कुटुंबात सामील व्हा" else if (isHi) "किसान समुदाय से जुड़ें" else "Join the Farmer Community",
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
                    // Top Error Banner
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
                                    text = errorMessage ?: if (isMr) "कृपया लाल रंगातील त्रुटी तपासून योग्य माहिती भरा" else if (isHi) "कृपया लाल रंग वाली त्रुटियां ठीक करें" else "Please fix the highlighted errors below",
                                    fontSize = 12.sp,
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Medium,
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

                    // Full Name Field with Validation
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        isError = nameError != null,
                        label = { Text(if (isMr) "पूर्ण नाव *" else if (isHi) "पूरा नाम *" else "Full Name *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = if (nameError != null) Color(0xFFDC2626) else Color(0xFF059669))
                        },
                        trailingIcon = {
                            if (nameError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626))
                            } else if (name.trim().length >= 2) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF059669))
                            }
                        },
                        supportingText = {
                            if (nameError != null) {
                                Text(nameError, color = Color(0xFFDC2626), fontSize = 11.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Field with Validation
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        isError = emailError != null,
                        label = { Text(if (isMr) "ईमेल पत्ता *" else if (isHi) "ईमेल *" else "Email Address *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = if (emailError != null) Color(0xFFDC2626) else Color(0xFF059669))
                        },
                        trailingIcon = {
                            if (emailError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626))
                            } else if (email.matches(emailRegex)) {
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
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Phone Field with Validation
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) onPhoneChange(it) },
                        isError = phoneError != null,
                        label = { Text(if (isMr) "१० अंकी मोबाइल नंबर *" else if (isHi) "10 अंकीय मोबाइल नंबर *" else "10-Digit Mobile Number *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = if (phoneError != null) Color(0xFFDC2626) else Color(0xFF059669))
                        },
                        trailingIcon = {
                            if (phoneError != null) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color(0xFFDC2626))
                            } else if (phone.matches(phoneRegex)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF059669))
                            }
                        },
                        supportingText = {
                            if (phoneError != null) {
                                Text(phoneError, color = Color(0xFFDC2626), fontSize = 11.sp)
                            } else {
                                Text("${phone.length}/10 digits", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
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
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // State Dropdown
                    ExposedDropdownMenuBox(
                        expanded = stateDropdownExpanded,
                        onExpandedChange = { stateDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedState,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isMr) "राज्य" else if (isHi) "राज्य" else "State") },
                            leadingIcon = {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF059669))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateDropdownExpanded) },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF059669)
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Field with Validation
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        isError = passwordError != null,
                        label = { Text(if (isMr) "पासवर्ड *" else if (isHi) "पासवर्ड *" else "Password *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = if (passwordError != null) Color(0xFFDC2626) else Color(0xFF059669))
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
                        supportingText = {
                            if (passwordError != null) {
                                Text(passwordError, color = Color(0xFFDC2626), fontSize = 11.sp)
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
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password strength indicator
                    if (password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f)) {
                                for (i in 1..4) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .padding(horizontal = 2.dp)
                                            .background(
                                                color = if (i <= passwordStrength) strengthColor else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strengthLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = strengthColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password with Match Validation
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        isError = confirmPasswordError != null,
                        label = { Text(if (isMr) "पासवर्ड पुन्हा टाका *" else if (isHi) "पासवर्ड की पुष्टि करें *" else "Confirm Password *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = if (confirmPasswordError != null) Color(0xFFDC2626) else Color(0xFF059669))
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
                        supportingText = {
                            if (confirmPasswordError != null) {
                                Text(confirmPasswordError, color = Color(0xFFDC2626), fontSize = 11.sp)
                            } else if (confirmPassword.isNotEmpty() && confirmPassword == password) {
                                Text(if (isMr) "✓ पासवर्ड जुळला" else if (isHi) "✓ पासवर्ड मेल खा गया" else "✓ Passwords match", color = Color(0xFF059669), fontSize = 11.sp)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            hasAttemptedSubmit = true
                            if (isFormValid) onSignUp()
                        }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            errorBorderColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Terms & conditions checkbox with error feedback
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { agreedToTerms = !agreedToTerms }
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF059669),
                                uncheckedColor = if (termsError != null) Color(0xFFDC2626) else Color(0xFF94A3B8)
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMr) "मी सर्व नियम व अटी मान्य करतो *" else if (isHi) "मैं नियम एवं शर्तों से सहमत हूँ *" else "I agree to Terms & Conditions *",
                            fontSize = 12.sp,
                            color = if (termsError != null) Color(0xFFDC2626) else Color(0xFF475569)
                        )
                    }
                    if (termsError != null) {
                        Text(
                            text = termsError,
                            fontSize = 11.sp,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sign up button with live validation gate
                    Button(
                        onClick = {
                            hasAttemptedSubmit = true
                            if (isFormValid) {
                                onSignUp()
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
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isMr) "खाते तयार करा" else if (isHi) "खाता बनाएं" else "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigate to login
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isMr) "आधीच खाते आहे? " else if (isHi) "पहले से खाता है? " else "Already have an account? ",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = if (isMr) "लॉगिन करा" else if (isHi) "लॉग इन करें" else "Log In",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
