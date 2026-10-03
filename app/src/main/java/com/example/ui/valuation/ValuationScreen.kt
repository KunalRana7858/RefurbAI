package com.example.ui.valuation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.BackToTopButton
import com.example.ui.components.CopyIconButton
import com.example.ui.components.ScrollProgressBar
import com.example.ui.components.formatPassivePrice
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianCardGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SecurityUtils
import com.example.viewmodel.PhoneKhojViewModel

@Composable
fun ValuationScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val brand by viewModel.valBrand.collectAsState()
    val model by viewModel.valModel.collectAsState()
    val storage by viewModel.valStorage.collectAsState()
    val isPassive by viewModel.isPassiveVisitMode.collectAsState()

    // Defect states
    val displayCrack by viewModel.defectDisplayCrack.collectAsState()
    val deadPixels by viewModel.defectDeadPixels.collectAsState()
    val batteryDegraded by viewModel.defectBatteryDegraded.collectAsState()
    val cameraIssue by viewModel.defectCameraIssue.collectAsState()
    val backCrack by viewModel.defectBackCoverCrack.collectAsState()
    val speakerIssue by viewModel.defectSpeakerMicIssue.collectAsState()
    val missingBox by viewModel.defectMissingBoxCharger.collectAsState()
    val housingDents by viewModel.defectHousingDents.collectAsState()

    val profitTarget by viewModel.targetProfitTarget.collectAsState()

    val basePrice = viewModel.getBaseMarketPrice()
    val totalDeductions = viewModel.getTotalDeductions()
    val repairOverhead = viewModel.getRepairOverhead()
    val estimatedResale = (basePrice - totalDeductions).coerceAtLeast(3000.0)
    val recommendedOffer = viewModel.getRecommendedBuyOffer()

    val showBackToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 1 }
    }
    val scrollProgress by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems <= 1) 0f
            else (listState.firstVisibleItemIndex.toFloat() / (totalItems - 1)).coerceIn(0f, 1f)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(ObsidianBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScrollProgressBar(progress = scrollProgress)

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // HEADER
                item {
                    Column {
                        Text(
                            text = "Smart Valuation & Margin Engine",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Algorithmic Pricing with Diagnostic Defect Deductions",
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }
                }

                // DEVICE SELECTOR CARD
                item {
                    DeviceSelectorCard(
                        brand = brand,
                        model = model,
                        storage = storage,
                        onBrandChange = { newBrand ->
                            viewModel.valBrand.value = newBrand
                            viewModel.valModel.value = when (newBrand) {
                                "Apple" -> "iPhone 13"
                                "Samsung" -> "Galaxy S23"
                                "OnePlus" -> "OnePlus 11"
                                "Google" -> "Pixel 7 Pro"
                                "Xiaomi" -> "Redmi Note 12 Pro"
                                else -> "Vivo V29 Pro"
                            }
                        },
                        onModelChange = { viewModel.valModel.value = it },
                        onStorageChange = { viewModel.valStorage.value = it },
                        baseMarketPrice = basePrice
                    )
                }

                // DEFECT DEDUCTION CHECKLIST
                item {
                    DefectDeductionCard(
                        displayCrack = displayCrack,
                        onToggleDisplayCrack = { viewModel.defectDisplayCrack.value = it },
                        deadPixels = deadPixels,
                        onToggleDeadPixels = { viewModel.defectDeadPixels.value = it },
                        batteryDegraded = batteryDegraded,
                        onToggleBatteryDegraded = { viewModel.defectBatteryDegraded.value = it },
                        cameraIssue = cameraIssue,
                        onToggleCameraIssue = { viewModel.defectCameraIssue.value = it },
                        backCrack = backCrack,
                        onToggleBackCrack = { viewModel.defectBackCoverCrack.value = it },
                        speakerIssue = speakerIssue,
                        onToggleSpeakerIssue = { viewModel.defectSpeakerMicIssue.value = it },
                        missingBox = missingBox,
                        onToggleMissingBox = { viewModel.defectMissingBoxCharger.value = it },
                        housingDents = housingDents,
                        onToggleHousingDents = { viewModel.defectHousingDents.value = it },
                        totalDeductions = totalDeductions
                    )
                }

                // PROFIT TARGET SLIDER
                item {
                    ProfitTargetCard(
                        profitTarget = profitTarget,
                        onProfitChange = { viewModel.targetProfitTarget.value = it }
                    )
                }

                // FINAL FINANCIAL CALCULATION HERO
                item {
                    PriceCalculationHeroCard(
                        basePrice = basePrice,
                        totalDeductions = totalDeductions,
                        estimatedResale = estimatedResale,
                        repairOverhead = repairOverhead,
                        profitTarget = profitTarget,
                        recommendedOffer = recommendedOffer,
                        isPassive = isPassive,
                        onProceedToKyc = {
                            viewModel.kycImei.value = "359284110948201"
                            viewModel.selectedTab.value = 4 // Switch to KYC tab
                        },
                        onIntakeToInventory = {
                            viewModel.intakeValuatedDevice { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        },
                        onShareQuote = {
                            val quoteText = """
                                📱 *RefurbIQ Device Valuation Quote*
                                Device: $brand $model ($storage)
                                Base MRP Benchmark: ₹${basePrice.toLong()}
                                Deductions: -₹${totalDeductions.toLong()}
                                Recommended Buyback Offer: ₹${recommendedOffer.toLong()}
                                Resale Estimate: ₹${estimatedResale.toLong()}
                                Verified via RefurbIQ Engine • Oct 02, 2026
                            """.trimIndent()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, quoteText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Valuation Quote"))
                        }
                    )
                }
            }
        }

        // Back to top floating button
        BackToTopButton(
            visible = showBackToTop,
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
        )
    }
}

@Composable
fun DeviceSelectorCard(
    brand: String,
    model: String,
    storage: String,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onStorageChange: (String) -> Unit,
    baseMarketPrice: Double
) {
    val brands = listOf("Apple", "Samsung", "OnePlus", "Google", "Xiaomi", "Vivo")
    val modelsForBrand = when (brand) {
        "Apple" -> listOf("iPhone 15 Pro", "iPhone 15", "iPhone 14 Pro", "iPhone 14", "iPhone 13", "iPhone 12")
        "Samsung" -> listOf("Galaxy S24 Ultra", "Galaxy S23 Ultra", "Galaxy S23", "Galaxy S22")
        "OnePlus" -> listOf("OnePlus 12", "OnePlus 11")
        "Google" -> listOf("Pixel 8 Pro", "Pixel 7 Pro")
        "Xiaomi" -> listOf("Redmi Note 13 Pro+", "Redmi Note 12 Pro")
        else -> listOf("Vivo V29 Pro")
    }
    val storages = listOf("64GB", "128GB", "256GB", "512GB")

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device Model & Variant",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Base Market: ${SecurityUtils.formatCurrency(baseMarketPrice)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brand Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                brands.forEach { b ->
                    val selected = (b == brand)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) CyberCyan else Color(0xFF161F30))
                            .border(1.dp, if (selected) CyberCyan else GlassBorderSubtle, RoundedCornerShape(14.dp))
                            .clickable { onBrandChange(b) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = b,
                            color = if (selected) ObsidianBackground else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Model Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                modelsForBrand.forEach { m ->
                    val selected = (m == model)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) AzureBlue else Color(0x331E293B))
                            .border(1.dp, if (selected) AzureBlue else Color(0x1FFFFFFF), RoundedCornerShape(14.dp))
                            .clickable { onModelChange(m) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = m,
                            color = if (selected) TextPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Storage Variant Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storages.forEach { s ->
                    val selected = (s == storage)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Color(0x4000F5D4) else Color(0xFF161F30))
                            .border(1.dp, if (selected) CyberCyan else GlassBorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { onStorageChange(s) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = s,
                            color = if (selected) CyberCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefectDeductionCard(
    displayCrack: Boolean,
    onToggleDisplayCrack: (Boolean) -> Unit,
    deadPixels: Boolean,
    onToggleDeadPixels: (Boolean) -> Unit,
    batteryDegraded: Boolean,
    onToggleBatteryDegraded: (Boolean) -> Unit,
    cameraIssue: Boolean,
    onToggleCameraIssue: (Boolean) -> Unit,
    backCrack: Boolean,
    onToggleBackCrack: (Boolean) -> Unit,
    speakerIssue: Boolean,
    onToggleSpeakerIssue: (Boolean) -> Unit,
    missingBox: Boolean,
    onToggleMissingBox: (Boolean) -> Unit,
    housingDents: Boolean,
    onToggleHousingDents: (Boolean) -> Unit,
    totalDeductions: Double
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Defect Deductions (Auto-Synced)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "-${SecurityUtils.formatCurrency(totalDeductions)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CrimsonFail
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DefectRow("Display Glass Scratch / Crack", "-₹3,500", displayCrack, onToggleDisplayCrack)
            DefectRow("Dead Pixels / Touch Digitizer Lag", "-₹2,500", deadPixels, onToggleDeadPixels)
            DefectRow("Battery Health < 80% (Replacement Req.)", "-₹1,500", batteryDegraded, onToggleBatteryDegraded)
            DefectRow("Camera Autofocus / Lens Scratches", "-₹2,000", cameraIssue, onToggleCameraIssue)
            DefectRow("Back Cover Glass / Panel Crack", "-₹800", backCrack, onToggleBackCrack)
            DefectRow("Distorted Speaker / Microphone", "-₹900", speakerIssue, onToggleSpeakerIssue)
            DefectRow("Missing Original Box / Fast Charger", "-₹600", missingBox, onToggleMissingBox)
            DefectRow("Bezel Dents / Housing Paint Wear", "-₹700", housingDents, onToggleHousingDents)
        }
    }
}

@Composable
fun DefectRow(
    title: String,
    deductionText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = "Deduction: $deductionText", fontSize = 10.sp, color = CrimsonFail)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberCyan,
                checkedTrackColor = AzureBlue,
                uncheckedTrackColor = Color(0xFF1E293B)
            )
        )
    }
}

@Composable
fun ProfitTargetCard(
    profitTarget: Double,
    onProfitChange: (Double) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shopkeeper Profit Target",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = SecurityUtils.formatCurrency(profitTarget),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldPass
                )
            }

            Slider(
                value = profitTarget.toFloat(),
                onValueChange = { onProfitChange(it.toDouble()) },
                valueRange = 1000f..8000f,
                steps = 14,
                colors = SliderDefaults.colors(
                    thumbColor = CyberCyan,
                    activeTrackColor = AzureBlue,
                    inactiveTrackColor = Color(0x33FFFFFF)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Min: ₹1,000", fontSize = 10.sp, color = TextMuted)
                Text("Max: ₹8,000", fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun PriceCalculationHeroCard(
    basePrice: Double,
    totalDeductions: Double,
    estimatedResale: Double,
    repairOverhead: Double,
    profitTarget: Double,
    recommendedOffer: Double,
    isPassive: Boolean = false,
    onProceedToKyc: () -> Unit,
    onIntakeToInventory: () -> Unit = {},
    onShareQuote: () -> Unit = {}
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberCyan.copy(alpha = 0.6f),
        backgroundColor = Color(0xFF0F1B2F)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Valuation Summary & Offer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CopyIconButton(
                        textToCopy = "Device Offer: ₹${recommendedOffer.toLong()} | Resale: ₹${estimatedResale.toLong()}",
                        label = "Valuation"
                    )
                    IconButton(
                        onClick = onShareQuote,
                        modifier = Modifier.size(32.dp).testTag("val_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = "Share Quote",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Base Market Value:", fontSize = 12.sp, color = TextSecondary)
                Text(formatPassivePrice(basePrice, isPassive), fontSize = 12.sp, color = TextPrimary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Defects Deduction:", fontSize = 12.sp, color = TextSecondary)
                Text("-${formatPassivePrice(totalDeductions, isPassive)}", fontSize = 12.sp, color = CrimsonFail)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Estimated Resale Value:", fontSize = 12.sp, color = TextSecondary)
                Text(formatPassivePrice(estimatedResale, isPassive), fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Repair Overhead Cost:", fontSize = 12.sp, color = TextSecondary)
                Text("-${formatPassivePrice(repairOverhead, isPassive)}", fontSize = 12.sp, color = AmberWarning)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Shopkeeper Target Margin:", fontSize = 12.sp, color = TextSecondary)
                Text("-${formatPassivePrice(profitTarget, isPassive)}", fontSize = 12.sp, color = EmeraldPass)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // RECOMMENDED BUY OFFER HERO BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(CyberCyan, AzureBlue)))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RECOMMENDED BUY OFFER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianBackground,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = formatPassivePrice(recommendedOffer, isPassive),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = ObsidianBackground
                    )
                    Text(
                        text = "Guarantees ${formatPassivePrice(profitTarget, isPassive)} net shopkeeper profit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xCC0B0F19)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Intake into Inventory
            Button(
                onClick = onIntakeToInventory,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPass),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("btn_direct_inventory_intake")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Intake Directly into Inventory", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            NeonButton(
                text = "Accept Offer & Proceed to Legal KYC",
                onClick = onProceedToKyc,
                icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ObsidianBackground) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "proceed_kyc_btn"
            )
        }
    }
}
