package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.EmeraldPassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightCardGlass
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.viewmodel.PhoneKhojViewModel

// -------------------------------------------------------------
// 1. SIMPLE COOKIE BANNER
// -------------------------------------------------------------
@Composable
fun CookieConsentBanner(
    isDismissed: Boolean,
    onAccept: () -> Unit,
    onCustomize: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isDismissed,
        enter = fadeIn() + scaleIn(initialScale = 0.95f),
        exit = fadeOut() + scaleOut(targetScale = 0.95f),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .border(1.dp, AzureBlue.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .testTag("cookie_consent_banner"),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cookie,
                            contentDescription = "Cookies",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Privacy & Local Telemetry Notice",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "RefurbIQ uses local SQLite and session tokens for CEIR Sanchar Saathi lookup, offline diagnostics, and valuation telemetry.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onCustomize,
                        modifier = Modifier.testTag("cookie_btn_customize")
                    ) {
                        Text("Essential Only", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = AzureBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("cookie_btn_accept")
                    ) {
                        Text("Accept All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. BACK TO THE TOP BUTTON
// -------------------------------------------------------------
@Composable
fun BackToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + scaleIn(),
        exit = fadeOut(tween(200)) + scaleOut(),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = AzureBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .size(46.dp)
                .testTag("btn_back_to_top")
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Back to Top",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 3. SCROLL PROGRESS BAR
// -------------------------------------------------------------
@Composable
fun ScrollProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp),
        color = CyberCyan,
        trackColor = Color.Transparent
    )
}

// -------------------------------------------------------------
// 4. COPY BUTTON WITH SYSTEM CLIPBOARD & TOAST
// -------------------------------------------------------------
@Composable
fun CopyIconButton(
    textToCopy: String,
    label: String = "Text",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, textToCopy)
            clipboard.setPrimaryClip(clip)
            isCopied = true
            Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
        },
        modifier = modifier.size(32.dp).testTag("copy_button_${label.lowercase().replace(" ", "_")}")
    ) {
        Icon(
            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
            contentDescription = "Copy $label",
            tint = if (isCopied) EmeraldPass else AzureBlue,
            modifier = Modifier.size(17.dp)
        )
    }
}

// -------------------------------------------------------------
// 5. SKIP TO CONTENT ACCESSIBILITY CHIP
// -------------------------------------------------------------
@Composable
fun SkipToContentChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = { Text("Skip to Content", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
        leadingIcon = {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.testTag("skip_to_content_chip")
    )
}

// -------------------------------------------------------------
// 6. MOBILE LOADING ANIMATION
// -------------------------------------------------------------
@Composable
fun MobileLoadingAnimation(
    statusText: String = "Analyzing Telemetry...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader_anim")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(64.dp)) {
                // Outer rotating ring
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(CyberCyan, AzureBlue, Color.Transparent)
                    ),
                    startAngle = angle,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            // Inner pulsing core
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(CyberCyan, AzureBlue)))
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = statusText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "RefurbIQ Hardware Telemetry Matrix",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// -------------------------------------------------------------
// 7. HOVER & PRESS INTERACTIVE CARD
// -------------------------------------------------------------
@Composable
fun HoverInteractiveBox(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (isHovered) 1.015f else 1f,
        animationSpec = tween(150),
        label = "hover_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .hoverable(interactionSource = interactionSource)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        content()
    }
}

// -------------------------------------------------------------
// 8. PASSIVE VISIT VISIBILITY TOGGLE (PRIVACY EYE)
// -------------------------------------------------------------
@Composable
fun PassiveVisitVisibilityToggle(
    isPassive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.testTag("passive_visit_toggle")
    ) {
        Icon(
            imageVector = if (isPassive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (isPassive) "Over-the-Counter Privacy Active" else "Standard View",
            tint = if (isPassive) AmberWarning else AzureBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Helper to mask sensitive values in passive mode
fun formatPassiveText(text: String, isPassive: Boolean, mask: String = "••••••"): String {
    return if (isPassive) mask else text
}

fun formatPassivePrice(price: Double, isPassive: Boolean): String {
    return if (isPassive) "₹ •••••" else "₹${price.toLong().toString().reversed().chunked(3).joinToString(",").reversed()}"
}

// -------------------------------------------------------------
// 9. STICKY CATEGORY HEADER
// -------------------------------------------------------------
@Composable
fun StickyCategoryHeader(
    title: String,
    count: Int? = null,
    lastUpdate: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AzureBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (count != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            if (lastUpdate != null) {
                Text(
                    text = "Updated: $lastUpdate",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 10. EXPANDABLE FAQ COMPONENT
// -------------------------------------------------------------
data class FaqItem(val question: String, val answer: String)

@Composable
fun ExpandableFaqSection(
    modifier: Modifier = Modifier
) {
    val faqs = remember {
        listOf(
            FaqItem(
                "How does DoT Sanchar Saathi CEIR verification work?",
                "RefurbIQ queries the Central Equipment Identity Register (CEIR) established by the Department of Telecommunications (DoT), Govt of India. Every 15-digit IMEI undergoes mathematical Luhn checksum validation and cross-checks the national blacklist for theft, lost devices, duplicate TACs, and clearance NOC."
            ),
            FaqItem(
                "What do RefurbIQ Condition Grades (A+, A, B, C) mean?",
                "• Grade A+ (Pristine): Flawless screen & housing, 100% hardware test score, battery health ≥ 88%.\n• Grade A (Very Good): Minor micro-scratches on frame only, 100% sensor pass, battery ≥ 82%.\n• Grade B (Fair): Normal cosmetic wear, small body dents, fully functional hardware certified.\n• Grade C: Requires parts refurb/screen replacement."
            ),
            FaqItem(
                "How is the Buyback / Trade-In Valuation calculated?",
                "The engine looks up current wholesale market benchmarks for the brand and model, applies tier multipliers for storage, and deducts calibrated refurbishment overheads for observed hardware defects (cracked screens, battery degradation, camera blur), leaving an optimal merchant profit margin."
            ),
            FaqItem(
                "What is covered under the 1-Month Shop Warranty?",
                "The digital warranty covers motherboard stability, display touchscreen responsiveness, cameras, microphone/speaker, and charging port. Physical drops, water immersion, and unauthorized disassembly void coverage."
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("expandable_faq_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = AzureBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RefurbIQ Knowledge Base & FAQs",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            faqs.forEachIndexed { index, faq ->
                var isExpanded by remember { mutableStateOf(index == 0) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { isExpanded = !isExpanded }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = faq.question,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = AzureBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = faq.answer,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 11. FLOATING CONTACT BUTTON & SUPPORT MODAL
// -------------------------------------------------------------
@Composable
fun FloatingContactFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = WhatsAppGreen,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = modifier
            .size(46.dp)
            .shadow(6.dp, CircleShape)
            .testTag("fab_floating_contact")
    ) {
        Icon(
            imageVector = Icons.Default.SupportAgent,
            contentDescription = "Support & Help",
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun ContactSupportModal(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = AzureBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RefurbIQ Support Desk", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Need technical assistance with CEIR Sanchar Saathi verification, trade-in valuations, or diagnostic logs?",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // WhatsApp Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCFCE7))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=919876543210&text=Hello%20RefurbIQ%20Support"))
                            context.startActivity(intent)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = WhatsAppGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("WhatsApp Technician Hotline", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF166534))
                        Text("+91 98765 43210 (Instant reply)", fontSize = 11.sp, color = Color(0xFF15803D))
                    }
                }
                // Email Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@refurbiq.gov.in"))
                            context.startActivity(intent)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = AzureBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Official Regulatory Email", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AzureBlue)
                        Text("support@refurbiq.gov.in", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                // Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Headquarters Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Connaught Place, New Delhi 110001", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AzureBlue)) {
                Text("Close", color = Color.White)
            }
        }
    )
}

// -------------------------------------------------------------
// 12. UTM CAMPAIGN TRACKING MODAL
// -------------------------------------------------------------
@Composable
fun UtmCampaignModal(
    viewModel: PhoneKhojViewModel,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return
    val utmSource by viewModel.utmSource.collectAsState()
    val utmMedium by viewModel.utmMedium.collectAsState()
    val utmCampaign by viewModel.utmCampaign.collectAsState()
    val utmTerm by viewModel.utmTerm.collectAsState()
    val utmContent by viewModel.utmContent.collectAsState()

    var tempSource by remember(utmSource) { mutableStateOf(utmSource) }
    var tempMedium by remember(utmMedium) { mutableStateOf(utmMedium) }
    var tempCampaign by remember(utmCampaign) { mutableStateOf(utmCampaign) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = AzureBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("UTM Campaign Attribution", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Track the marketing attribution parameters attached to customer intakes, trade-in leads, and digital invoices.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = tempSource,
                    onValueChange = { tempSource = it },
                    label = { Text("utm_source") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("utm_input_source")
                )
                OutlinedTextField(
                    value = tempMedium,
                    onValueChange = { tempMedium = it },
                    label = { Text("utm_medium") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("utm_input_medium")
                )
                OutlinedTextField(
                    value = tempCampaign,
                    onValueChange = { tempCampaign = it },
                    label = { Text("utm_campaign") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("utm_input_campaign")
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Current URL Tag: ?utm_source=$tempSource&utm_medium=$tempMedium&utm_campaign=$tempCampaign",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = AzureBlue,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.utmSource.value = tempSource
                    viewModel.utmMedium.value = tempMedium
                    viewModel.utmCampaign.value = tempCampaign
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AzureBlue),
                modifier = Modifier.testTag("utm_btn_save")
            ) {
                Text("Save Attribution", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -------------------------------------------------------------
// 13. GLOBAL CONFIRMATION MODAL
// -------------------------------------------------------------
@Composable
fun GlobalConfirmationModal(
    viewModel: PhoneKhojViewModel
) {
    val title by viewModel.confirmDialogTitle.collectAsState()
    val message by viewModel.confirmDialogMessage.collectAsState()

    if (title != null && message != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmation() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title ?: "Confirm Action", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(message ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.executeConfirmation() },
                    colors = ButtonDefaults.buttonColors(containerColor = AzureBlue),
                    modifier = Modifier.testTag("modal_btn_confirm")
                ) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissConfirmation() },
                    modifier = Modifier.testTag("modal_btn_cancel")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 14. MOBILE NAVIGATION DRAWER / MENU
// -------------------------------------------------------------
@Composable
fun MobileNavigationDrawerSheet(
    viewModel: PhoneKhojViewModel,
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return
    val userRole by viewModel.userRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val isPassive by viewModel.isPassiveVisitMode.collectAsState()
    val lastUpdate by viewModel.lastUpdateTimestamp.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("RefurbIQ OS", fontSize = 17.sp, fontWeight = FontWeight.Black)
                        Text(currentUser?.shopName ?: "Apex Mobile Hub", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Menu")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dark Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (isDark) CyberCyan else AzureBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.isDarkMode.value = it },
                        modifier = Modifier.testTag("menu_switch_dark_mode")
                    )
                }

                // Passive Visit Privacy Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPassive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = if (isPassive) AmberWarning else AzureBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Passive Privacy View", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Switch(
                        checked = isPassive,
                        onCheckedChange = { viewModel.isPassiveVisitMode.value = it },
                        modifier = Modifier.testTag("menu_switch_passive_mode")
                    )
                }

                // Role Switcher
                Text("Switch Active Profile:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("SELLER", "CONSUMER", "ADMIN").forEach { role ->
                        val isSelected = (userRole == role)
                        Button(
                            onClick = {
                                viewModel.switchRole(role)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) AzureBlue else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(role, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Quick Links
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.isUtmDialogOpen.value = true
                            onDismiss()
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UTM Campaign Attribution", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.isContactDialogOpen.value = true
                            onDismiss()
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Technician Support & Help Desk", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                // Last updated footer
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "System Sync: $lastUpdate • v2.4",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AzureBlue)) {
                Text("Close", color = Color.White)
            }
        }
    )
}
