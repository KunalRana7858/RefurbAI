package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.CaptchaChallenge
import kotlin.random.Random

@Composable
fun CaptchaVisualCard(
    challenge: CaptchaChallenge,
    enteredCode: String,
    onCodeChange: (String) -> Unit,
    onRefresh: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (errorMessage != null) CrimsonFail else GlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AzureBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sanchar Saathi Security Verification",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "DoT Captcha",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzureBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Captcha Canvas Image Box + Refresh Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Distorted Captcha Image Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE2E8F0))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CaptchaCanvas(code = challenge.code)
                }

                // Refresh Captcha Button
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                        .testTag("refresh_captcha_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload Captcha",
                        tint = AzureBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Field
            OutlinedTextField(
                value = enteredCode,
                onValueChange = {
                    if (it.length <= 6) onCodeChange(it.trim())
                },
                label = { Text("Enter Characters Shown Above") },
                placeholder = { Text("e.g. ${challenge.code}") },
                singleLine = true,
                isError = errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("captcha_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzureBlue,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    errorBorderColor = CrimsonFail
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = errorMessage,
                    color = CrimsonFail,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CaptchaCanvas(code: String) {
    val seed = remember(code) { code.hashCode().toLong() }
    val random = remember(seed) { Random(seed) }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Background noise grid
        for (i in 0 until 18) {
            val startX = random.nextFloat() * width
            val startY = random.nextFloat() * height
            val endX = random.nextFloat() * width
            val endY = random.nextFloat() * height
            drawLine(
                color = Color(0x3364748B),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1.5f
            )
        }

        // Noise dots
        for (i in 0 until 35) {
            val cx = random.nextFloat() * width
            val cy = random.nextFloat() * height
            drawCircle(
                color = Color(0x44334155),
                radius = random.nextFloat() * 2.5f + 1f,
                center = Offset(cx, cy)
            )
        }

        // Draw distorted text characters on native Canvas
        val native = drawContext.canvas.nativeCanvas
        val paint = android.graphics.Paint().apply {
            textSize = 62f
            isFakeBoldText = true
            isAntiAlias = true
            letterSpacing = 0.2f
        }

        val charWidth = width / (code.length + 1)
        code.forEachIndexed { index, char ->
            val charColor = when (index % 4) {
                0 -> android.graphics.Color.rgb(30, 58, 138)  // Deep Blue
                1 -> android.graphics.Color.rgb(180, 24, 48)  // Dark Crimson
                2 -> android.graphics.Color.rgb(5, 150, 105)  // Forest Green
                else -> android.graphics.Color.rgb(109, 40, 217) // Deep Violet
            }
            paint.color = charColor

            val x = (index + 0.6f) * charWidth
            val y = height * 0.72f + (random.nextFloat() * 10f - 5f)
            val angle = (random.nextFloat() * 26f - 13f)

            native.save()
            native.rotate(angle, x, y)
            native.drawText(char.toString(), x, y, paint)
            native.restore()
        }

        // Sine wave through the text
        val wavePoints = 20
        var prevX = 0f
        var prevY = height * 0.5f
        for (i in 1..wavePoints) {
            val currX = (i.toFloat() / wavePoints) * width
            val currY = height * 0.5f + kotlin.math.sin(i * 0.8f) * 12f
            drawLine(
                color = Color(0x550284C7),
                start = Offset(prevX, prevY),
                end = Offset(currX, currY),
                strokeWidth = 2f
            )
            prevX = currX
            prevY = currY
        }
    }
}
