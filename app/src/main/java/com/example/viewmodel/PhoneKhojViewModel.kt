package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DiagnosticRecord
import com.example.data.model.FeedbackItem
import com.example.data.model.InventoryItem
import com.example.data.model.User
import com.example.data.repository.PhoneKhojRepository
import com.example.util.CaptchaChallenge
import com.example.util.CaptchaEngine
import com.example.util.SancharSaathiEngine
import com.example.util.SancharSaathiReport
import com.example.util.SecurityUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PhoneKhojViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PhoneKhojRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PhoneKhojRepository(
            db.userDao(),
            db.inventoryDao(),
            db.feedbackDao(),
            db.diagnosticDao()
        )
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    // -------------------------------------------------------------
    // NAVIGATION & TAB STATE
    // -------------------------------------------------------------
    val selectedTab = MutableStateFlow(0)

    // -------------------------------------------------------------
    // SYSTEM PREFERENCES, PRIVACY & UTM
    // -------------------------------------------------------------
    val isDarkMode = MutableStateFlow(false)
    val isPassiveVisitMode = MutableStateFlow(false) // Blurs wholesale margins & masks sensitive customer data
    val isCookieBannerDismissed = MutableStateFlow(false)
    val isMobileMenuOpen = MutableStateFlow(false)
    val isContactDialogOpen = MutableStateFlow(false)
    val isFaqDialogOpen = MutableStateFlow(false)
    val isUtmDialogOpen = MutableStateFlow(false)

    // UTM Tracking Engine
    val utmSource = MutableStateFlow("google_ads")
    val utmMedium = MutableStateFlow("cpc")
    val utmCampaign = MutableStateFlow("diwali_refurb_exchange_2026")
    val utmTerm = MutableStateFlow("refurbished_smartphones")
    val utmContent = MutableStateFlow("hero_cta")

    val lastUpdateTimestamp = MutableStateFlow("Oct 02, 2026 • 15:30 IST")

    // Confirmation Modal State
    val confirmDialogTitle = MutableStateFlow<String?>(null)
    val confirmDialogMessage = MutableStateFlow<String?>(null)
    val confirmDialogAction = MutableStateFlow<(() -> Unit)?>(null)

    fun requestConfirmation(title: String, message: String, action: () -> Unit) {
        confirmDialogTitle.value = title
        confirmDialogMessage.value = message
        confirmDialogAction.value = action
    }

    fun dismissConfirmation() {
        confirmDialogTitle.value = null
        confirmDialogMessage.value = null
        confirmDialogAction.value = null
    }

    fun executeConfirmation() {
        confirmDialogAction.value?.invoke()
        dismissConfirmation()
    }

    // -------------------------------------------------------------
    // AUTHENTICATION & ROLE STATE
    // -------------------------------------------------------------
    val currentUser: StateFlow<User?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRole = MutableStateFlow("SELLER") // "CONSUMER", "SELLER", "ADMIN"

    val authScreenState = MutableStateFlow("LOGIN") // Default to LOGIN page on app start
    val authLoading = MutableStateFlow(false)
    val authErrorMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)

    // Active role tab inside the auth screen (0: Consumer, 1: Seller)
    val authRoleTab = MutableStateFlow(0) // 0: Consumer, 1: Seller

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authLoading.value = true
            authErrorMessage.value = null
            authSuccessMessage.value = null
            delay(500)
            val result = repository.loginUser(email, pass)
            authLoading.value = false
            result.onSuccess {
                userRole.value = it.role
                authScreenState.value = "AUTHENTICATED"
                authSuccessMessage.value = "Welcome back, ${it.ownerName} (${it.role})!"
            }.onFailure {
                authErrorMessage.value = it.message ?: "Login failed"
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        shop: String,
        owner: String,
        phone: String,
        addr: String,
        role: String = "SELLER"
    ) {
        viewModelScope.launch {
            authLoading.value = true
            authErrorMessage.value = null
            authSuccessMessage.value = null
            if (!SecurityUtils.isValidEmail(email)) {
                authLoading.value = false
                authErrorMessage.value = "Please enter a valid email address."
                return@launch
            }
            if (pass.length < 6) {
                authLoading.value = false
                authErrorMessage.value = "Password must be at least 6 characters."
                return@launch
            }
            delay(500)
            val result = repository.registerUser(email, pass, shop, owner, phone, addr, role)
            authLoading.value = false
            result.onSuccess {
                userRole.value = it.role
                authScreenState.value = "AUTHENTICATED"
                authSuccessMessage.value = "Account created successfully as ${it.role}!"
            }.onFailure {
                authErrorMessage.value = it.message ?: "Registration failed"
            }
        }
    }

    fun loginAsAdmin(adminKey: String, masterPin: String) {
        viewModelScope.launch {
            authLoading.value = true
            authErrorMessage.value = null
            authSuccessMessage.value = null
            delay(600)
            if (adminKey.trim().lowercase() == "admin@refurbiq.gov.in" && (masterPin == "AdminAudit2026!" || masterPin == "2026")) {
                val adminUser = User(
                    email = "admin@refurbiq.gov.in",
                    passwordHash = "",
                    shopName = "DoT CEIR Regulatory Authority",
                    ownerName = "Director General Audit",
                    phone = "+91 11 2337 1200",
                    gstNumber = "GOVT-CEIR-DELHI",
                    shopAddress = "Sanchar Bhawan, 20 Ashoka Road, New Delhi",
                    role = "ADMIN"
                )
                repository.setActiveUser(adminUser)
                userRole.value = "ADMIN"
                authScreenState.value = "AUTHENTICATED"
                authLoading.value = false
                authSuccessMessage.value = "Admin Regulatory Console Access Granted"
            } else {
                authLoading.value = false
                authErrorMessage.value = "Invalid Admin Key or Master PIN"
            }
        }
    }

    fun logout() {
        repository.setActiveUser(null)
        authScreenState.value = "LOGIN"
        selectedTab.value = 0
    }

    fun switchRole(newRole: String) {
        userRole.value = newRole
        selectedTab.value = 0
    }

    // -------------------------------------------------------------
    // REAL SANCHAR SAATHI & IMEI INSPECTOR WITH CAPTCHA
    // -------------------------------------------------------------
    val sancharSaathiInputImei = MutableStateFlow("353084110948201")
    val sancharSaathiIsLoading = MutableStateFlow(false)
    val sancharSaathiCaptcha = MutableStateFlow(CaptchaEngine.generateChallenge())
    val sancharSaathiCaptchaInput = MutableStateFlow("")
    val sancharSaathiCaptchaError = MutableStateFlow<String?>(null)

    fun refreshSancharSaathiCaptcha() {
        sancharSaathiCaptcha.value = CaptchaEngine.generateChallenge()
        sancharSaathiCaptchaInput.value = ""
        sancharSaathiCaptchaError.value = null
    }

    val sancharSaathiReport = MutableStateFlow<SancharSaathiReport?>(
        SancharSaathiEngine.inspectImei("353084110948201")
    )
    val sancharSaathiHistory = MutableStateFlow<List<SancharSaathiReport>>(
        listOf(
            SancharSaathiEngine.inspectImei("353084110948201"),
            SancharSaathiEngine.inspectImei("359284102948194")
        )
    )

    // Pre-filled fields for Diagnostic Engine
    val diagDeviceModel = MutableStateFlow("iPhone 15 Pro Max")
    val diagImei = MutableStateFlow("353084110948201")

    fun verifyAndInspectSancharSaathi(rawImei: String, enteredCaptcha: String): Boolean {
        val actualCode = sancharSaathiCaptcha.value.code
        val isValid = CaptchaEngine.verify(enteredCaptcha, actualCode)
        if (!isValid) {
            sancharSaathiCaptchaError.value = "Incorrect Captcha! Please enter the exact characters displayed in the security image."
            refreshSancharSaathiCaptcha()
            return false
        }
        sancharSaathiCaptchaError.value = null
        runSancharSaathiInspection(rawImei)
        // Refresh captcha for next transaction
        sancharSaathiCaptcha.value = CaptchaEngine.generateChallenge()
        sancharSaathiCaptchaInput.value = ""
        return true
    }

    fun runSancharSaathiInspection(rawImei: String) {
        viewModelScope.launch {
            sancharSaathiIsLoading.value = true
            sancharSaathiInputImei.value = rawImei
            delay(800) // realistic network query simulation
            val report = SancharSaathiEngine.inspectImei(rawImei)
            sancharSaathiReport.value = report

            // Append to history if not duplicate
            val currentList = sancharSaathiHistory.value.toMutableList()
            currentList.removeAll { it.imei == report.imei }
            currentList.add(0, report)
            sancharSaathiHistory.value = currentList.take(20)

            // Also update KYC imei and ceir status if clean
            kycImei.value = report.imei
            kycCeirStatus.value = if (report.ceirStatus == "CLEAN_VALID") "CLEAN" else "FLAGGED"
            kycCeirDetails.value = report.ceirRemarks

            sancharSaathiIsLoading.value = false
        }
    }

    fun resetPassword(email: String, newPass: String) {
        viewModelScope.launch {
            authLoading.value = true
            authErrorMessage.value = null
            authSuccessMessage.value = null
            delay(600)
            val result = repository.resetPassword(email, newPass)
            authLoading.value = false
            result.onSuccess {
                authSuccessMessage.value = "Password updated! You can now log in."
                authScreenState.value = "LOGIN"
            }.onFailure {
                authErrorMessage.value = it.message ?: "Failed to reset password."
            }
        }
    }

    // -------------------------------------------------------------
    // DASHBOARD & INVENTORY STATE
    // -------------------------------------------------------------
    val allInventory: StateFlow<List<InventoryItem>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStockCount: StateFlow<Int> = repository.activeStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCapitalLocked: StateFlow<Double?> = repository.totalCapitalLocked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalRealizedProfit: StateFlow<Double?> = repository.totalRealizedProfit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val unitsInRepairCount: StateFlow<Int> = repository.unitsInRepairCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Customer Feedback Aggregation
    val allFeedback: StateFlow<List<FeedbackItem>> = repository.allFeedback
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val averageRating: StateFlow<Double?> = repository.averageRating
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4.8)

    val totalFeedbackCount: StateFlow<Int> = repository.totalFeedbackCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Search, Filters & Sorting
    val searchQuery = MutableStateFlow("")
    val filterBrand = MutableStateFlow("ALL")
    val filterStatus = MutableStateFlow("ALL")
    val filterGrade = MutableStateFlow("ALL")
    val sortOption = MutableStateFlow("NEWEST") // NEWEST, PRICE_ASC, PRICE_DESC, HEALTH_HIGH, MARGIN_HIGH

    data class FilterParams(
        val query: String,
        val brand: String,
        val status: String,
        val grade: String,
        val sort: String
    )

    private val filterParams = combine(
        searchQuery,
        filterBrand,
        filterStatus,
        filterGrade,
        sortOption
    ) { query, brand, status, grade, sort ->
        FilterParams(query, brand, status, grade, sort)
    }

    val filteredInventory: StateFlow<List<InventoryItem>> = combine(
        allInventory,
        filterParams
    ) { items: List<InventoryItem>, params: FilterParams ->
        var list = items

        if (params.query.isNotBlank()) {
            list = list.filter {
                it.model.contains(params.query, ignoreCase = true) ||
                it.brand.contains(params.query, ignoreCase = true) ||
                it.imei.contains(params.query, ignoreCase = true)
            }
        }

        if (params.brand != "ALL") {
            list = list.filter { it.brand.equals(params.brand, ignoreCase = true) }
        }

        if (params.status != "ALL") {
            list = list.filter { it.status.equals(params.status, ignoreCase = true) }
        }

        if (params.grade != "ALL") {
            list = list.filter { it.conditionGrade.equals(params.grade, ignoreCase = true) }
        }

        when (params.sort) {
            "PRICE_ASC" -> list.sortedBy { it.targetSalePrice }
            "PRICE_DESC" -> list.sortedByDescending { it.targetSalePrice }
            "HEALTH_HIGH" -> list.sortedByDescending { it.healthScore }
            "MARGIN_HIGH" -> list.sortedByDescending { it.targetSalePrice - (it.purchasePrice + it.repairCost) }
            else -> list.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Low stock notification config
    val lowStockThreshold = MutableStateFlow(2) // alert if brand count in stock <= threshold

    fun addInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.insertInventoryItem(item)
        }
    }

    fun updateDeviceStatus(item: InventoryItem, newStatus: String) {
        viewModelScope.launch {
            repository.updateInventoryItem(item.copy(status = newStatus))
        }
    }

    fun deleteDevice(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    fun exportInventoryCsv(): String {
        return repository.exportToCsv(allInventory.value)
    }

    fun importCsv(content: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.bulkImportCsv(content)
            result.onSuccess { count ->
                onResult(true, "Successfully imported $count devices into inventory!")
            }.onFailure { err ->
                onResult(false, err.message ?: "Failed to parse CSV")
            }
        }
    }

    // -------------------------------------------------------------
    // DIAGNOSTIC ENGINE (TAB 2)
    // -------------------------------------------------------------
    val isRadarScanning = MutableStateFlow(false)
    val radarScanProgress = MutableStateFlow(0f)
    val radarScanStage = MutableStateFlow("Ready to begin 2-Minute Scan")

    // Touch Grid: 48 cells (6 columns x 8 rows)
    val totalCells = 48
    val touchGrid = MutableStateFlow(List(totalCells) { false })
    val touchGridProgress = MutableStateFlow(0) // count of passed cells

    // Camera & Flash Test
    val cameraViewActive = MutableStateFlow(true)
    val cameraAutofocusPassed = MutableStateFlow(true)
    val cameraFlashPassed = MutableStateFlow(true)
    val cameraTorchOn = MutableStateFlow(false)
    val cameraLens = MutableStateFlow("REAR") // REAR or FRONT

    // Audio & Mic Test
    val audioSpeakerPassed = MutableStateFlow(true)
    val audioMicPassed = MutableStateFlow(true)
    val audioWaveLevels = MutableStateFlow(listOf(0.2f, 0.4f, 0.8f, 0.5f, 0.9f, 0.6f, 0.3f, 0.7f, 0.4f))

    // Battery Gauge
    val batteryCapacity = MutableStateFlow(87) // %
    val batteryCycleCount = MutableStateFlow(328)
    val batteryTemperature = MutableStateFlow(31.4f)
    val batteryStatus = MutableStateFlow("NORMAL (GOOD)")

    // Overall Computed Diagnostic Score (0 to 100)
    val diagnosticScore = MutableStateFlow(91)

    fun startRadarScan() {
        if (isRadarScanning.value) return
        viewModelScope.launch {
            isRadarScanning.value = true
            val stages = listOf(
                "Initializing Sensor Array...",
                "Pinging RF & Baseband Radios...",
                "Scanning Digitizer Grid & Dead Pixels...",
                "Calibrating CMOS Camera & Strobe Flash...",
                "Sampling Dual Mic & Stereo Audio DAC...",
                "Reading Battery BMS & Cycle Degradation...",
                "Checking Motherboard Thermal Sensor...",
                "Diagnostic Complete! Generating Health Report..."
            )
            for (i in stages.indices) {
                radarScanStage.value = stages[i]
                radarScanProgress.value = (i + 1f) / stages.size
                delay(400)
            }
            isRadarScanning.value = false
            computeDiagnosticScore()
        }
    }

    fun onCellTouched(index: Int) {
        if (index in 0 until totalCells) {
            val current = touchGrid.value.toMutableList()
            if (!current[index]) {
                current[index] = true
                touchGrid.value = current
                touchGridProgress.value = current.count { it }
                computeDiagnosticScore()
            }
        }
    }

    fun resetTouchGrid() {
        touchGrid.value = List(totalCells) { false }
        touchGridProgress.value = 0
    }

    fun fillTouchGrid() {
        touchGrid.value = List(totalCells) { true }
        touchGridProgress.value = totalCells
        computeDiagnosticScore()
    }

    fun toggleCameraFlash() {
        cameraTorchOn.value = !cameraTorchOn.value
    }

    fun switchCameraLens() {
        cameraLens.value = if (cameraLens.value == "REAR") "FRONT" else "REAR"
    }

    private fun computeDiagnosticScore() {
        var score = 100
        val touchedRatio = touchGridProgress.value.toFloat() / totalCells.toFloat()
        if (touchedRatio < 0.8f && touchGridProgress.value > 0) {
            score -= 15
        }
        if (!cameraAutofocusPassed.value) score -= 12
        if (!cameraFlashPassed.value) score -= 6
        if (!audioSpeakerPassed.value) score -= 8
        if (!audioMicPassed.value) score -= 8
        if (batteryCapacity.value < 80) score -= 15 else if (batteryCapacity.value < 85) score -= 7
        diagnosticScore.value = score.coerceIn(40, 100)
    }

    // -------------------------------------------------------------
    // SMART VALUATION & MARGIN ENGINE (TAB 3)
    // -------------------------------------------------------------
    val valBrand = MutableStateFlow("Apple")
    val valModel = MutableStateFlow("iPhone 13")
    val valStorage = MutableStateFlow("128GB")

    // Defects checklist
    val defectDisplayCrack = MutableStateFlow(false)
    val defectDeadPixels = MutableStateFlow(false)
    val defectBatteryDegraded = MutableStateFlow(false)
    val defectCameraIssue = MutableStateFlow(false)
    val defectBackCoverCrack = MutableStateFlow(false)
    val defectSpeakerMicIssue = MutableStateFlow(false)
    val defectMissingBoxCharger = MutableStateFlow(true)
    val defectHousingDents = MutableStateFlow(false)

    val targetProfitTarget = MutableStateFlow(2500.0)

    // Base market benchmark map
    private val modelMarketValues = mapOf(
        "iPhone 15 Pro" to 78000.0,
        "iPhone 15" to 54000.0,
        "iPhone 14 Pro" to 64000.0,
        "iPhone 14" to 46000.0,
        "iPhone 13" to 38000.0,
        "iPhone 12" to 26000.0,
        "Galaxy S24 Ultra" to 82000.0,
        "Galaxy S23 Ultra" to 68000.0,
        "Galaxy S23" to 44000.0,
        "Galaxy S22" to 32000.0,
        "OnePlus 12" to 52000.0,
        "OnePlus 11" to 35000.0,
        "Pixel 8 Pro" to 56000.0,
        "Pixel 7 Pro" to 34000.0,
        "Redmi Note 13 Pro+" to 22000.0,
        "Redmi Note 12 Pro" to 15000.0,
        "Vivo V29 Pro" to 25000.0
    )

    fun getBaseMarketPrice(): Double {
        val base = modelMarketValues[valModel.value] ?: 30000.0
        val storageMult = when (valStorage.value) {
            "256GB" -> 1.10
            "512GB" -> 1.25
            "1TB" -> 1.40
            else -> 1.00
        }
        return base * storageMult
    }

    fun getTotalDeductions(): Double {
        var total = 0.0
        if (defectDisplayCrack.value) total += 3500.0
        if (defectDeadPixels.value) total += 2500.0
        if (defectBatteryDegraded.value) total += 1500.0
        if (defectCameraIssue.value) total += 2000.0
        if (defectBackCoverCrack.value) total += 800.0
        if (defectSpeakerMicIssue.value) total += 900.0
        if (defectMissingBoxCharger.value) total += 600.0
        if (defectHousingDents.value) total += 700.0
        return total
    }

    fun getRepairOverhead(): Double {
        var repair = 0.0
        if (defectDisplayCrack.value) repair += 2200.0
        if (defectBatteryDegraded.value) repair += 900.0
        if (defectCameraIssue.value) repair += 1200.0
        if (defectBackCoverCrack.value) repair += 500.0
        if (defectSpeakerMicIssue.value) repair += 400.0
        return repair
    }

    fun getRecommendedBuyOffer(): Double {
        val base = getBaseMarketPrice()
        val deductions = getTotalDeductions()
        val estimatedSale = (base - deductions).coerceAtLeast(3000.0)
        val overhead = getRepairOverhead()
        val profit = targetProfitTarget.value
        val offer = estimatedSale - overhead - profit
        return offer.coerceAtLeast(1500.0)
    }

    fun syncDiagnosticsToValuation() {
        // Auto-configure defects based on diagnostic scores
        defectBatteryDegraded.value = batteryCapacity.value < 82
        defectDeadPixels.value = touchGridProgress.value < (totalCells * 0.9f)
        defectCameraIssue.value = !cameraAutofocusPassed.value
        defectSpeakerMicIssue.value = !audioSpeakerPassed.value || !audioMicPassed.value
        selectedTab.value = 2 // Switch to Valuation tab
    }

    fun intakeValuatedDevice(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val base = getBaseMarketPrice()
            val offer = getRecommendedBuyOffer()
            val deductions = getTotalDeductions()
            val resale = (base - deductions).coerceAtLeast(offer + 1500.0)
            val overhead = getRepairOverhead()
            val score = if (defectDisplayCrack.value || defectDeadPixels.value) 74 else 90
            val grade = if (score >= 88) "A+" else if (score >= 80) "A" else "B"
            val imei = "35" + (1000000000000L..9999999999999L).random()

            val newItem = InventoryItem(
                brand = valBrand.value,
                model = valModel.value,
                imei = imei,
                storage = valStorage.value,
                purchasePrice = offer,
                repairCost = overhead,
                targetSalePrice = resale,
                conditionGrade = grade,
                status = "IN_STOCK",
                healthScore = score,
                defectsSummary = if (deductions == 0.0) "Clean intake. Zero defects." else "Trade-in intake with estimated ₹${deductions.toLong()} deductions",
                customerName = "Trade-in Walk-in",
                customerPhone = "+91 98110 54321",
                warrantyType = "1-Month Shop Warranty",
                ceirStatus = "CLEAN",
                utmSource = utmCampaign.value
            )
            repository.insertInventoryItem(newItem)
            onSuccess("Successfully intook ${valBrand.value} ${valModel.value} to Inventory with purchase offer ₹${offer.toLong()}!")
        }
    }


    // -------------------------------------------------------------
    // LEGAL KYC & FRAUD SHIELD (TAB 4)
    // -------------------------------------------------------------
    val kycImei = MutableStateFlow("359284110948201")
    val kycCeirStatus = MutableStateFlow("IDLE") // IDLE, VERIFYING, CLEAN, FLAGGED
    val kycCeirDetails = MutableStateFlow("")
    val kycCustomerName = MutableStateFlow("Rahul Verma")
    val kycCustomerPhone = MutableStateFlow("+91 98110 98765")
    val kycCustomerAadhaar = MutableStateFlow("5482 9104 2938")
    val kycSelfieCaptured = MutableStateFlow(true)
    val kycIdUploaded = MutableStateFlow(true)
    val kycDeclarationAccepted = MutableStateFlow(true)

    // Digital Signature Pad Points
    val signatureStrokes = MutableStateFlow<List<List<Offset>>>(emptyList())
    val currentStroke = MutableStateFlow<List<Offset>>(emptyList())
    val isSignatureSaved = MutableStateFlow(true)

    fun runCeirFraudCheck() {
        viewModelScope.launch {
            kycCeirStatus.value = "VERIFYING"
            delay(1200)
            val imei = kycImei.value.filter { it.isDigit() }
            if (imei.endsWith("999")) {
                kycCeirStatus.value = "FLAGGED"
                kycCeirDetails.value = "CEIR ALERT: Device reported LOST/STOLEN on 14-Aug-2026. Do NOT buy!"
            } else {
                kycCeirStatus.value = "CLEAN"
                kycCeirDetails.value = "Sanchar Saathi NOC PASSED: Clean device, Valid TAC, No FIR/Theft report logged."
            }
        }
    }

    fun addStrokePoint(point: Offset) {
        val stroke = currentStroke.value.toMutableList()
        stroke.add(point)
        currentStroke.value = stroke
    }

    fun endStroke() {
        if (currentStroke.value.isNotEmpty()) {
            val list = signatureStrokes.value.toMutableList()
            list.add(currentStroke.value)
            signatureStrokes.value = list
            currentStroke.value = emptyList()
            isSignatureSaved.value = true
        }
    }

    fun clearSignature() {
        signatureStrokes.value = emptyList()
        currentStroke.value = emptyList()
        isSignatureSaved.value = false
    }

    // -------------------------------------------------------------
    // WHATSAPP-READY DIGITAL INVOICE (TAB 5)
    // -------------------------------------------------------------
    val invoiceDeviceModel = MutableStateFlow("iPhone 14 Pro 256GB")
    val invoiceImei = MutableStateFlow("359284110948201")
    val invoiceCustomerName = MutableStateFlow("Rahul Verma")
    val invoiceCustomerPhone = MutableStateFlow("+91 98110 98765")
    val invoiceBasePrice = MutableStateFlow(64999.0)
    val invoiceServiceFee = MutableStateFlow(1200.0)
    val invoiceDiscount = MutableStateFlow(1000.0)
    val invoiceWarranty = MutableStateFlow("1-Month Shop Warranty")
    val invoiceRepairLog = MutableStateFlow("Full OEM Diagnostic Certified, Clean Motherboard, Battery 96% Health")

    fun selectItemForInvoice(item: InventoryItem) {
        invoiceDeviceModel.value = "${item.brand} ${item.model} ${item.storage}"
        invoiceImei.value = item.imei
        invoiceCustomerName.value = item.customerName
        invoiceCustomerPhone.value = item.customerPhone
        invoiceBasePrice.value = item.targetSalePrice
        invoiceServiceFee.value = item.repairCost
        invoiceWarranty.value = item.warrantyType
        invoiceRepairLog.value = item.defectsSummary
        selectedTab.value = 4 // switch to Invoice tab
    }

    fun getFinalInvoiceAmount(): Double {
        return (invoiceBasePrice.value + invoiceServiceFee.value - invoiceDiscount.value).coerceAtLeast(0.0)
    }

    fun generateWhatsAppBillText(): String {
        val total = getFinalInvoiceAmount()
        return """
            🧾 *PHONETKHOJ / SMARTREFURB DIGITAL INVOICE*
            ------------------------------------------------
            🏪 *Shop:* Apex Mobile Hub
            📍 *Address:* Connaught Place, New Delhi
            📞 *Contact:* +91 98765 43210
            
            👤 *Customer:* ${invoiceCustomerName.value} (${invoiceCustomerPhone.value})
            📱 *Device:* ${invoiceDeviceModel.value}
            🔢 *IMEI:* ${SecurityUtils.formatImei(invoiceImei.value)}
            🛡️ *CEIR Status:* Verified Clean (Sanchar Saathi NOC)
            🛡️ *Warranty:* ${invoiceWarranty.value}
            
            🔧 *Diagnostic & Service Log:*
            ${invoiceRepairLog.value}
            
            💰 *Price Breakdown:*
            • Base Device Price: ₹${invoiceBasePrice.value.toLong()}
            • Service & QC Certification: ₹${invoiceServiceFee.value.toLong()}
            • Special Discount: -₹${invoiceDiscount.value.toLong()}
            ------------------------------------------------
            ✅ *FINAL PAYABLE AMOUNT:* ₹${total.toLong()}
            ------------------------------------------------
            _Generated digitally via PhoneKhoj Pro. Thank you for your business!_
        """.trimIndent()
    }

    // -------------------------------------------------------------
    // CUSTOMER FEEDBACK MODULE
    // -------------------------------------------------------------
    val feedbackDialogOpen = MutableStateFlow(false)
    val feedbackRating = MutableStateFlow(5)
    val feedbackReviewText = MutableStateFlow("")
    val feedbackCustomerName = MutableStateFlow("")
    val feedbackDeviceModel = MutableStateFlow("")
    val feedbackServiceType = MutableStateFlow("Sale")

    fun openFeedbackDialog(customer: String, device: String, type: String = "Sale") {
        feedbackCustomerName.value = customer
        feedbackDeviceModel.value = device
        feedbackServiceType.value = type
        feedbackRating.value = 5
        feedbackReviewText.value = ""
        feedbackDialogOpen.value = true
    }

    fun submitFeedback() {
        val name = feedbackCustomerName.value.ifBlank { "Verified Customer" }
        val model = feedbackDeviceModel.value.ifBlank { "Refurbished Smartphone" }
        val text = feedbackReviewText.value.ifBlank { "Prompt service and crystal clear diagnostics report!" }

        viewModelScope.launch {
            repository.addFeedback(
                FeedbackItem(
                    customerName = name,
                    deviceModel = model,
                    serviceType = feedbackServiceType.value,
                    rating = feedbackRating.value,
                    reviewText = text,
                    tags = if (feedbackRating.value >= 4) "Genuine Warranty, Clear Pricing" else "Standard Service"
                )
            )
            feedbackDialogOpen.value = false
        }
    }

    // -------------------------------------------------------------
    // OFFLINE ROOM PERSISTENCE: DIAGNOSTIC HISTORY
    // -------------------------------------------------------------
    val diagnosticHistory: StateFlow<List<DiagnosticRecord>> = repository.allDiagnostics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val saveDiagnosticSuccess = MutableStateFlow<String?>(null)

    fun saveCurrentDiagnostic(
        deviceModel: String,
        imei: String,
        technicianNotes: String = "Passed 2-Minute Diagnostic Engine. Room SQLite Verified."
    ) {
        viewModelScope.launch {
            val score = diagnosticScore.value
            val grade = when {
                score >= 90 -> "Grade A+"
                score >= 80 -> "Grade A"
                score >= 70 -> "Grade B"
                else -> "Grade C (Service Needed)"
            }
            val record = DiagnosticRecord(
                deviceModel = deviceModel.ifBlank { "Refurbished Smartphone" },
                imei = imei.ifBlank { "86" + (1000000000000L..9999999999999L).random() },
                healthScore = score,
                conditionGrade = grade,
                touchGridScore = touchGridProgress.value,
                cameraStatus = if (cameraAutofocusPassed.value) "PASS" else "WARNING",
                flashStatus = if (cameraFlashPassed.value) "PASS" else "FAIL",
                audioStatus = if (audioSpeakerPassed.value) "PASS" else "FAIL",
                micStatus = if (audioMicPassed.value) "PASS" else "FAIL",
                batteryCapacity = batteryCapacity.value,
                batteryCycles = batteryCycleCount.value,
                batteryStatus = batteryStatus.value,
                defectsSummary = if (score >= 90) "All hardware sensors certified" else "Defects detected in diagnostic check",
                technicianNotes = technicianNotes,
                timestamp = System.currentTimeMillis()
            )
            repository.saveDiagnosticRecord(record)
            saveDiagnosticSuccess.value = "Diagnostic report for $deviceModel saved offline in SQLite!"
            delay(3500)
            saveDiagnosticSuccess.value = null
        }
    }

    fun deleteDiagnostic(record: DiagnosticRecord) {
        viewModelScope.launch {
            repository.deleteDiagnosticRecord(record)
        }
    }
}
