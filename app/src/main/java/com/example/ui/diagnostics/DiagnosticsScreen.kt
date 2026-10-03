package com.example.ui.diagnostics

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.components.BackToTopButton
import com.example.ui.components.CopyIconButton
import com.example.ui.components.MobileLoadingAnimation
import com.example.ui.components.ScrollProgressBar
import com.example.ui.components.StickyCategoryHeader
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.data.model.DiagnosticRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.StatusBadge
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
import com.example.viewmodel.PhoneKhojViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiagnosticsScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isScanning by viewModel.isRadarScanning.collectAsState()
    val scanProgress by viewModel.radarScanProgress.collectAsState()
    val scanStage by viewModel.radarScanStage.collectAsState()
    val score by viewModel.diagnosticScore.collectAsState()

    var showDeadPixelModal by remember { mutableStateOf(false) }

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
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
                // TOP BANNER
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "2-Minute Diagnostic Engine",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Automated Hardware & Sensor Telemetry Scan",
                                fontSize = 12.sp,
                                color = CyberCyan
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CopyIconButton(
                                textToCopy = "RefurbIQ Diagnostic Health Score: $score/100 • Grade ${if (score >= 85) "A+" else "B"}",
                                label = "Diagnostic Score"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(status = if (score >= 85) "PASS" else "WARNING")
                        }
                    }
                }

                // STICKY HEADER 1: RADAR SCAN
                stickyHeader {
                    StickyCategoryHeader(title = "Automated Hardware Diagnostics", lastUpdate = "Oct 02, 2026")
                }

                // MODULE 0: RADAR SCANNER HERO
                item {
                    RadarScannerCard(
                        isScanning = isScanning,
                        progress = scanProgress,
                        stageText = scanStage,
                        onStartScan = { viewModel.startRadarScan() }
                    )
                }

                // DYNAMIC HEALTH SCORE CIRCLE & SYNC ACTION
                item {
                    HealthScoreBanner(
                        score = score,
                        onSyncToValuation = { viewModel.syncDiagnosticsToValuation() }
                    )
                }

                // STICKY HEADER 2: HARDWARE TESTS
                stickyHeader {
                    StickyCategoryHeader(title = "Interactive Component Sensor Matrix")
                }

                // MODULE 1: TOUCH & DEAD PIXEL TEST
                item {
                    TouchGridModule(
                        viewModel = viewModel,
                        onOpenDeadPixelTest = { showDeadPixelModal = true }
                    )
                }

                // MODULE 2: CAMERA & STROBE FLASH TEST
                item {
                    CameraFlashModule(viewModel = viewModel)
                }

                // MODULE 3: AUDIO & MIC LOOPBACK FREQUENCY
                item {
                    AudioMicModule(viewModel = viewModel)
                }

                // MODULE 4: BATTERY HEALTH & BMS CYCLE
                item {
                    BatteryBmsModule(viewModel = viewModel)
                }

                // STICKY HEADER 3: ROOM OFFLINE ARCHIVE
                stickyHeader {
                    StickyCategoryHeader(title = "Offline SQLite Diagnostic Archive")
                }

                // MODULE 5: SAVE TO OFFLINE ROOM DATABASE
                item {
                    SaveDiagnosticRecordModule(viewModel = viewModel)
                }

                // MODULE 6: OFFLINE SAVED DIAGNOSTIC REPORTS
                item {
                    DiagnosticHistoryModule(viewModel = viewModel)
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

    if (showDeadPixelModal) {
        DeadPixelColorDialog(onDismiss = { showDeadPixelModal = false })
    }
}

@Composable
fun RadarScannerCard(
    isScanning: Boolean,
    progress: Float,
    stageText: String,
    onStartScan: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isScanning) CyberCyan else AzureBlue.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF07101E))
                    .border(2.dp, CyberCyan.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2

                    // Concentric rings
                    drawCircle(color = Color(0x3300F5D4), radius = radius * 0.33f, style = Stroke(1.dp.toPx()))
                    drawCircle(color = Color(0x3300F5D4), radius = radius * 0.66f, style = Stroke(1.dp.toPx()))
                    drawCircle(color = Color(0x3300F5D4), radius = radius * 0.95f, style = Stroke(1.dp.toPx()))

                    // Expanding sonar pulse
                    if (isScanning) {
                        drawCircle(
                            color = CyberCyan.copy(alpha = (1f - pulseRadius).coerceIn(0f, 0.6f)),
                            radius = radius * pulseRadius,
                            style = Stroke(2.dp.toPx())
                        )
                    }

                    // Rotating radar beam
                    if (isScanning) {
                        val rad = Math.toRadians(angle.toDouble())
                        val endX = center.x + radius * cos(rad).toFloat()
                        val endY = center.y + radius * sin(rad).toFloat()
                        drawLine(
                            color = CyberCyan,
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Center dot
                    drawCircle(color = CyberCyan, radius = 4.dp.toPx(), center = center)
                }

                if (!isScanning) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Radar",
                        tint = CyberCyan,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stageText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isScanning) CyberCyan else TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isScanning) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyberCyan,
                    trackColor = Color(0x33FFFFFF)
                )
            } else {
                NeonButton(
                    text = "Start 2-Minute Full Scan",
                    onClick = onStartScan,
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ObsidianBackground) },
                    testTag = "start_scan_btn"
                )
            }
        }
    }
}

@Composable
fun HealthScoreBanner(
    score: Int,
    onSyncToValuation: () -> Unit
) {
    val scoreColor = when {
        score >= 85 -> EmeraldPass
        score >= 70 -> AmberWarning
        else -> CrimsonFail
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = scoreColor.copy(alpha = 0.5f),
        backgroundColor = Color(0xFF111D30)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(scoreColor.copy(alpha = 0.15f))
                        .border(2.dp, scoreColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$score",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Calculated Health Score",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (score >= 85) "Grade A+ (Pristine Condition)" else "Grade B (Minor Service Needed)",
                        fontSize = 11.sp,
                        color = scoreColor
                    )
                }
            }

            NeonButton(
                text = "Sync to Valuation",
                onClick = onSyncToValuation,
                modifier = Modifier.padding(start = 4.dp),
                testTag = "sync_valuation_btn"
            )
        }
    }
}

@Composable
fun TouchGridModule(
    viewModel: PhoneKhojViewModel,
    onOpenDeadPixelTest: () -> Unit
) {
    val grid by viewModel.touchGrid.collectAsState()
    val passedCount by viewModel.touchGridProgress.collectAsState()
    val total = viewModel.totalCells
    val percent = ((passedCount.toFloat() / total.toFloat()) * 100).toInt()

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Touch & Digitizer Grid",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "$passedCount / $total ($percent%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (percent >= 90) EmeraldPass else AmberWarning
                )
            }

            Text(
                text = "Tap or drag across cells to test responsiveness and identify dead touch zones.",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 6 columns x 8 rows interactive touch canvas / grid
            var gridWidthPx by remember { mutableStateOf(1f) }
            var gridHeightPx by remember { mutableStateOf(1f) }
            val cols = 6
            val rows = 8

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090E17))
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .onGloballyPositioned {
                        gridWidthPx = it.size.width.toFloat()
                        gridHeightPx = it.size.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val pos = change.position
                            val c = (pos.x / (gridWidthPx / cols)).toInt().coerceIn(0, cols - 1)
                            val r = (pos.y / (gridHeightPx / rows)).toInt().coerceIn(0, rows - 1)
                            val idx = r * cols + c
                            viewModel.onCellTouched(idx)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { pos ->
                            val c = (pos.x / (gridWidthPx / cols)).toInt().coerceIn(0, cols - 1)
                            val r = (pos.y / (gridHeightPx / rows)).toInt().coerceIn(0, rows - 1)
                            val idx = r * cols + c
                            viewModel.onCellTouched(idx)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellW = size.width / cols
                    val cellH = size.height / rows

                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            val idx = r * cols + c
                            val active = grid.getOrNull(idx) ?: false
                            val rectOffset = Offset(c * cellW, r * cellH)
                            val rectSize = Size(cellW - 2.dp.toPx(), cellH - 2.dp.toPx())

                            drawRect(
                                color = if (active) CyberCyan.copy(alpha = 0.85f) else Color(0xFF1E293B),
                                topLeft = rectOffset,
                                size = rectSize
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Fill All, Reset, Dead Pixel Fullscreen Screen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonButton(
                    text = "Quick Pass (100%)",
                    onClick = { viewModel.fillTouchGrid() },
                    isSecondary = true,
                    modifier = Modifier.weight(1f),
                    testTag = "fill_touch_grid_btn"
                )
                NeonButton(
                    text = "Reset Grid",
                    onClick = { viewModel.resetTouchGrid() },
                    isSecondary = true,
                    modifier = Modifier.weight(1f),
                    testTag = "reset_touch_grid_btn"
                )
                NeonButton(
                    text = "RGB Pixel Screen",
                    onClick = onOpenDeadPixelTest,
                    modifier = Modifier.weight(1.2f),
                    testTag = "rgb_pixel_btn"
                )
            }
        }
    }
}

@Composable
fun CameraFlashModule(viewModel: PhoneKhojViewModel) {
    val autofocusPassed by viewModel.cameraAutofocusPassed.collectAsState()
    val flashPassed by viewModel.cameraFlashPassed.collectAsState()
    val torchOn by viewModel.cameraTorchOn.collectAsState()
    val lens by viewModel.cameraLens.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "focus")
    val focusPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "focus_pulse"
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Camera & Strobe Flash Test",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                StatusBadge(status = if (autofocusPassed && flashPassed) "PASS" else "WARNING")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simulated Viewfinder Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (torchOn) Color(0xFFF1F5F9) else Color(0xFF0F172A))
                    .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (torchOn) {
                    Text(
                        text = "⚡ STROBE TORCH FLASH ACTIVE ⚡",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                } else {
                    // Autofocus reticle
                    Canvas(modifier = Modifier.size(70.dp * focusPulse)) {
                        drawCircle(
                            color = CyberCyan,
                            radius = size.width / 2,
                            style = Stroke(2.dp.toPx())
                        )
                        drawLine(
                            color = CyberCyan,
                            start = Offset(size.width / 2, 0f),
                            end = Offset(size.width / 2, 16.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = CyberCyan,
                            start = Offset(size.width / 2, size.height),
                            end = Offset(size.width / 2, size.height - 16.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = CyberCyan,
                            start = Offset(0f, size.height / 2),
                            end = Offset(16.dp.toPx(), size.height / 2),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = CyberCyan,
                            start = Offset(size.width, size.height / 2),
                            end = Offset(size.width - 16.dp.toPx(), size.height / 2),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Watermark
                    Text(
                        text = "VIEWFINDER SIMULATOR • ${lens} SENSOR",
                        color = Color(0x80FFFFFF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Camera Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeonButton(
                        text = "Switch Lens ($lens)",
                        onClick = { viewModel.switchCameraLens() },
                        isSecondary = true,
                        icon = { Icon(Icons.Default.FlipCameraAndroid, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp)) }
                    )
                    NeonButton(
                        text = if (torchOn) "Flash OFF" else "Flash ON",
                        onClick = { viewModel.toggleCameraFlash() },
                        isSecondary = !torchOn,
                        icon = { Icon(Icons.Default.FlashOn, contentDescription = null, tint = if (torchOn) ObsidianBackground else CyberCyan, modifier = Modifier.size(16.dp)) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Autofocus: ", fontSize = 11.sp, color = TextSecondary)
                    Switch(
                        checked = autofocusPassed,
                        onCheckedChange = { viewModel.cameraAutofocusPassed.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = AzureBlue)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioMicModule(viewModel: PhoneKhojViewModel) {
    val speakerPassed by viewModel.audioSpeakerPassed.collectAsState()
    val micPassed by viewModel.audioMicPassed.collectAsState()
    val waveLevels by viewModel.audioWaveLevels.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "audio_anim")
    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_shift"
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Audio & Mic Loopback Analyzer",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                StatusBadge(status = if (speakerPassed && micPassed) "PASS" else "WARNING")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sound frequency wave visualizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF090E17))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                waveLevels.forEachIndexed { i, baseLevel ->
                    val factor = if (i % 2 == 0) waveShift else (1.3f - waveShift)
                    val barHeight = (44f * (baseLevel * factor).coerceIn(0.15f, 1f)).dp

                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.verticalGradient(listOf(CyberCyan, AzureBlue)))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Stereo Speaker", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = speakerPassed,
                        onCheckedChange = { viewModel.audioSpeakerPassed.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = AzureBlue)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Mic Input", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = micPassed,
                        onCheckedChange = { viewModel.audioMicPassed.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = AzureBlue)
                    )
                }
            }
        }
    }
}

@Composable
fun BatteryBmsModule(viewModel: PhoneKhojViewModel) {
    val capacity by viewModel.batteryCapacity.collectAsState()
    val cycles by viewModel.batteryCycleCount.collectAsState()
    val temp by viewModel.batteryTemperature.collectAsState()
    val status by viewModel.batteryStatus.collectAsState()

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Battery Health & Cycle Degradation",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                StatusBadge(status = if (capacity >= 80) "GOOD" else "DEGRADED")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Gauge
                Box(
                    modifier = Modifier
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { capacity / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = if (capacity >= 80) CyberCyan else AmberWarning,
                        strokeWidth = 7.dp,
                        trackColor = Color(0x33FFFFFF)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$capacity%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "HEALTH",
                            fontSize = 8.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Battery Stats
                Column(
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Cycle Count:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "$cycles cycles", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Temperature:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${temp}°C (Safe)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPass)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Verdict:", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = if (capacity >= 80) "Optimal (No Replacement)" else "Service Recommended",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (capacity >= 80) CyberCyan else AmberWarning
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadPixelColorDialog(onDismiss: () -> Unit) {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
    var selectedIndex by remember { mutableStateOf(0) }

    BasicAlertDialog(onDismissRequest = onDismiss, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[selectedIndex])
                .clickable {
                    selectedIndex = (selectedIndex + 1) % colors.size
                }
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Screen ${selectedIndex + 1} of 5 • Tap to cycle color",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Look for dark spots, discolored dots, or flickering lines.",
                    color = Color(0xCCFFFFFF),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                NeonButton(text = "Done / Exit Test", onClick = onDismiss, isSecondary = true)
            }
        }
    }
}

@Composable
fun SaveDiagnosticRecordModule(viewModel: PhoneKhojViewModel) {
    var deviceModel by remember { mutableStateOf("iPhone 14 Pro 128GB") }
    var imei by remember { mutableStateOf("359284110948201") }
    var technicianNotes by remember { mutableStateOf("Hardware sensors and digitizer certified via automated test suite.") }
    val saveMessage by viewModel.saveDiagnosticSuccess.collectAsState()

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save QC Certificate to Room DB",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                StatusBadge(status = "OFFLINE SQLITE")
            }

            Text(
                text = "Persist hardware diagnostic results locally on device for offline records and trade-in proof",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (saveMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldPass.copy(alpha = 0.2f))
                        .border(1.dp, EmeraldPass, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "✅ ${saveMessage ?: ""}",
                        color = EmeraldPass,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = deviceModel,
                onValueChange = { deviceModel = it },
                label = { Text("Device Model & Storage") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = CyberCyan
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = imei,
                onValueChange = { imei = it },
                label = { Text("15-Digit Device IMEI") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = CyberCyan
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            NeonButton(
                text = "💾 Persist Diagnostic Record (Offline SQLite)",
                onClick = {
                    viewModel.saveCurrentDiagnostic(deviceModel, imei, technicianNotes)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DiagnosticHistoryModule(viewModel: PhoneKhojViewModel) {
    val history by viewModel.diagnosticHistory.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Saved Diagnostic Reports (${history.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x330066FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "SQLite Room DB", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Historical diagnostic logs stored safely on this device",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No diagnostic reports saved yet. Run a diagnostic scan above to save.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    history.forEach { record ->
                        DiagnosticHistoryItem(
                            record = record,
                            dateStr = dateFormat.format(Date(record.timestamp)),
                            onDelete = { viewModel.deleteDiagnostic(record) },
                            onSelectForInvoice = {
                                viewModel.invoiceDeviceModel.value = record.deviceModel
                                viewModel.invoiceImei.value = record.imei
                                viewModel.invoiceRepairLog.value = "Hardware Diagnostic Health: ${record.healthScore}%\nCondition: ${record.conditionGrade}\nSensor Status: Camera ${record.cameraStatus}, Audio ${record.audioStatus}, Battery ${record.batteryCapacity}%\nNotes: ${record.technicianNotes}"
                                viewModel.selectedTab.value = 4 // switch to invoice tab
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticHistoryItem(
    record: DiagnosticRecord,
    dateStr: String,
    onDelete: () -> Unit,
    onSelectForInvoice: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22131D33))
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.deviceModel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "IMEI: ${record.imei} • $dateStr",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Health Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (record.healthScore >= 90) EmeraldPass.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f))
                        .border(1.dp, if (record.healthScore >= 90) EmeraldPass else AmberWarning, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${record.healthScore}/100 • ${record.conditionGrade}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.healthScore >= 90) EmeraldPass else AmberWarning
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pills of test items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SmallStatusChip(label = "Touch ${record.touchGridScore}/48")
                SmallStatusChip(label = "Cam: ${record.cameraStatus}")
                SmallStatusChip(label = "Audio: ${record.audioStatus}")
                SmallStatusChip(label = "Bat: ${record.batteryCapacity}%")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notes: ${record.technicianNotes}",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onSelectForInvoice) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Create Bill", fontSize = 11.sp, color = CyberCyan)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete record", tint = CrimsonFail, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SmallStatusChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x3300F5D4))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
    }
}

