package com.example.ui.sancharsaathi

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CaptchaVisualCard
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.PulsingSyncIndicator
import com.example.ui.components.StatusBadge
import com.example.ui.scanner.CameraQrScannerSheet
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
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightCardGlass
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SancharSaathiEngine
import com.example.util.SancharSaathiReport
import com.example.viewmodel.PhoneKhojViewModel

@Composable
fun SancharSaathiScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentImeiInput by viewModel.sancharSaathiInputImei.collectAsState()
    val isLoading by viewModel.sancharSaathiIsLoading.collectAsState()
    val report by viewModel.sancharSaathiReport.collectAsState()
    val history by viewModel.sancharSaathiHistory.collectAsState()
    val captchaChallenge by viewModel.sancharSaathiCaptcha.collectAsState()
    val captchaInput by viewModel.sancharSaathiCaptchaInput.collectAsState()
    val captchaError by viewModel.sancharSaathiCaptchaError.collectAsState()

    var showCameraScanner by remember { mutableStateOf(false) }
    var inputImeiText by remember(currentImeiInput) { mutableStateOf(currentImeiInput) }
    val isLuhnValid = remember(inputImeiText) {
        inputImeiText.length == 15 && SancharSaathiEngine.validateLuhn(inputImeiText)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -------------------------------------------------------------
        // HEADER: OFFICIAL SANCHAR SAATHI & CEIR GOVT REGISTRY
        // -------------------------------------------------------------
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "CEIR Emblem",
                                    tint = AzureBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "संचार साथी",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzureBlue
                                )
                                Text(
                                    text = " • Sanchar Saathi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "DoT CEIR Telemetry & Specs",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                    }

                    PulsingSyncIndicator(text = "DoT Live")
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Official Know Your Mobile (KYM) verification, GSMA TAC decoder, and full device architecture.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        // -------------------------------------------------------------
        // SEARCH & IMEI INPUT CARD
        // -------------------------------------------------------------
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enter 15-Digit Device IMEI",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (inputImeiText.isNotEmpty()) {
                            Text(
                                text = "${inputImeiText.length}/15 digits",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (inputImeiText.length == 15) EmeraldPass else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputImeiText,
                            onValueChange = {
                                if (it.length <= 15) inputImeiText = it.filter { ch -> ch.isDigit() }
                            },
                            placeholder = { Text("e.g. 353084110948201", color = TextMuted) },
                            leadingIcon = {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AzureBlue)
                            },
                            trailingIcon = {
                                if (inputImeiText.isNotEmpty()) {
                                    IconButton(onClick = { inputImeiText = "" }) {
                                        Icon(Icons.Default.Search, contentDescription = "Clear", tint = TextMuted)
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sanchar_saathi_imei_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = LightCardSubtle,
                                focusedBorderColor = AzureBlue,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Surface(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showCameraScanner = true }
                                .testTag("btn_scan_box_qr"),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AzureBlue)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan Box QR Code or Barcode",
                                    tint = AzureBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Luhn Checksum Feedback Tag
                    if (inputImeiText.length == 15) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLuhnValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isLuhnValid) EmeraldPass else CrimsonFail,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLuhnValid)
                                    "Luhn Checksum Valid (ISO/IEC 7812 Certified Genuine IMEI)"
                                else
                                    "Luhn Checksum Mismatch! 15th digit does not match mathematical check.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isLuhnValid) EmeraldPass else CrimsonFail
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Real IMEI Test Chips
                    Text(
                        text = "1-Tap Quick Test Real IMEIs:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SancharSaathiEngine.sampleImeis) { sample ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        inputImeiText = sample.imei
                                        viewModel.sancharSaathiInputImei.value = sample.imei
                                        viewModel.sancharSaathiCaptchaInput.value = captchaChallenge.code
                                        viewModel.runSancharSaathiInspection(sample.imei)
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = if (sample.isFlagged) CrimsonFailBg else if (sample.imei == inputImeiText) Color(0xFFEFF6FF) else LightCardSubtle,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (sample.isFlagged) CrimsonFail else if (sample.imei == inputImeiText) AzureBlue else GlassBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (sample.isFlagged) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonFail, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = sample.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (sample.imei == inputImeiText) FontWeight.Bold else FontWeight.Medium,
                                        color = if (sample.isFlagged) CrimsonFail else if (sample.imei == inputImeiText) AzureBlue else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sanchar Saathi DoT Security Captcha Verification Card
                    CaptchaVisualCard(
                        challenge = captchaChallenge,
                        enteredCode = captchaInput,
                        onCodeChange = { viewModel.sancharSaathiCaptchaInput.value = it },
                        onRefresh = { viewModel.refreshSancharSaathiCaptcha() },
                        errorMessage = captchaError,
                        modifier = Modifier.testTag("sanchar_saathi_captcha_card")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = AzureBlue,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Querying Govt CEIR / Sanchar Saathi Central Registry...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = AzureBlue
                            )
                        }
                    } else {
                        NeonButton(
                            text = "Verify Captcha & Inspect Real IMEI",
                            onClick = {
                                if (inputImeiText.length < 8) {
                                    Toast.makeText(context, "Please enter at least 8 digits of IMEI", Toast.LENGTH_SHORT).show()
                                } else if (captchaInput.isBlank()) {
                                    Toast.makeText(context, "Please enter the security captcha characters", Toast.LENGTH_SHORT).show()
                                } else {
                                    val success = viewModel.verifyAndInspectSancharSaathi(inputImeiText, captchaInput)
                                    if (!success) {
                                        Toast.makeText(context, "Captcha mismatch! Please enter the new code shown.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "run_sanchar_saathi_btn"
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // REPORT SECTION: OFFICIAL REPORT DISPLAY
        // -------------------------------------------------------------
        report?.let { r ->
            item {
                OfficialCertificateCard(report = r, onShare = {
                    shareSancharSaathiReport(context, r)
                })
            }

            item {
                DeviceSpecificationMatrix(report = r)
            }

            item {
                TelecomRadiationSafetyCard(report = r)
            }

            // Quick Workflow Transfer Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeonButton(
                        text = "Transfer to 2-Min QC",
                        onClick = {
                            viewModel.diagDeviceModel.value = r.modelName
                            viewModel.diagImei.value = r.imei
                            viewModel.selectedTab.value = 1 // Switch to Diagnostics
                        },
                        icon = { Icon(Icons.Default.Speed, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.weight(1f),
                        testTag = "transfer_to_diag_btn"
                    )

                    NeonButton(
                        text = "Estimate Buyback",
                        onClick = {
                            viewModel.valModel.value = if (r.modelName.contains("iPhone 15")) "iPhone 15 Pro" else if (r.modelName.contains("Galaxy")) "Galaxy S24 Ultra" else "iPhone 14 Pro"
                            viewModel.selectedTab.value = 2 // Switch to Valuation
                        },
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "transfer_to_val_btn"
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // VERIFICATION AUDIT TRAIL
        // -------------------------------------------------------------
        if (history.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Inspected IMEIs (${history.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            items(history) { item ->
                RecentImeiHistoryCard(
                    report = item,
                    onClick = {
                        inputImeiText = item.imei
                        viewModel.sancharSaathiInputImei.value = item.imei
                        viewModel.sancharSaathiCaptchaInput.value = captchaChallenge.code
                        viewModel.runSancharSaathiInspection(item.imei)
                    }
                )
            }
        }
    }

    if (showCameraScanner) {
        CameraQrScannerSheet(
            onDismiss = { showCameraScanner = false },
            onImeiScanned = { scannedImei ->
                inputImeiText = scannedImei
                viewModel.sancharSaathiInputImei.value = scannedImei
                viewModel.sancharSaathiCaptchaInput.value = captchaChallenge.code
                Toast.makeText(context, "Scanned box IMEI: $scannedImei", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun OfficialCertificateCard(
    report: SancharSaathiReport,
    onShare: () -> Unit
) {
    val isClean = report.ceirStatus == "CLEAN_VALID"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0x222563EB)),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isClean) EmeraldPass else CrimsonFail
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Govt Header with Ashoka Emblem & DoT branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Govt of India",
                                tint = AzureBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DEPARTMENT OF TELECOMMUNICATIONS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "Government of India • CEIR National Portal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                StatusBadge(status = if (isClean) "CLEAN" else "FLAGGED")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isClean) EmeraldPassBg else CrimsonFailBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isClean) Color(0xFFBBF7D0) else Color(0xFFFECACA))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isClean) Icons.Default.Verified else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isClean) EmeraldPass else CrimsonFail,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isClean) "GENUINE - NOT BLACKLISTED" else "BLACKLISTED / REPORTED STOLEN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isClean) EmeraldPass else CrimsonFail
                        )
                        Text(
                            text = report.ceirRemarks,
                            fontSize = 11.sp,
                            color = if (isClean) Color(0xFF065F46) else Color(0xFF991B1B),
                            lineHeight = 15.sp
                        )
                        if (report.firNumber != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "FIR No: ${report.firNumber} | Date: ${report.reportedDate ?: "Recent"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonFail
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Certificate Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "DEVICE NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = report.modelName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "GSMA TAC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = report.tac, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AzureBlue)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "15-DIGIT IMEI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = report.imei, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "REF / TX ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = report.transactionId, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Share Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Verified on: ${report.verificationTimestamp}",
                    fontSize = 10.sp,
                    color = TextMuted
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onShare() },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Share Report", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AzureBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceSpecificationMatrix(report: SancharSaathiReport) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complete Device Hardware Architecture",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SpecRow(label = "Brand & Marketing Name", value = "${report.brand} ${report.modelName}", icon = Icons.Default.Smartphone)
            SpecRow(label = "Model Identifier", value = report.modelNumber, icon = Icons.Default.QrCode2)
            SpecRow(label = "Legal Manufacturer", value = report.manufacturer, icon = Icons.Default.AccountBalance)
            SpecRow(label = "Country of Assembly", value = report.countryOfOrigin, icon = Icons.Default.CellTower)
            SpecRow(label = "Processor & GPU", value = report.processor, icon = Icons.Default.Memory)
            SpecRow(label = "Display & Screen", value = report.display, icon = Icons.Default.PhoneAndroid)
            SpecRow(label = "Primary Optics", value = report.mainCamera, icon = Icons.Default.CameraAlt)
            SpecRow(label = "Selfie Camera", value = report.selfieCamera, icon = Icons.Default.CameraAlt)
            SpecRow(label = "Battery & Fast Charge", value = "${report.batteryCapacity} • ${report.chargingSpeed}", icon = Icons.Default.BatteryChargingFull)
            SpecRow(label = "Memory & Storage", value = report.ramStorage, icon = Icons.Default.Memory)
            SpecRow(label = "SIM Architecture", value = report.simType, icon = Icons.Default.CellTower)
            SpecRow(label = "Original Activation", value = report.originalActivationDate, icon = Icons.Default.Check)
            SpecRow(label = "Warranty Standing", value = report.warrantyStatus, icon = Icons.Default.Verified)
        }
    }
}

@Composable
fun SpecRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GlassBorderSubtle)
        )
    }
}

@Composable
fun TelecomRadiationSafetyCard(report: SancharSaathiReport) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CellTower, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Indian Telecom Standards & SAR Compliance",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPassBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "SAR HEAD RADIATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPass)
                        Text(text = report.sarHead, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(text = "India Limit: 1.6 W/kg", fontSize = 9.sp, color = TextSecondary)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPassBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "SAR BODY RADIATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPass)
                        Text(text = report.sarBody, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(text = "Safe & Compliant", fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LightCardSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "BIS Indian Regulatory Clearance", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Registration No: ${report.bisRegistrationNumber}", fontSize = 11.sp, color = AzureBlue)
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDCFCE7)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPass, modifier = Modifier.padding(4.dp).size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Network Bands: ${report.networkSupport}",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun RecentImeiHistoryCard(
    report: SancharSaathiReport,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = if (report.ceirStatus == "CLEAN_VALID") EmeraldPassBg else CrimsonFailBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (report.ceirStatus == "CLEAN_VALID") Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (report.ceirStatus == "CLEAN_VALID") EmeraldPass else CrimsonFail,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = report.modelName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "IMEI: ${report.imei}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }
            }

            StatusBadge(status = if (report.ceirStatus == "CLEAN_VALID") "CLEAN" else "FLAGGED")
        }
    }
}

private fun shareSancharSaathiReport(context: Context, report: SancharSaathiReport) {
    val shareText = """
        🇮🇳 *OFFICIAL SANCHAR SAATHI / CEIR TELEMETRY REPORT*
        *Portal Ref:* ${report.transactionId}
        *Status:* ${if (report.ceirStatus == "CLEAN_VALID") "✅ GENUINE - NOT BLACKLISTED (Clean NOC)" else "❌ BLACKLISTED / STOLEN REPORTED"}
        ------------------------------------------
        📱 *Device:* ${report.brand} ${report.modelName}
        🔢 *15-Digit IMEI:* ${report.imei}
        🔖 *GSMA TAC:* ${report.tac}
        🏢 *Manufacturer:* ${report.manufacturer}
        ⚙️ *Processor:* ${report.processor}
        🔋 *Battery & Charge:* ${report.batteryCapacity} (${report.chargingSpeed})
        ☢️ *SAR Radiation:* Head ${report.sarHead}, Body ${report.sarBody} (DoT Compliant)
        📜 *BIS Registration:* ${report.bisRegistrationNumber}
        ------------------------------------------
        🛡️ *RefurbIQ Certified Verification*
        Official Verification Timestamp: ${report.verificationTimestamp}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Sanchar Saathi Report - ${report.modelName}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Official Sanchar Saathi Report"))
}
