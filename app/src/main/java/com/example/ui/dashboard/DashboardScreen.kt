package com.example.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.components.BackToTopButton
import com.example.ui.components.CopyIconButton
import com.example.ui.components.ExpandableFaqSection
import com.example.ui.components.ScrollProgressBar
import com.example.ui.components.SkipToContentChip
import com.example.ui.components.formatPassivePrice
import com.example.ui.components.formatPassiveText
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InventoryItem
import com.example.ui.components.PulsingSyncIndicator
import com.example.ui.components.StatusBadge
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
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightCardGlass
import com.example.ui.theme.LightCardSubtle
import com.example.ui.scanner.CameraQrScannerSheet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.util.SancharSaathiEngine
import com.example.util.SecurityUtils
import com.example.viewmodel.PhoneKhojViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val allItems by viewModel.allInventory.collectAsState(initial = emptyList())
    val activeStock by viewModel.activeStockCount.collectAsState()
    val capitalLocked by viewModel.totalCapitalLocked.collectAsState()
    val realizedProfit by viewModel.totalRealizedProfit.collectAsState()
    val inRepairCount by viewModel.unitsInRepairCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedBrandFilter by remember { mutableStateOf("ALL") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var selectedSort by remember { mutableStateOf("NEWEST") } // NEWEST, PRICE_HIGH, PRICE_LOW, HEALTH_HIGH

    var showAddDialog by remember { mutableStateOf(false) }
    var showCsvImportDialog by remember { mutableStateOf(false) }
    var showScannerSheet by remember { mutableStateOf(false) }
    var prefilledScannedImei by remember { mutableStateOf<String?>(null) }

    // Inventory Count Computations
    val totalCount = allItems.size
    val inStockUnits = allItems.count { it.status == "IN_STOCK" }
    val inRepairUnits = allItems.count { it.status == "IN_REPAIR" }
    val soldUnits = allItems.count { it.status == "SOLD" }

    val gradeAPlusCount = allItems.count { it.conditionGrade == "A+" }
    val gradeACount = allItems.count { it.conditionGrade == "A" }
    val gradeBCount = allItems.count { it.conditionGrade == "B" || it.conditionGrade == "Fair" }

    val totalCapital = capitalLocked ?: allItems.filter { it.status != "SOLD" }.sumOf { it.purchasePrice + it.repairCost }
    val totalProfit = realizedProfit ?: allItems.filter { it.status == "SOLD" }.sumOf { it.targetSalePrice - it.purchasePrice - it.repairCost }

    // Brand Distribution
    val brandCounts = remember(allItems) {
        allItems.filter { it.status != "SOLD" }.groupBy { it.brand }.mapValues { it.value.size }
    }

    // Filter and Sort Recent Entries
    val filteredRecentEntries = remember(allItems, searchQuery, selectedBrandFilter, selectedStatusFilter, selectedSort) {
        var list = allItems.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.model.contains(searchQuery, ignoreCase = true) ||
                    item.brand.contains(searchQuery, ignoreCase = true) ||
                    item.imei.contains(searchQuery, ignoreCase = true)
            val matchesBrand = selectedBrandFilter == "ALL" || item.brand.equals(selectedBrandFilter, ignoreCase = true)
            val matchesStatus = selectedStatusFilter == "ALL" || item.status.equals(selectedStatusFilter, ignoreCase = true)
            matchesQuery && matchesBrand && matchesStatus
        }

        when (selectedSort) {
            "PRICE_HIGH" -> list.sortedByDescending { it.targetSalePrice }
            "PRICE_LOW" -> list.sortedBy { it.targetSalePrice }
            "HEALTH_HIGH" -> list.sortedByDescending { it.healthScore }
            else -> list.sortedByDescending { it.createdAt }
        }
    }

    val isPassive by viewModel.isPassiveVisitMode.collectAsState()
    val lastUpdate by viewModel.lastUpdateTimestamp.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showBackToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 }
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
            .background(LightBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScrollProgressBar(progress = scrollProgress)

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackground),
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { showAddDialog = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add Phone") },
                        text = { Text("Intake Phone", fontWeight = FontWeight.Bold) },
                        containerColor = AzureBlue,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(bottom = 60.dp)
                            .testTag("fab_add_device")
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBackground)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // -------------------------------------------------------------
                    // STORE BANNER & QUICK ACTIONS
                    // -------------------------------------------------------------
                    item {
                        StoreHeaderCard(
                            shopName = user?.shopName ?: "RefurbIQ - Apex Mobile Hub",
                            ownerName = user?.ownerName ?: "Store Manager",
                            lastUpdate = lastUpdate,
                            onExportCsv = {
                                val csv = viewModel.exportInventoryCsv()
                                shareCsvIntent(context, csv)
                            },
                            onImportCsv = { showCsvImportDialog = true },
                            onScanBoxQr = { showScannerSheet = true },
                            onPrint = { shareInventoryPrintSheet(context, allItems) },
                            onSkipToContent = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(2)
                                }
                            }
                        )
                    }

                    // -------------------------------------------------------------
                    // SECTION 1: MATERIAL 3 INVENTORY COUNTS SUMMARY
                    // -------------------------------------------------------------
                    item {
                        InventoryCountsSummaryCard(
                            totalCount = totalCount,
                            inStockCount = inStockUnits,
                            inRepairCount = inRepairUnits,
                            soldCount = soldUnits,
                            totalCapital = totalCapital,
                            realizedProfit = totalProfit,
                            gradeAPlusCount = gradeAPlusCount,
                            gradeACount = gradeACount,
                            gradeBCount = gradeBCount,
                            brandCounts = brandCounts,
                            selectedBrand = selectedBrandFilter,
                            onSelectBrand = { selectedBrandFilter = it }
                        )
                    }

                    // -------------------------------------------------------------
                    // SECTION 2: SEARCH & FILTER CONTROLS FOR RECENT ENTRIES (STICKY)
                    // -------------------------------------------------------------
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                RecentEntriesHeader(
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    selectedStatus = selectedStatusFilter,
                                    onSelectStatus = { selectedStatusFilter = it },
                                    selectedSort = selectedSort,
                                    onSelectSort = { selectedSort = it },
                                    totalFiltered = filteredRecentEntries.size
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // SECTION 3: LIST OF RECENT DEVICE ENTRIES
                    // -------------------------------------------------------------
                    if (filteredRecentEntries.isEmpty()) {
                        item {
                            EmptyRecentEntriesCard(
                                onResetFilters = {
                                    searchQuery = ""
                                    selectedBrandFilter = "ALL"
                                    selectedStatusFilter = "ALL"
                                }
                            )
                        }
                    } else {
                        items(filteredRecentEntries, key = { it.id }) { item ->
                            RecentDeviceEntryCard(
                                item = item,
                                isPassive = isPassive,
                                onInspectSancharSaathi = {
                                    viewModel.runSancharSaathiInspection(item.imei)
                                    viewModel.selectedTab.value = 2 // Tab 2: Sanchar Saathi
                                },
                                onRunDiagnostics = {
                                    viewModel.diagDeviceModel.value = "${item.brand} ${item.model}"
                                    viewModel.diagImei.value = item.imei
                                    viewModel.selectedTab.value = 1 // Tab 1: Diagnostics
                                },
                                onGenerateInvoice = {
                                    viewModel.selectItemForInvoice(item)
                                },
                                onWhatsAppCatalogShare = {
                                    shareWhatsAppCatalog(context, item)
                                },
                                onUpdateStatus = { newStatus ->
                                    viewModel.updateDeviceStatus(item, newStatus)
                                    Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    // -------------------------------------------------------------
                    // SECTION 4: EXPANDABLE KNOWLEDGE BASE & FAQ
                    // -------------------------------------------------------------
                    item {
                        ExpandableFaqSection()
                    }
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
                .padding(bottom = 125.dp, end = 16.dp)
        )
    }

    // Add Device Dialog
    if (showAddDialog) {
        MaterialAddDeviceDialog(
            initialImei = prefilledScannedImei,
            onDismiss = {
                showAddDialog = false
                prefilledScannedImei = null
            },
            onAdd = { newItem ->
                viewModel.addInventoryItem(newItem)
                showAddDialog = false
                prefilledScannedImei = null
                Toast.makeText(context, "${newItem.brand} ${newItem.model} recorded in inventory!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Camera QR Scanner Sheet for Dashboard Quick Box Scanning
    if (showScannerSheet) {
        CameraQrScannerSheet(
            onDismiss = { showScannerSheet = false },
            onImeiScanned = { scannedImei ->
                showScannerSheet = false
                prefilledScannedImei = scannedImei
                showAddDialog = true
                Toast.makeText(context, "Scanned box IMEI: $scannedImei", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // CSV Bulk Import Dialog
    if (showCsvImportDialog) {
        MaterialCsvImportDialog(
            onDismiss = { showCsvImportDialog = false },
            onImport = { content ->
                viewModel.importCsv(content) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) showCsvImportDialog = false
                }
            }
        )
    }
}

fun shareInventoryPrintSheet(context: Context, items: List<InventoryItem>) {
    val sb = StringBuilder()
    sb.append("=========================================\n")
    sb.append("      REFURBIQ CERTIFIED INVENTORY       \n")
    sb.append("  Apex Mobile Hub • Last Sync: Oct 02, 2026\n")
    sb.append("=========================================\n\n")
    items.forEachIndexed { idx, item ->
        sb.append("${idx + 1}. ${item.brand} ${item.model} (${item.storage})\n")
        sb.append("   IMEI: ${SecurityUtils.formatImei(item.imei)} [${item.ceirStatus}]\n")
        sb.append("   Grade: ${item.conditionGrade} | Status: ${item.status}\n")
        sb.append("   Cost: ₹${item.purchasePrice.toLong()} | Target: ₹${item.targetSalePrice.toLong()}\n")
        sb.append("   Health: ${item.healthScore}% | QC: ${item.defectsSummary}\n\n")
    }
    sb.append("-----------------------------------------\n")
    sb.append("Total Units: ${items.size}\n")
    sb.append("RefurbIQ DoT Certified Registry System\n")

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "RefurbIQ Inventory Print Sheet")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Print / Export Inventory Sheet"))
}

// -------------------------------------------------------------------------
// COMPONENT 1: STORE HEADER WITH ACTIONS
// -------------------------------------------------------------------------
@Composable
fun StoreHeaderCard(
    shopName: String,
    ownerName: String,
    lastUpdate: String = "Oct 02, 2026",
    onExportCsv: () -> Unit,
    onImportCsv: () -> Unit,
    onScanBoxQr: () -> Unit,
    onPrint: () -> Unit = {},
    onSkipToContent: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = EmeraldPass
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = shopName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "Operator: $ownerName • Sync: $lastUpdate",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onScanBoxQr,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzureBlue, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_dashboard_scan_qr")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Box QR", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan Box", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = onPrint,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightCardSubtle)
                            .testTag("btn_dashboard_print")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print Sheet", tint = AzureBlue, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onExportCsv,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightCardSubtle)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export CSV", tint = AzureBlue, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onImportCsv,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightCardSubtle)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = "Import CSV", tint = AzureBlue, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkipToContentChip(onClick = onSkipToContent)
                Text(
                    text = "RefurbIQ Telemetry Engine v2.4",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPONENT 2: MATERIAL 3 INVENTORY COUNTS SUMMARY CARD
// -------------------------------------------------------------------------
@Composable
fun InventoryCountsSummaryCard(
    totalCount: Int,
    inStockCount: Int,
    inRepairCount: Int,
    soldCount: Int,
    totalCapital: Double,
    realizedProfit: Double,
    gradeAPlusCount: Int,
    gradeACount: Int,
    gradeBCount: Int,
    brandCounts: Map<String, Int>,
    selectedBrand: String,
    onSelectBrand: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Inventory Telemetry & Summary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$totalCount Total Devices Tracked in System",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                PulsingSyncIndicator(text = "DoT Active")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Pillar Status Metric Counts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // In Stock Metric
                StatusMetricPillar(
                    title = "IN STOCK",
                    count = inStockCount,
                    caption = "Ready for Sale",
                    accentColor = EmeraldPass,
                    bgColor = EmeraldPassBg,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )

                // In Repair Metric
                StatusMetricPillar(
                    title = "IN REPAIR",
                    count = inRepairCount,
                    caption = "QC & Service",
                    accentColor = AmberWarning,
                    bgColor = AmberWarningBg,
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f)
                )

                // Sold Metric
                StatusMetricPillar(
                    title = "SOLD",
                    count = soldCount,
                    caption = "Delivered",
                    accentColor = AzureBlue,
                    bgColor = Color(0xFFEFF6FF),
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Financial Summary Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = LightCardSubtle,
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "CAPITAL INVESTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(text = SecurityUtils.formatCurrency(totalCapital), fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }

                    Box(modifier = Modifier.height(28.dp).width(1.dp).background(Color(0xFFCBD5E1)))

                    Column {
                        Text(text = "REALIZED PROFIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldPass)
                        Text(text = "+${SecurityUtils.formatCurrency(realizedProfit)}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = EmeraldPass)
                    }

                    Box(modifier = Modifier.height(28.dp).width(1.dp).background(Color(0xFFCBD5E1)))

                    Column {
                        Text(text = "AVG MARGIN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(text = "23.4%", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AzureBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inventory Readiness Linear Progress Bar
            val readyRatio = if (totalCount > 0) inStockCount.toFloat() / totalCount else 0.8f
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Stock Readiness Ratio", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Text(text = "${(readyRatio * 100).toInt()}% Ready for Sale", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPass)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { readyRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EmeraldPass,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quality Grade Distribution Chips
            Text(text = "Condition Grade Breakdown", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GradeChip(label = "Grade A+ (Pristine)", count = gradeAPlusCount, color = EmeraldPass, modifier = Modifier.weight(1f))
                GradeChip(label = "Grade A (Like New)", count = gradeACount, color = AzureBlue, modifier = Modifier.weight(1f))
                GradeChip(label = "Grade B (Good)", count = gradeBCount, color = AmberWarning, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Brand Distribution Carousel
            Text(text = "Filter Stock By Brand", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isAll = selectedBrand == "ALL"
                    FilterChip(
                        selected = isAll,
                        onClick = { onSelectBrand("ALL") },
                        label = { Text("All Brands ($totalCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzureBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(brandCounts.toList()) { (brand, count) ->
                    val isSel = selectedBrand.equals(brand, ignoreCase = true)
                    FilterChip(
                        selected = isSel,
                        onClick = { onSelectBrand(if (isSel) "ALL" else brand) },
                        label = { Text("$brand ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzureBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatusMetricPillar(
    title: String,
    count: Int,
    caption: String,
    accentColor: Color,
    bgColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Black, color = accentColor)
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(text = caption, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
fun GradeChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(text = "$count Units", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 9.sp, color = TextMuted, maxLines = 1)
        }
    }
}

// -------------------------------------------------------------------------
// COMPONENT 3: RECENT ENTRIES HEADER & SEARCH
// -------------------------------------------------------------------------
@Composable
fun RecentEntriesHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedStatus: String,
    onSelectStatus: (String) -> Unit,
    selectedSort: String,
    onSelectSort: (String) -> Unit,
    totalFiltered: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Recent Device Entries",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "$totalFiltered Devices",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzureBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            var showSortMenu by remember { mutableStateOf(false) }
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showSortMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    color = LightCardSubtle,
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = AzureBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Sort", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                    DropdownMenuItem(text = { Text("Newest Added First") }, onClick = { onSelectSort("NEWEST"); showSortMenu = false })
                    DropdownMenuItem(text = { Text("Price: High to Low") }, onClick = { onSelectSort("PRICE_HIGH"); showSortMenu = false })
                    DropdownMenuItem(text = { Text("Price: Low to High") }, onClick = { onSelectSort("PRICE_LOW"); showSortMenu = false })
                    DropdownMenuItem(text = { Text("Highest QC Health Score") }, onClick = { onSelectSort("HEALTH_HIGH"); showSortMenu = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by model, brand, or IMEI...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AzureBlue) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AzureBlue,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Segmented Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "All Status", "IN_STOCK" to "In Stock", "IN_REPAIR" to "In Repair", "SOLD" to "Sold").forEach { (key, label) ->
                val isSel = selectedStatus == key
                FilterChip(
                    selected = isSel,
                    onClick = { onSelectStatus(key) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (key == "IN_STOCK") EmeraldPass else if (key == "IN_REPAIR") AmberWarning else AzureBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPONENT 4: RECENT DEVICE ENTRY CARD (MATERIAL 3)
// -------------------------------------------------------------------------
@Composable
fun RecentDeviceEntryCard(
    item: InventoryItem,
    isPassive: Boolean = false,
    onInspectSancharSaathi: () -> Unit,
    onRunDiagnostics: () -> Unit,
    onGenerateInvoice: () -> Unit,
    onWhatsAppCatalogShare: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showStatusMenu by remember { mutableStateOf(false) }

    val profitMargin = item.targetSalePrice - item.purchasePrice - item.repairCost
    val profitPercentage = if (item.purchasePrice > 0) ((profitMargin / item.purchasePrice) * 100).toInt() else 20

    val brandColor = when (item.brand.lowercase()) {
        "apple" -> Color(0xFF1E293B)
        "samsung" -> Color(0xFF1D4ED8)
        "google" -> Color(0xFF0F766E)
        "oneplus" -> Color(0xFFDC2626)
        else -> AzureBlue
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Brand Avatar, Title, Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = brandColor.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.brand.take(2).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = brandColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${item.brand} ${item.model}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${item.storage} • ${item.color} • Grade ${item.conditionGrade}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Interactive Status Badge with Menu
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showStatusMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        color = when (item.status) {
                            "IN_STOCK" -> EmeraldPassBg
                            "IN_REPAIR" -> AmberWarningBg
                            else -> Color(0xFFEFF6FF)
                        },
                        border = BorderStroke(
                            1.dp,
                            when (item.status) {
                                "IN_STOCK" -> EmeraldPass
                                "IN_REPAIR" -> AmberWarning
                                else -> AzureBlue
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (item.status) {
                                    "IN_STOCK" -> "IN STOCK ▼"
                                    "IN_REPAIR" -> "IN REPAIR ▼"
                                    else -> "SOLD ▼"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = when (item.status) {
                                    "IN_STOCK" -> EmeraldPass
                                    "IN_REPAIR" -> AmberWarning
                                    else -> AzureBlue
                                }
                            )
                        }
                    }

                    DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                        DropdownMenuItem(text = { Text("Mark IN STOCK") }, onClick = { onUpdateStatus("IN_STOCK"); showStatusMenu = false })
                        DropdownMenuItem(text = { Text("Mark IN REPAIR") }, onClick = { onUpdateStatus("IN_REPAIR"); showStatusMenu = false })
                        DropdownMenuItem(text = { Text("Mark SOLD") }, onClick = { onUpdateStatus("SOLD"); showStatusMenu = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IMEI Row & Sanchar Saathi Verification Indicator
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = LightCardSubtle
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(item.imei))
                            Toast.makeText(context, "IMEI Copied: ${item.imei}", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "IMEI: ${formatPassiveText(SecurityUtils.formatImei(item.imei), isPassive, "3592 •••• •••• 201")}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CopyIconButton(textToCopy = item.imei, label = "IMEI")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldPass, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "DoT NOC Clean", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPass)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Margins Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "BUY / COST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = formatPassivePrice(item.purchasePrice, isPassive), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }

                Column {
                    Text(text = "TARGET SALE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = formatPassivePrice(item.targetSalePrice, isPassive), fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPassBg
                ) {
                    Text(
                        text = if (isPassive) "Profit Protected" else "+₹${profitMargin.toInt()} ($profitPercentage%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPass,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Strip (Material 3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Sanchar Saathi Button
                AssistChip(
                    onClick = onInspectSancharSaathi,
                    label = { Text("Sanchar Saathi", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzureBlue) },
                    modifier = Modifier.weight(1f)
                )

                // 2-Min QC Diagnostics
                AssistChip(
                    onClick = onRunDiagnostics,
                    label = { Text("QC Test", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp), tint = AmberWarning) },
                    modifier = Modifier.weight(1f)
                )

                // Invoice Bill
                AssistChip(
                    onClick = onGenerateInvoice,
                    label = { Text("Invoice", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp), tint = ElectricIndigo) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EmptyRecentEntriesCard(onResetFilters: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "No matching devices found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "Try clearing search keywords or active status filters.", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onResetFilters) {
                Text(text = "Reset All Filters", fontWeight = FontWeight.Bold, color = AzureBlue)
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPONENT 5: MATERIAL 3 ADD DEVICE DIALOG
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialAddDeviceDialog(
    initialImei: String? = null,
    onDismiss: () -> Unit,
    onAdd: (InventoryItem) -> Unit
) {
    var showDialogCameraScanner by remember { mutableStateOf(false) }

    val initialReport = remember(initialImei) {
        if (!initialImei.isNullOrBlank()) SancharSaathiEngine.inspectImei(initialImei) else null
    }

    var brand by remember(initialImei) { mutableStateOf(initialReport?.brand?.takeIf { it != "Unknown / Altered" } ?: "Apple") }
    var model by remember(initialImei) { mutableStateOf(initialReport?.modelName?.takeIf { !it.contains("Unverified") } ?: "iPhone 15 Pro") }
    var storage by remember(initialImei) {
        mutableStateOf(initialReport?.ramStorage?.split("|")?.lastOrNull()?.trim() ?: "256GB")
    }
    var color by remember { mutableStateOf("Natural Titanium") }
    var imei by remember(initialImei) { mutableStateOf(initialImei ?: "353084110948201") }
    var purchasePrice by remember(initialImei) {
        mutableStateOf(
            if (initialReport?.brand?.contains("Apple") == true) "54000"
            else if (initialReport?.brand?.contains("Samsung") == true) "49000"
            else if (initialReport?.brand?.contains("Google") == true) "42000"
            else "24000"
        )
    }
    var targetSalePrice by remember(initialImei) {
        mutableStateOf(
            if (initialReport?.brand?.contains("Apple") == true) "65999"
            else if (initialReport?.brand?.contains("Samsung") == true) "59999"
            else if (initialReport?.brand?.contains("Google") == true) "51999"
            else "29999"
        )
    }
    var grade by remember { mutableStateOf("A+") }
    var showConfirmIntakeModal by remember { mutableStateOf(false) }

    val isImeiValidLength = imei.length == 15
    val isImeiLuhnValid = SancharSaathiEngine.validateLuhn(imei)
    val isImeiError = imei.isNotEmpty() && (!isImeiValidLength || !isImeiLuhnValid)
    val isImeiSuccess = isImeiValidLength && isImeiLuhnValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Intake New Phone Entry",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "Retail Box Intake",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzureBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Record incoming refurbished or pre-owned mobile into shop inventory. Scan retail packaging QR or enter IMEI to auto-populate specifications.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // IMEI Input Row with Box QR Scanner Trigger Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = imei,
                        onValueChange = {
                            if (it.length <= 15) {
                                imei = it.filter { ch -> ch.isDigit() }
                                if (imei.length == 15) {
                                    val rep = SancharSaathiEngine.inspectImei(imei)
                                    if (rep.brand != "Unknown / Altered") {
                                        brand = rep.brand
                                        model = rep.modelName
                                        storage = rep.ramStorage.split("|").lastOrNull()?.trim() ?: storage
                                    }
                                }
                            }
                        },
                        label = { Text("15-Digit IMEI") },
                        singleLine = true,
                        isError = isImeiError,
                        supportingText = {
                            if (isImeiError) {
                                Text("Must be 15 digits with valid Luhn checksum", color = CrimsonFail, fontSize = 10.sp)
                            } else if (isImeiSuccess) {
                                Text("✓ Valid DoT IMEI with verified TAC", color = EmeraldPass, fontSize = 10.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("dialog_input_imei")
                    )

                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDialogCameraScanner = true }
                            .testTag("btn_dialog_scan_box_qr"),
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = storage,
                        onValueChange = { storage = it },
                        label = { Text("Storage") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = grade,
                        onValueChange = { grade = it },
                        label = { Text("Grade") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = purchasePrice,
                        onValueChange = { purchasePrice = it },
                        label = { Text("Cost (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetSalePrice,
                        onValueChange = { targetSalePrice = it },
                        label = { Text("Target (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showConfirmIntakeModal = true
                },
                enabled = !isImeiError && imei.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AzureBlue),
                modifier = Modifier.testTag("dialog_btn_submit_intake")
            ) {
                Text("Add Device", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )

    // Confirmation Modal before Intake
    if (showConfirmIntakeModal) {
        val p = purchasePrice.toDoubleOrNull() ?: 10000.0
        val s = targetSalePrice.toDoubleOrNull() ?: (p * 1.25)
        AlertDialog(
            onDismissRequest = { showConfirmIntakeModal = false },
            title = {
                Text("Confirm Device Intake", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to intake $brand $model ($storage, Grade $grade) with purchase cost ₹${p.toLong()} and target resale ₹${s.toLong()}?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newItem = InventoryItem(
                            brand = brand.trim().ifEmpty { "Apple" },
                            model = model.trim().ifEmpty { "iPhone" },
                            imei = imei.trim().ifEmpty { "353084110948201" },
                            storage = storage.trim().ifEmpty { "128GB" },
                            color = color.trim().ifEmpty { "Black" },
                            conditionGrade = grade.trim().ifEmpty { "A+" },
                            purchasePrice = p,
                            repairCost = 500.0,
                            targetSalePrice = s,
                            status = "IN_STOCK",
                            healthScore = 95,
                            defectsSummary = "Clean motherboard, certified battery, pristine display."
                        )
                        showConfirmIntakeModal = false
                        onAdd(newItem)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPass)
                ) {
                    Text("Confirm Intake", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmIntakeModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    if (showDialogCameraScanner) {
        CameraQrScannerSheet(
            onDismiss = { showDialogCameraScanner = false },
            onImeiScanned = { scanned ->
                imei = scanned
                val rep = SancharSaathiEngine.inspectImei(scanned)
                if (rep.brand != "Unknown / Altered") {
                    brand = rep.brand
                    model = rep.modelName
                    storage = rep.ramStorage.split("|").lastOrNull()?.trim() ?: "128GB"
                    if (rep.brand.contains("Apple")) {
                        purchasePrice = "54000"
                        targetSalePrice = "65999"
                    } else if (rep.brand.contains("Samsung")) {
                        purchasePrice = "49000"
                        targetSalePrice = "59999"
                    } else {
                        purchasePrice = "22000"
                        targetSalePrice = "27999"
                    }
                }
            }
        )
    }
}

// -------------------------------------------------------------------------
// COMPONENT 6: CSV IMPORT DIALOG
// -------------------------------------------------------------------------
@Composable
fun MaterialCsvImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var csvText by remember {
        mutableStateOf(
            """
            Brand,Model,IMEI,PurchasePrice,TargetSalePrice,Grade,Status,Storage
            "Apple","iPhone 15 Pro","353084110948201",56000,68000,"A+","IN_STOCK","256GB"
            "Samsung","Galaxy S24 Ultra","359284102948194",62000,74000,"A+","IN_STOCK","512GB"
            "Google","Pixel 8 Pro","354892091823904",42000,51000,"A","IN_STOCK","128GB"
            """.trimIndent()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bulk Import Inventory (CSV)", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                Text(text = "Paste or edit CSV rows to bulk load phones into Room database:", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onImport(csvText) }) {
                Text("Import CSV", fontWeight = FontWeight.Bold, color = AzureBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Helper Intents
private fun shareCsvIntent(context: Context, csvContent: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "RefurbIQ_Inventory_Backup.csv")
        putExtra(Intent.EXTRA_TEXT, csvContent)
    }
    context.startActivity(Intent.createChooser(intent, "Export Inventory CSV"))
}

private fun shareWhatsAppCatalog(context: Context, item: InventoryItem) {
    val text = """
        📱 *FOR SALE - REFURBIQ CERTIFIED USED MOBILE*
        ------------------------------------------
        *Device:* ${item.brand} ${item.model}
        *Variant:* ${item.storage} • ${item.color}
        *Condition Grade:* ${item.conditionGrade}
        *QC Health Score:* ${item.healthScore}%
        *IMEI:* ${SecurityUtils.formatImei(item.imei)}
        *DoT CEIR Status:* Clean / NOC Issued
        
        💰 *Special Offer Price:* ₹${item.targetSalePrice.toInt()}
        🛡️ *Warranty:* ${item.warrantyType}
        
        _Inspected & Verified via RefurbIQ Mobile OS._
    """.trimIndent()

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(text)}")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Device Catalog"))
    }
}
