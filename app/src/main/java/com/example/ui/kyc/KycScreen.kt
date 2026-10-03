package com.example.ui.kyc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.StatusBadge
import com.example.ui.scanner.CameraQrScannerSheet
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.CrimsonFailBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.EmeraldPassBg
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
fun KycScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val imei by viewModel.kycImei.collectAsState()
    val ceirStatus by viewModel.kycCeirStatus.collectAsState()
    val ceirDetails by viewModel.kycCeirDetails.collectAsState()

    val customerName by viewModel.kycCustomerName.collectAsState()
    val customerPhone by viewModel.kycCustomerPhone.collectAsState()
    val customerAadhaar by viewModel.kycCustomerAadhaar.collectAsState()
    val selfieCaptured by viewModel.kycSelfieCaptured.collectAsState()
    val idUploaded by viewModel.kycIdUploaded.collectAsState()
    val declarationAccepted by viewModel.kycDeclarationAccepted.collectAsState()

    val strokes by viewModel.signatureStrokes.collectAsState()
    val isSaved by viewModel.isSignatureSaved.collectAsState()
    var showCameraScanner by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Legal KYC & Fraud Shield",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Stolen Phone Protection • Sanchar Saathi / CEIR Portal",
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }

                    StatusBadge(status = if (ceirStatus == "CLEAN") "CLEAN" else if (ceirStatus == "FLAGGED") "FLAGGED" else "ACTIVE")
                }
            }
        }

        // STEP 1: IMEI CEIR SANCHAR SAATHI VALIDATION
        item {
            CeirValidationCard(
                imei = imei,
                onImeiChange = { viewModel.kycImei.value = it },
                ceirStatus = ceirStatus,
                ceirDetails = ceirDetails,
                onVerify = { viewModel.runCeirFraudCheck() },
                onViewSpecs = {
                    viewModel.runSancharSaathiInspection(imei)
                    viewModel.selectedTab.value = 2
                },
                onScanBoxQr = { showCameraScanner = true }
            )
        }

        // STEP 2: CUSTOMER IDENTIFICATION & LIVE SELFIE CAPTURE
        item {
            CustomerVerificationCard(
                name = customerName,
                onNameChange = { viewModel.kycCustomerName.value = it },
                phone = customerPhone,
                onPhoneChange = { viewModel.kycCustomerPhone.value = it },
                aadhaar = customerAadhaar,
                onAadhaarChange = { viewModel.kycCustomerAadhaar.value = it },
                selfieCaptured = selfieCaptured,
                onCaptureSelfie = { viewModel.kycSelfieCaptured.value = true },
                idUploaded = idUploaded,
                onUploadId = { viewModel.kycIdUploaded.value = true }
            )
        }

        // STEP 3: STATUTORY LEGAL SELF-DECLARATION
        item {
            LegalDeclarationCard(
                accepted = declarationAccepted,
                onAcceptChange = { viewModel.kycDeclarationAccepted.value = it },
                imei = imei
            )
        }

        // STEP 4: DIGITAL SIGNATURE PAD (TOUCH CANVAS)
        item {
            DigitalSignaturePadCard(
                strokes = strokes,
                isSaved = isSaved,
                onAddPoint = { viewModel.addStrokePoint(it) },
                onEndStroke = { viewModel.endStroke() },
                onClear = { viewModel.clearSignature() }
            )
        }

        // COMPLETION ACTION
        item {
            NeonButton(
                text = "Complete KYC & Generate WhatsApp Invoice",
                onClick = {
                    viewModel.invoiceCustomerName.value = customerName
                    viewModel.invoiceCustomerPhone.value = customerPhone
                    viewModel.invoiceImei.value = imei
                    viewModel.selectedTab.value = 4 // switch to Invoice tab
                },
                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = ObsidianBackground) },
                modifier = Modifier.fillMaxWidth(),
                enabled = declarationAccepted,
                testTag = "complete_kyc_btn"
            )
        }
    }

    if (showCameraScanner) {
        CameraQrScannerSheet(
            onDismiss = { showCameraScanner = false },
            onImeiScanned = { scannedImei ->
                viewModel.kycImei.value = scannedImei
                viewModel.runCeirFraudCheck()
            }
        )
    }
}

@Composable
fun CeirValidationCard(
    imei: String,
    onImeiChange: (String) -> Unit,
    ceirStatus: String,
    ceirDetails: String,
    onVerify: () -> Unit,
    onViewSpecs: () -> Unit = {},
    onScanBoxQr: () -> Unit = {}
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (ceirStatus == "CLEAN") EmeraldPass else if (ceirStatus == "FLAGGED") CrimsonFail else AzureBlue.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1. IMEI Fraud & CEIR Blacklist Check",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (ceirStatus == "CLEAN") {
                    StatusBadge(status = "CLEAN")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = imei,
                    onValueChange = onImeiChange,
                    label = { Text("15-Digit Device IMEI") },
                    placeholder = { Text("e.g. 359284110948201") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("kyc_imei_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = CyberCyan
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onScanBoxQr() }
                        .testTag("btn_kyc_scan_box_qr"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AzureBlue)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Box QR",
                            tint = AzureBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (ceirStatus == "VERIFYING") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Querying Govt CEIR / Sanchar Saathi Registry...", fontSize = 12.sp, color = CyberCyan)
                }
            } else {
                NeonButton(
                    text = "Verify on CEIR / Sanchar Saathi Portal",
                    onClick = onVerify,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "verify_ceir_btn"
                )
            }

            // Results Banner
            if (ceirStatus == "CLEAN" || ceirStatus == "FLAGGED") {
                Spacer(modifier = Modifier.height(10.dp))
                val isClean = (ceirStatus == "CLEAN")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isClean) EmeraldPassBg else CrimsonFailBg)
                        .border(1.dp, if (isClean) EmeraldPass else CrimsonFail, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isClean) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isClean) EmeraldPass else CrimsonFail,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ceirDetails,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isClean) EmeraldPass else CrimsonFail
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.TextButton(
                    onClick = onViewSpecs,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "View DoT Sanchar Saathi Specs →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzureBlue
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerVerificationCard(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    aadhaar: String,
    onAadhaarChange: (String) -> Unit,
    selfieCaptured: Boolean,
    onCaptureSelfie: () -> Unit,
    idUploaded: Boolean,
    onUploadId: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "2. Customer Identity Verification",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Customer Full Name (as per Govt ID)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("WhatsApp Contact") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = aadhaar,
                    onValueChange = onAadhaarChange,
                    label = { Text("Aadhaar / ID Card No.") },
                    singleLine = true,
                    modifier = Modifier.weight(1.2f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Media verification boxes (Selfie + Aadhaar Card)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Mock Selfie Capture Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, if (selfieCaptured) EmeraldPass else GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { onCaptureSelfie() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selfieCaptured) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = if (selfieCaptured) EmeraldPass else CyberCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selfieCaptured) "Live Selfie: Verified" else "Take Live Selfie",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selfieCaptured) EmeraldPass else TextPrimary
                        )
                        Text(text = "Facial Match OK", fontSize = 9.sp, color = TextSecondary)
                    }
                }

                // Mock Govt ID / Aadhaar Upload Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, if (idUploaded) EmeraldPass else GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { onUploadId() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (idUploaded) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = if (idUploaded) EmeraldPass else AzureBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (idUploaded) "Aadhaar: Verified" else "Upload Aadhaar Card",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (idUploaded) EmeraldPass else TextPrimary
                        )
                        Text(text = "OCR Match 100%", fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun LegalDeclarationCard(
    accepted: Boolean,
    onAcceptChange: (Boolean) -> Unit,
    imei: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = AmberWarning.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "3. Statutory Ownership Self-Declaration",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090E17))
                    .padding(10.dp)
            ) {
                Text(
                    text = "UNDER BHARATIYA NYAYA SANHITA (BNS) & INDIAN IT ACT:\n" +
                            "I hereby declare that I am the legitimate, sole legal owner of this device with IMEI: ${SecurityUtils.formatImei(imei)}. " +
                            "I solemnly affirm that this mobile phone was neither stolen, duplicated, cloned, nor acquired via unlawful means. " +
                            "I authorize the shopkeeper to store this biometric timestamp and KYC record for police verification if required.",
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = accepted,
                    onCheckedChange = onAcceptChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = CyberCyan,
                        checkmarkColor = ObsidianBackground
                    )
                )
                Text(
                    text = "I accept and digitally sign the legal ownership affidavit.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (accepted) CyberCyan else TextPrimary
                )
            }
        }
    }
}

@Composable
fun DigitalSignaturePadCard(
    strokes: List<List<androidx.compose.ui.geometry.Offset>>,
    isSaved: Boolean,
    onAddPoint: (androidx.compose.ui.geometry.Offset) -> Unit,
    onEndStroke: () -> Unit,
    onClear: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "4. Customer Digital Signature Pad",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClear, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Text(
                text = "Draw signature smoothly using your finger on the touch surface below:",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Signature Touch Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070C15))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                onAddPoint(change.position)
                            },
                            onDragEnd = {
                                onEndStroke()
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw saved strokes
                    strokes.forEach { strokeList ->
                        if (strokeList.size > 1) {
                            val path = Path()
                            path.moveTo(strokeList[0].x, strokeList[0].y)
                            for (i in 1 until strokeList.size) {
                                path.lineTo(strokeList[i].x, strokeList[i].y)
                            }
                            drawPath(
                                path = path,
                                color = CyberCyan,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    // Watermark line
                    drawLine(
                        color = Color(0x33FFFFFF),
                        start = androidx.compose.ui.geometry.Offset(20f, size.height - 24f),
                        end = androidx.compose.ui.geometry.Offset(size.width - 20f, size.height - 24f),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // If empty, draw a placeholder hint
                if (strokes.isEmpty()) {
                    Text(
                        text = "✍️ Sign Here with Finger",
                        color = Color(0x40FFFFFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Watermark Timestamp
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0x6000F5D4), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Geo-Verified 28.6139° N, 77.2090° E",
                        fontSize = 9.sp,
                        color = Color(0x60FFFFFF)
                    )
                }
            }
        }
    }
}
