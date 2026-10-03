package com.example.ui.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.PhoneKhojViewModel
import kotlin.math.cos
import kotlin.math.sin

// Light Theme Palette
private val LightBgBase = Color(0xFFF8FAFC)
private val LightCardSurface = Color(0xF7FFFFFF)
private val LightBorder = Color(0x332563EB)
private val LightTextPrimary = Color(0xFF0F172A)
private val LightTextSecondary = Color(0xFF475569)
private val LightTextMuted = Color(0xFF94A3B8)
private val LightInputBg = Color(0xFFF1F5F9)
private val ElectricAzure = Color(0xFF2563EB)
private val IndigoAdmin = Color(0xFF4F46E5)
private val CyberTeal = Color(0xFF0D9488)
private val VividViolet = Color(0xFF7C3AED)
private val RoseCoral = Color(0xFFE11D48)
private val EmeraldSuccess = Color(0xFF059669)

@Composable
fun AuthContainer(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authScreenState.collectAsState()
    val error by viewModel.authErrorMessage.collectAsState()
    val success by viewModel.authSuccessMessage.collectAsState()
    val loading by viewModel.authLoading.collectAsState()

    // 0: Consumer, 1: Seller
    var selectedRoleIndex by remember { mutableStateOf(0) }
    // "LOGIN", "SIGNUP"
    var authMode by remember { mutableStateOf("LOGIN") }

    // DYNAMIC COLOR ANIMATION (Light Theme Animated Mesh Gradient)
    val infiniteTransition = rememberInfiniteTransition(label = "auth_bg_transition")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim_progress"
    )

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightBgBase)
    ) {
        // Shifting glowing color orbs in Light Theme
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0FDF4),
                        Color(0xFFEFF6FF),
                        Color(0xFFFAF5FF),
                        Color(0xFFFFF1F2)
                    )
                )
            )

            val radAngle = Math.toRadians(angle.toDouble())

            val orb1X = width * 0.25f + (cos(radAngle) * 120).toFloat()
            val orb1Y = height * 0.20f + (sin(radAngle) * 90).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x5538BDF8), Color(0x0038BDF8)),
                    center = Offset(orb1X, orb1Y),
                    radius = width * 0.65f
                ),
                center = Offset(orb1X, orb1Y),
                radius = width * 0.65f
            )

            val orb2X = width * 0.80f - (sin(radAngle) * 110).toFloat()
            val orb2Y = height * 0.35f + (cos(radAngle) * 100).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x40A855F7), Color(0x00A855F7)),
                    center = Offset(orb2X, orb2Y),
                    radius = width * 0.60f
                ),
                center = Offset(orb2X, orb2Y),
                radius = width * 0.60f
            )

            val orb3X = width * 0.15f + (sin(radAngle * 1.2) * 90).toFloat()
            val orb3Y = height * 0.75f - (cos(radAngle * 1.2) * 80).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x4034D399), Color(0x0034D399)),
                    center = Offset(orb3X, orb3Y),
                    radius = width * 0.55f
                ),
                center = Offset(orb3X, orb3Y),
                radius = width * 0.55f
            )
        }

        // Top Navigation Header with Brand on Left and Admin Login on Top Right Corner
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Logo",
                                tint = ElectricAzure,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RefurbIQ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }

                // RIGHT SIDE CORNER TOP: ADMIN LOGIN BUTTON
                Surface(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (authState == "ADMIN_LOGIN") {
                                viewModel.authScreenState.value = "LOGIN"
                            } else {
                                viewModel.authScreenState.value = "ADMIN_LOGIN"
                            }
                        }
                        .border(1.dp, IndigoAdmin.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = if (authState == "ADMIN_LOGIN") IndigoAdmin else Color(0xFFEEF2FF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Admin Portal",
                            tint = if (authState == "ADMIN_LOGIN") Color.White else IndigoAdmin,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (authState == "ADMIN_LOGIN") "User Login" else "Admin Login",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (authState == "ADMIN_LOGIN") Color.White else IndigoAdmin
                        )
                    }
                }
            }

            // Scrollable Forms Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Logo Header
                AnimatedRefurbIqHeader(animProgress = animProgress)

                Spacer(modifier = Modifier.height(16.dp))

                // Error or Success Banner
                if (error != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚠️ ${error ?: ""}", color = RoseCoral, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (success != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = success ?: "", color = EmeraldSuccess, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // If in Admin Login Mode
                if (authState == "ADMIN_LOGIN") {
                    AdminLoginCard(
                        viewModel = viewModel,
                        isLoading = loading,
                        animProgress = animProgress,
                        onBackToUser = { viewModel.authScreenState.value = "LOGIN" }
                    )
                } else if (authState == "FORGOT_PASSWORD") {
                    LightForgotPasswordCard(
                        viewModel = viewModel,
                        isLoading = loading,
                        animProgress = animProgress,
                        onNavigateLogin = { viewModel.authScreenState.value = "LOGIN" }
                    )
                } else {
                    // 2-PERSON ROLE SELECTOR: CONSUMER vs SELLER
                    RoleSelectorPill(
                        selectedRole = selectedRoleIndex,
                        onSelectRole = { selectedRoleIndex = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // MODE SWITCHER: SIGN IN vs SIGN UP
                    AuthModeSwitcher(
                        mode = authMode,
                        onSelectMode = { authMode = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (authMode == "LOGIN") {
                        if (selectedRoleIndex == 0) {
                            ConsumerLoginCard(
                                viewModel = viewModel,
                                isLoading = loading,
                                animProgress = animProgress,
                                onNavigateForgot = { viewModel.authScreenState.value = "FORGOT_PASSWORD" },
                                onSwitchToSignUp = { authMode = "SIGNUP" }
                            )
                        } else {
                            SellerLoginCard(
                                viewModel = viewModel,
                                isLoading = loading,
                                animProgress = animProgress,
                                onNavigateForgot = { viewModel.authScreenState.value = "FORGOT_PASSWORD" },
                                onSwitchToSignUp = { authMode = "SIGNUP" }
                            )
                        }
                    } else {
                        if (selectedRoleIndex == 0) {
                            ConsumerSignUpCard(
                                viewModel = viewModel,
                                isLoading = loading,
                                animProgress = animProgress,
                                onSwitchToLogin = { authMode = "LOGIN" }
                            )
                        } else {
                            SellerSignUpCard(
                                viewModel = viewModel,
                                isLoading = loading,
                                animProgress = animProgress,
                                onSwitchToLogin = { authMode = "LOGIN" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Trust Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xCCFFFFFF))
                        .border(1.dp, Color(0x3394A3B8), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeaturePill(icon = Icons.Default.Security, label = "Sanchar Saathi Live")
                    FeaturePill(icon = Icons.Default.Shield, label = "CEIR Anti-Theft")
                    FeaturePill(icon = Icons.Default.Storage, label = "Offline SQLite Room")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RoleSelectorPill(
    selectedRole: Int,
    onSelectRole: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Consumer Tab
            val isConsumer = selectedRole == 0
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectRole(0) },
                shape = RoundedCornerShape(12.dp),
                color = if (isConsumer) Color(0xFFEFF6FF) else Color.Transparent,
                border = if (isConsumer) androidx.compose.foundation.BorderStroke(1.5.dp, ElectricAzure) else null
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = if (isConsumer) ElectricAzure else LightTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Consumer (Buyer)",
                        fontSize = 13.sp,
                        fontWeight = if (isConsumer) FontWeight.Bold else FontWeight.Medium,
                        color = if (isConsumer) ElectricAzure else LightTextSecondary
                    )
                }
            }

            // Seller Tab
            val isSeller = selectedRole == 1
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectRole(1) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSeller) Color(0xFFEEF2FF) else Color.Transparent,
                border = if (isSeller) androidx.compose.foundation.BorderStroke(1.5.dp, IndigoAdmin) else null
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        tint = if (isSeller) IndigoAdmin else LightTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Seller (Shopkeeper)",
                        fontSize = 13.sp,
                        fontWeight = if (isSeller) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSeller) IndigoAdmin else LightTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AuthModeSwitcher(
    mode: String,
    onSelectMode: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF1F5F9))
                .padding(3.dp)
        ) {
            val isLogin = mode == "LOGIN"
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSelectMode("LOGIN") },
                shape = RoundedCornerShape(18.dp),
                color = if (isLogin) Color.White else Color.Transparent,
                shadowElevation = if (isLogin) 2.dp else 0.dp
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 12.sp,
                    fontWeight = if (isLogin) FontWeight.Bold else FontWeight.Medium,
                    color = if (isLogin) LightTextPrimary else LightTextMuted,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }

            val isSignUp = mode == "SIGNUP"
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSelectMode("SIGNUP") },
                shape = RoundedCornerShape(18.dp),
                color = if (isSignUp) Color.White else Color.Transparent,
                shadowElevation = if (isSignUp) 2.dp else 0.dp
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 12.sp,
                    fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSignUp) LightTextPrimary else LightTextMuted,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AnimatedRefurbIqHeader(animProgress: Float) {
    val gradientShift = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2563EB),
            Color(0xFF7C3AED),
            Color(0xFF06B6D4),
            Color(0xFF2563EB)
        ),
        start = Offset(animProgress * 300f, 0f),
        end = Offset((animProgress + 1f) * 300f, 300f)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(12.dp, CircleShape, spotColor = ElectricAzure)
                .clip(CircleShape)
                .background(gradientShift)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "RefurbIQ",
                    tint = ElectricAzure,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "RefurbIQ",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = LightTextPrimary,
            letterSpacing = (-0.5).sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "Certified Mobile Platform", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricAzure)
            Text(text = "•", fontSize = 12.sp, color = LightTextMuted)
            Text(text = "DoT Sanchar Saathi Integrated", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = LightTextSecondary)
        }
    }
}

// -------------------------------------------------------------
// CONSUMER LOGIN CARD
// -------------------------------------------------------------
@Composable
fun ConsumerLoginCard(
    viewModel: PhoneKhojViewModel,
    isLoading: Boolean,
    animProgress: Float,
    onNavigateForgot: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("consumer@refurbiq.in") }
    var password by remember { mutableStateOf("RefurbIQ2026!") }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x222563EB)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ElectricAzure, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Consumer Login", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = LightTextPrimary)
            }
            Text(text = "Sign in to inspect IMEIs, check reports & buy verified phones", fontSize = 12.sp, color = LightTextSecondary)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email or Mobile Number") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ElectricAzure) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricAzure) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = LightTextMuted)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign In Action Button
            AnimatedActionButton(
                text = "Sign In as Consumer",
                isLoading = isLoading,
                animProgress = animProgress,
                onClick = { viewModel.login(email, password) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Demo Consumer 1-Tap Fill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        email = "consumer@refurbiq.in"
                        password = "RefurbIQ2026!"
                        viewModel.login("consumer@refurbiq.in", "RefurbIQ2026!")
                    },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = ElectricAzure, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "1-Tap Demo Consumer Login", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricAzure)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onNavigateForgot) {
                    Text(text = "Forgot Password?", color = LightTextSecondary, fontSize = 12.sp)
                }
                TextButton(onClick = onSwitchToSignUp) {
                    Text(text = "New user? Create Account", color = ElectricAzure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SELLER LOGIN CARD
// -------------------------------------------------------------
@Composable
fun SellerLoginCard(
    viewModel: PhoneKhojViewModel,
    isLoading: Boolean,
    animProgress: Float,
    onNavigateForgot: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("apex.shop@refurbiq.in") }
    var password by remember { mutableStateOf("SmartRefurb2026!") }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x224F46E5)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, contentDescription = null, tint = IndigoAdmin, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Seller / Merchant Login", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = LightTextPrimary)
            }
            Text(text = "Manage store inventory, 2-min QC scans & GST invoices", fontSize = 12.sp, color = LightTextSecondary)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Shop Business Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoAdmin) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = LightTextMuted)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedActionButton(
                text = "Sign In as Seller",
                isLoading = isLoading,
                animProgress = animProgress,
                onClick = { viewModel.login(email, password) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1-Tap Demo Seller Login
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        email = "apex.shop@refurbiq.in"
                        password = "SmartRefurb2026!"
                        viewModel.login("apex.shop@refurbiq.in", "SmartRefurb2026!")
                    },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEEF2FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = IndigoAdmin, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "1-Tap Demo Seller Login (Apex Hub)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IndigoAdmin)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onNavigateForgot) {
                    Text(text = "Forgot Password?", color = LightTextSecondary, fontSize = 12.sp)
                }
                TextButton(onClick = onSwitchToSignUp) {
                    Text(text = "Register Shop", color = IndigoAdmin, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CONSUMER SIGN UP CARD
// -------------------------------------------------------------
@Composable
fun ConsumerSignUpCard(
    viewModel: PhoneKhojViewModel,
    isLoading: Boolean,
    animProgress: Float,
    onSwitchToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x222563EB)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(text = "Create Consumer Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LightTextPrimary)
            Text(text = "Access verified phones, free Sanchar Saathi certificates", fontSize = 12.sp, color = LightTextSecondary)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ElectricAzure) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Mobile Number (+91)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ElectricAzure) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ElectricAzure) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City / Region") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ElectricAzure) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (Min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricAzure) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedActionButton(
                text = "Register Consumer Profile",
                isLoading = isLoading,
                animProgress = animProgress,
                onClick = {
                    viewModel.register(
                        email = email,
                        pass = password,
                        shop = "Consumer Profile",
                        owner = name,
                        phone = phone,
                        addr = city,
                        role = "CONSUMER"
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onSwitchToLogin) {
                    Text(text = "Already have an account? Sign In", color = ElectricAzure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SELLER SIGN UP CARD
// -------------------------------------------------------------
@Composable
fun SellerSignUpCard(
    viewModel: PhoneKhojViewModel,
    isLoading: Boolean,
    animProgress: Float,
    onSwitchToLogin: () -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gst by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x224F46E5)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(text = "Register Mobile Store", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LightTextPrimary)
            Text(text = "Join India's certified refurbished mobile shop ecosystem", fontSize = 12.sp, color = LightTextSecondary)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop / Business Name") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("Owner / Proprietor Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Business Mobile (+91)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = IndigoAdmin) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = gst,
                onValueChange = { gst = it },
                label = { Text("GSTIN / Trade License (Optional)") },
                leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Shop Address & City") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Login Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (Min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoAdmin) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedActionButton(
                text = "Register Merchant Account",
                isLoading = isLoading,
                animProgress = animProgress,
                onClick = {
                    viewModel.register(
                        email = email,
                        pass = password,
                        shop = shopName,
                        owner = ownerName,
                        phone = phone,
                        addr = address,
                        role = "SELLER"
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onSwitchToLogin) {
                    Text(text = "Already registered? Sign In", color = IndigoAdmin, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TOP-RIGHT CORNER ADMIN LOGIN CARD
// -------------------------------------------------------------
@Composable
fun AdminLoginCard(
    viewModel: PhoneKhojViewModel,
    isLoading: Boolean,
    animProgress: Float,
    onBackToUser: () -> Unit
) {
    var adminKey by remember { mutableStateOf("admin@refurbiq.gov.in") }
    var masterPin by remember { mutableStateOf("AdminAudit2026!") }
    var pinVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0x334F46E5)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6366F1))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = Color(0xFFEEF2FF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = IndigoAdmin, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Admin Regulatory Console", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LightTextPrimary)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEEF2FF)
                ) {
                    Text(text = "CENTRAL NODE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = IndigoAdmin, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Central authority oversight: CEIR blacklist management & merchant database audit.", fontSize = 11.sp, color = LightTextSecondary)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = adminKey,
                onValueChange = { adminKey = it },
                label = { Text("Regulatory Admin Key / ID") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = IndigoAdmin) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = masterPin,
                onValueChange = { masterPin = it },
                label = { Text("Master Clearance PIN") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoAdmin) },
                trailingIcon = {
                    IconButton(onClick = { pinVisible = !pinVisible }) {
                        Icon(if (pinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = LightTextMuted)
                    }
                },
                visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            AnimatedActionButton(
                text = "Authenticate As Admin",
                isLoading = isLoading,
                animProgress = animProgress,
                onClick = { viewModel.loginAsAdmin(adminKey, masterPin) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1-Tap Demo Admin Fill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        adminKey = "admin@refurbiq.gov.in"
                        masterPin = "AdminAudit2026!"
                        viewModel.loginAsAdmin("admin@refurbiq.gov.in", "AdminAudit2026!")
                    },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F3FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "1-Tap Demo Admin Access (DoT Desk)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onBackToUser) {
                    Text(text = "← Return to Consumer / Seller Login", color = LightTextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// FORGOT PASSWORD CARD
// -------------------------------------------------------------
@Composable
fun LightForgotPasswordCard(
    viewModel: PhoneKhojViewModel,
    isLoading: Boolean,
    animProgress: Float,
    onNavigateLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(text = "Reset Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LightTextPrimary)
            Text(text = "Enter your registered email and choose a new password", fontSize = 12.sp, color = LightTextSecondary)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Account Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ElectricAzure) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password (Min 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricAzure) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = defaultInputColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedActionButton(
                text = "Update Password",
                isLoading = isLoading,
                animProgress = animProgress,
                onClick = { viewModel.resetPassword(email, newPassword) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onNavigateLogin) {
                    Text(text = "Back to Login", color = ElectricAzure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnimatedActionButton(
    text: String,
    isLoading: Boolean,
    animProgress: Float,
    onClick: () -> Unit
) {
    val buttonBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2563EB),
            Color(0xFF4F46E5),
            Color(0xFF0D9488),
            Color(0xFF2563EB)
        ),
        start = Offset(animProgress * 250f, 0f),
        end = Offset((animProgress + 1f) * 250f, 250f)
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = ElectricAzure),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp),
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(buttonBrush, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text(text = text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FeaturePill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = ElectricAzure, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LightTextPrimary)
    }
}

@Composable
private fun defaultInputColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = LightInputBg,
    focusedBorderColor = ElectricAzure,
    unfocusedBorderColor = Color(0xFFCBD5E1),
    focusedLabelColor = ElectricAzure,
    unfocusedLabelColor = LightTextSecondary,
    focusedTextColor = LightTextPrimary,
    unfocusedTextColor = LightTextPrimary
)
