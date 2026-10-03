package com.example.ui.consumer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.EmeraldPassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhoneKhojViewModel

@Composable
fun ConsumerSellScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val models = listOf(
        "iPhone 15 Pro", "iPhone 15", "iPhone 14 Pro", "iPhone 14", "iPhone 13",
        "Galaxy S24 Ultra", "Galaxy S23 Ultra", "Galaxy S23", "OnePlus 12", "Pixel 8 Pro"
    )

    var selectedModel by remember { mutableStateOf("iPhone 15 Pro") }
    var selectedStorage by remember { mutableStateOf("256GB") }
    var conditionGrade by remember { mutableStateOf("Like New (Flawless)") }
    var modelDropdownOpen by remember { mutableStateOf(false) }

    val basePrice = when (selectedModel) {
        "iPhone 15 Pro" -> 78000.0
        "iPhone 15" -> 54000.0
        "iPhone 14 Pro" -> 64000.0
        "iPhone 14" -> 46000.0
        "iPhone 13" -> 38000.0
        "Galaxy S24 Ultra" -> 82000.0
        "Galaxy S23 Ultra" -> 68000.0
        "Galaxy S23" -> 44000.0
        "OnePlus 12" -> 52000.0
        "Pixel 8 Pro" -> 56000.0
        else -> 40000.0
    }

    val conditionMultiplier = when (conditionGrade) {
        "Like New (Flawless)" -> 0.88
        "Good (Minor Body Scratches)" -> 0.75
        "Fair (Dents or Heavy Wear)" -> 0.60
        "Screen Cracked (Needs Repair)" -> 0.42
        else -> 0.75
    }

    val estimatedPayout = (basePrice * conditionMultiplier).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "INSTANT AI BUYBACK VALUATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AzureBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sell Your Phone at Highest Price",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Real-time transparent pricing powered by Sanchar Saathi verification & verified RefurbIQ merchant bids.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Model Selector
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Select Device Model", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { modelDropdownOpen = true }
                                .border(1.dp, AzureBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            color = LightCardSubtle
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = selectedModel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Text(text = "Change ▼", fontSize = 12.sp, color = AzureBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = modelDropdownOpen,
                            onDismissRequest = { modelDropdownOpen = false }
                        ) {
                            models.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m, color = TextPrimary) },
                                    onClick = {
                                        selectedModel = m
                                        modelDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Storage Capacity", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("128GB", "256GB", "512GB", "1TB").forEach { st ->
                            val isSel = selectedStorage == st
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedStorage = st },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) AzureBlue else LightCardSubtle,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AzureBlue else GlassBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Text(
                                        text = st,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Phone Physical Condition", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    listOf(
                        "Like New (Flawless)",
                        "Good (Minor Body Scratches)",
                        "Fair (Dents or Heavy Wear)",
                        "Screen Cracked (Needs Repair)"
                    ).forEach { cond ->
                        val isSel = conditionGrade == cond
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { conditionGrade = cond },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) Color(0xFFEFF6FF) else LightCardSubtle,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AzureBlue else GlassBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSel) Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isSel) AzureBlue else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cond,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) AzureBlue else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Instant Buyback Quote Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = EmeraldPassBg,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPass)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "ESTIMATED REFURBIQ PAYOUT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = EmeraldPass)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹$estimatedPayout",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPass
                        ) {
                            Text(
                                text = "Price Guaranteed",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "Based on $selectedModel $selectedStorage in $conditionGrade condition with clean Sanchar Saathi clearance.",
                        fontSize = 11.sp,
                        color = Color(0xFF065F46)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NeonButton(
                        text = "Book Free Doorstep Pickup & Cash Payout",
                        onClick = {
                            Toast.makeText(context, "Pickup Request Booked for $selectedModel! RefurbIQ agent will contact you.", Toast.LENGTH_LONG).show()
                        },
                        icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
