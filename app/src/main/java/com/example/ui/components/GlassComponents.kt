package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.CrimsonFailBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.EmeraldPassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.LightCardGlass
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = LightCardGlass,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = shape, spotColor = Color(0x140F172A))
            .clip(shape)
            .border(borderWidth, borderColor, shape),
        color = backgroundColor,
        shape = shape
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "neon_button",
    icon: (@Composable () -> Unit)? = null,
    isSecondary: Boolean = false,
    enabled: Boolean = true
) {
    val gradientBrush = if (isSecondary) {
        Brush.horizontalGradient(listOf(LightCardSubtle, LightCardSubtle))
    } else {
        Brush.horizontalGradient(listOf(AzureBlue, ElectricIndigo))
    }

    val textColor = if (isSecondary) AzureBlue else Color.White
    val borderStroke = if (isSecondary) BorderStroke(1.dp, Color(0xFFCBD5E1)) else null

    Button(
        onClick = onClick,
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(14.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0x1F2563EB)
        ),
        border = borderStroke,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "IN_STOCK", "CERTIFIED", "PASS", "CLEAN", "GOOD", "CLEAN_VALID" ->
            Triple(EmeraldPassBg, EmeraldPass, if (status == "IN_STOCK") "IN STOCK" else if (status == "CLEAN_VALID") "CLEAN & VALID" else status)
        "IN_REPAIR", "WARNING", "DEGRADED" ->
            Triple(AmberWarningBg, AmberWarning, if (status == "IN_REPAIR") "IN REPAIR" else status)
        "SOLD" ->
            Triple(Color(0xFFEEF2FF), ElectricIndigo, "SOLD")
        "FAILED", "FLAGGED", "CRITICAL", "BLACKLISTED_STOLEN" ->
            Triple(CrimsonFailBg, CrimsonFail, if (status == "BLACKLISTED_STOLEN") "BLACKLISTED" else status)
        else ->
            Triple(Color(0xFFF1F5F9), TextSecondary, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PulsingSyncIndicator(
    text: String = "Sanchar Saathi: Live",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEFF6FF))
            .border(1.dp, AzureBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(AzureBlue)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = AzureBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
