package com.example.data.repository

import com.example.data.db.DiagnosticDao
import com.example.data.db.FeedbackDao
import com.example.data.db.InventoryDao
import com.example.data.db.UserDao
import com.example.data.model.DiagnosticRecord
import com.example.data.model.FeedbackItem
import com.example.data.model.InventoryItem
import com.example.data.model.User
import com.example.util.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class PhoneKhojRepository(
    private val userDao: UserDao,
    private val inventoryDao: InventoryDao,
    private val feedbackDao: FeedbackDao,
    private val diagnosticDao: DiagnosticDao
) {
    val allInventory: Flow<List<InventoryItem>> = inventoryDao.getAllInventory()
    val activeStockCount: Flow<Int> = inventoryDao.getActiveStockCount()
    val totalCapitalLocked: Flow<Double?> = inventoryDao.getTotalCapitalLocked()
    val totalRealizedProfit: Flow<Double?> = inventoryDao.getTotalRealizedProfit()
    val unitsInRepairCount: Flow<Int> = inventoryDao.getUnitsInRepairCount()

    val allFeedback: Flow<List<FeedbackItem>> = feedbackDao.getAllFeedback()
    val averageRating: Flow<Double?> = feedbackDao.getAverageRating()
    val totalFeedbackCount: Flow<Int> = feedbackDao.getTotalFeedbackCount()

    val allDiagnostics: Flow<List<DiagnosticRecord>> = diagnosticDao.getAllDiagnostics()
    val totalDiagnosticsCount: Flow<Int> = diagnosticDao.getTotalDiagnosticsCount()
    val avgHealthScore: Flow<Double?> = diagnosticDao.getAverageHealthScore()

    val allUsers: Flow<List<User>> = userDao.getAllUsersFlow()

    private val _activeUser = kotlinx.coroutines.flow.MutableStateFlow<User?>(null)
    val currentUserFlow: Flow<User?> = _activeUser

    fun setActiveUser(user: User?) {
        _activeUser.value = user
    }

    suspend fun registerUser(
        email: String,
        password: String,
        shopName: String,
        ownerName: String,
        phone: String,
        shopAddress: String,
        role: String = "SELLER"
    ): Result<User> {
        val existing = userDao.getUserByEmail(email.trim().lowercase())
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val newUser = User(
            email = email.trim().lowercase(),
            passwordHash = SecurityUtils.hashPassword(password),
            shopName = shopName.trim().ifEmpty { if (role == "CONSUMER") "Consumer Profile" else "RefurbIQ Store" },
            ownerName = ownerName.trim().ifEmpty { if (role == "CONSUMER") "Consumer Buyer" else "Shop Owner" },
            phone = phone.trim().ifEmpty { "+91 98765 43210" },
            shopAddress = shopAddress.trim().ifEmpty { "New Delhi, India" },
            role = role
        )
        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)
        _activeUser.value = created
        return Result.success(created)
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("No account found with this email."))
        if (!SecurityUtils.verifyPassword(password, user.passwordHash)) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }
        _activeUser.value = user
        return Result.success(user)
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Boolean> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("No account found with this email."))
        val updated = user.copy(passwordHash = SecurityUtils.hashPassword(newPassword))
        userDao.updateUser(updated)
        return Result.success(true)
    }

    suspend fun insertInventoryItem(item: InventoryItem): Long {
        return inventoryDao.insertItem(item)
    }

    suspend fun updateInventoryItem(item: InventoryItem) {
        inventoryDao.updateItem(item)
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        inventoryDao.deleteItem(item)
    }

    suspend fun addFeedback(feedback: FeedbackItem): Long {
        return feedbackDao.insertFeedback(feedback)
    }

    suspend fun saveDiagnosticRecord(record: DiagnosticRecord): Long {
        return diagnosticDao.insertDiagnostic(record)
    }

    suspend fun deleteDiagnosticRecord(record: DiagnosticRecord) {
        diagnosticDao.deleteDiagnostic(record)
    }

    suspend fun bulkImportCsv(csvContent: String): Result<Int> {
        return try {
            val lines = csvContent.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) return Result.failure(Exception("CSV content is empty"))

            val startIndex = if (lines[0].contains("Brand", ignoreCase = true) || lines[0].contains("Model", ignoreCase = true)) 1 else 0
            val itemsToInsert = mutableListOf<InventoryItem>()

            for (i in startIndex until lines.size) {
                val parts = lines[i].split(",").map { it.trim().removeSurrounding("\"") }
                if (parts.size >= 4) {
                    val brand = parts.getOrNull(0) ?: "Brand"
                    val model = parts.getOrNull(1) ?: "Model"
                    val imei = parts.getOrNull(2) ?: "860000000000000"
                    val purchasePrice = parts.getOrNull(3)?.toDoubleOrNull() ?: 10000.0
                    val targetSale = parts.getOrNull(4)?.toDoubleOrNull() ?: (purchasePrice * 1.25)
                    val grade = parts.getOrNull(5) ?: "A+"
                    val status = parts.getOrNull(6) ?: "IN_STOCK"
                    val storage = parts.getOrNull(7) ?: "128GB"

                    itemsToInsert.add(
                        InventoryItem(
                            brand = brand,
                            model = model,
                            imei = imei,
                            storage = storage,
                            purchasePrice = purchasePrice,
                            repairCost = 500.0,
                            targetSalePrice = targetSale,
                            conditionGrade = grade,
                            status = status,
                            healthScore = 90
                        )
                    )
                }
            }

            if (itemsToInsert.isNotEmpty()) {
                inventoryDao.insertAll(itemsToInsert)
                Result.success(itemsToInsert.size)
            } else {
                Result.failure(Exception("No valid rows parsed from CSV"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportToCsv(items: List<InventoryItem>): String {
        val sb = StringBuilder()
        sb.append("Brand,Model,IMEI,PurchasePrice,RepairCost,TargetSalePrice,ConditionGrade,Status,Storage,HealthScore\n")
        items.forEach { item ->
            sb.append("\"${item.brand}\",")
            sb.append("\"${item.model}\",")
            sb.append("\"${item.imei}\",")
            sb.append("${item.purchasePrice},")
            sb.append("${item.repairCost},")
            sb.append("${item.targetSalePrice},")
            sb.append("\"${item.conditionGrade}\",")
            sb.append("\"${item.status}\",")
            sb.append("\"${item.storage}\",")
            sb.append("${item.healthScore}\n")
        }
        return sb.toString()
    }

    suspend fun populateInitialDataIfEmpty() {
        // Pre-create initial accounts if not present
        if (userDao.getUserByEmail("apex.shop@refurbiq.in") == null) {
            val sellerUser = User(
                email = "apex.shop@refurbiq.in",
                passwordHash = SecurityUtils.hashPassword("SmartRefurb2026!"),
                shopName = "RefurbIQ - Apex Mobile Hub",
                ownerName = "Rana Kunal",
                phone = "+91 98765 43210",
                gstNumber = "07AAAAA1234F1Z9",
                shopAddress = "Booth #14, Electronics Commercial Market, Connaught Place, New Delhi",
                role = "SELLER"
            )
            userDao.insertUser(sellerUser)
        }

        // Backward compatibility for demo login with old email
        if (userDao.getUserByEmail("apex.shop@phonekhoj.in") == null) {
            val legacyUser = User(
                email = "apex.shop@phonekhoj.in",
                passwordHash = SecurityUtils.hashPassword("SmartRefurb2026!"),
                shopName = "RefurbIQ - Apex Mobile Hub",
                ownerName = "Rana Kunal",
                phone = "+91 98765 43210",
                gstNumber = "07AAAAA1234F1Z9",
                shopAddress = "Booth #14, Electronics Commercial Market, Connaught Place, New Delhi",
                role = "SELLER"
            )
            userDao.insertUser(legacyUser)
        }

        if (userDao.getUserByEmail("consumer@refurbiq.in") == null) {
            val consumerUser = User(
                email = "consumer@refurbiq.in",
                passwordHash = SecurityUtils.hashPassword("RefurbIQ2026!"),
                shopName = "Consumer Profile",
                ownerName = "Aarav Sharma",
                phone = "+91 98110 55443",
                gstNumber = "",
                shopAddress = "Sector 62, Noida, NCR",
                role = "CONSUMER"
            )
            userDao.insertUser(consumerUser)
        }

        if (userDao.getUserByEmail("admin@refurbiq.gov.in") == null) {
            val adminUser = User(
                email = "admin@refurbiq.gov.in",
                passwordHash = SecurityUtils.hashPassword("AdminAudit2026!"),
                shopName = "DoT CEIR Regulatory Authority",
                ownerName = "Director General Audit",
                phone = "+91 11 2337 1200",
                gstNumber = "GOVT-CEIR-DELHI",
                shopAddress = "Sanchar Bhawan, 20 Ashoka Road, New Delhi",
                role = "ADMIN"
            )
            userDao.insertUser(adminUser)
        }

        // Pre-populate inventory items if empty
        val existingInventory = inventoryDao.getAllInventory().firstOrNull()
        if (existingInventory.isNullOrEmpty()) {
            val sampleItems = listOf(
                InventoryItem(
                    brand = "Apple",
                    model = "iPhone 14 Pro",
                    imei = "359284110948201",
                    storage = "256GB",
                    color = "Deep Purple",
                    conditionGrade = "A+",
                    purchasePrice = 54000.0,
                    repairCost = 1200.0,
                    targetSalePrice = 64999.0,
                    status = "IN_STOCK",
                    healthScore = 96,
                    defectsSummary = "Battery cycle 190, Original Display, 100% Functional",
                    warrantyType = "1-Month Shop Warranty",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "Samsung",
                    model = "Galaxy S23 Ultra",
                    imei = "354921098471203",
                    storage = "256GB",
                    color = "Phantom Black",
                    conditionGrade = "A",
                    purchasePrice = 58000.0,
                    repairCost = 1800.0,
                    targetSalePrice = 69000.0,
                    status = "IN_STOCK",
                    healthScore = 92,
                    defectsSummary = "Minor bezel hairline scuff, S-Pen pristine",
                    warrantyType = "1-Month Shop Warranty",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "OnePlus",
                    model = "OnePlus 11 5G",
                    imei = "864192049182390",
                    storage = "128GB",
                    color = "Titan Black",
                    conditionGrade = "A+",
                    purchasePrice = 28000.0,
                    repairCost = 600.0,
                    targetSalePrice = 34500.0,
                    status = "IN_STOCK",
                    healthScore = 94,
                    defectsSummary = "Clean body, Hasselblad optical pass",
                    warrantyType = "7-Day Testing Guarantee",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "Apple",
                    model = "iPhone 13",
                    imei = "358102938475612",
                    storage = "128GB",
                    color = "Midnight",
                    conditionGrade = "B",
                    purchasePrice = 31000.0,
                    repairCost = 2500.0,
                    targetSalePrice = 39000.0,
                    status = "IN_REPAIR",
                    healthScore = 79,
                    defectsSummary = "Screen glass replacement in progress, FaceID intact",
                    warrantyType = "1-Month Shop Warranty",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "Google",
                    model = "Pixel 7 Pro",
                    imei = "352918273645102",
                    storage = "128GB",
                    color = "Hazel",
                    conditionGrade = "A",
                    purchasePrice = 26000.0,
                    repairCost = 900.0,
                    targetSalePrice = 33000.0,
                    status = "IN_STOCK",
                    healthScore = 91,
                    defectsSummary = "Clean camera bar, OEM battery health 92%",
                    warrantyType = "1-Month Shop Warranty",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "Xiaomi",
                    model = "Redmi Note 12 Pro+",
                    imei = "860192837465019",
                    storage = "256GB",
                    color = "Arctic White",
                    conditionGrade = "A+",
                    purchasePrice = 14500.0,
                    repairCost = 400.0,
                    targetSalePrice = 18500.0,
                    status = "SOLD",
                    healthScore = 95,
                    defectsSummary = "Sold with original 120W HyperCharger",
                    warrantyType = "1-Month Shop Warranty",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "Vivo",
                    model = "Vivo V29 Pro",
                    imei = "869281726354819",
                    storage = "256GB",
                    color = "Himalayan Blue",
                    conditionGrade = "A",
                    purchasePrice = 21000.0,
                    repairCost = 700.0,
                    targetSalePrice = 26500.0,
                    status = "IN_STOCK",
                    healthScore = 93,
                    defectsSummary = "Aura light fully operational, clean display",
                    warrantyType = "7-Day Testing Guarantee",
                    ceirStatus = "CLEAN"
                ),
                InventoryItem(
                    brand = "Apple",
                    model = "iPhone 12",
                    imei = "359182736450192",
                    storage = "64GB",
                    color = "Blue",
                    conditionGrade = "C",
                    purchasePrice = 18000.0,
                    repairCost = 3200.0,
                    targetSalePrice = 24000.0,
                    status = "IN_REPAIR",
                    healthScore = 68,
                    defectsSummary = "Battery service required (76% health), speaker cleaning",
                    warrantyType = "No Warranty",
                    ceirStatus = "CLEAN"
                )
            )
            inventoryDao.insertAll(sampleItems)
        }

        // Pre-populate customer feedback
        val existingFeedback = feedbackDao.getAllFeedback().firstOrNull()
        if (existingFeedback.isNullOrEmpty()) {
            val sampleFeedback = listOf(
                FeedbackItem(
                    customerName = "Vikram Sharma",
                    customerPhone = "+91 98112 34567",
                    deviceModel = "iPhone 14 Pro 256GB",
                    serviceType = "Sale",
                    rating = 5,
                    reviewText = "Amazing transparent diagnosis! Checked IMEI live on CEIR and phone is in pristine condition. Great warranty support.",
                    tags = "Transparent Pricing, Original Parts"
                ),
                FeedbackItem(
                    customerName = "Priya Mukherjee",
                    customerPhone = "+91 97234 56789",
                    deviceModel = "OnePlus 11 5G",
                    serviceType = "Sale",
                    rating = 5,
                    reviewText = "Best refurbished shop in town. Got WhatsApp digital invoice with all diagnostic checkmarks. Highly recommended!",
                    tags = "Digital Invoice, Fair Valuation"
                ),
                FeedbackItem(
                    customerName = "Arjun Patel",
                    customerPhone = "+91 99012 34567",
                    deviceModel = "Redmi Note 12 Pro",
                    serviceType = "Repair",
                    rating = 4,
                    reviewText = "Quick battery and display repair within 2 hours. Diagnostic report showed before and after health score clearly.",
                    tags = "Speedy Repair, Accurate Diagnostics"
                )
            )
            feedbackDao.insertAll(sampleFeedback)
        }

        // Pre-populate diagnostic history records for offline verification
        val existingDiagnostics = diagnosticDao.getAllDiagnostics().firstOrNull()
        if (existingDiagnostics.isNullOrEmpty()) {
            val sampleDiagnostics = listOf(
                DiagnosticRecord(
                    deviceModel = "iPhone 14 Pro 256GB",
                    imei = "359284110948201",
                    healthScore = 96,
                    conditionGrade = "Grade A+",
                    touchGridScore = 48,
                    cameraStatus = "PASS",
                    flashStatus = "PASS",
                    audioStatus = "PASS",
                    micStatus = "PASS",
                    batteryCapacity = 96,
                    batteryCycles = 190,
                    batteryStatus = "NORMAL",
                    defectsSummary = "Original Super Retina XDR, TrueDepth face sensor OK, zero dead pixels",
                    technicianNotes = "NOC Clean. Sanchar Saathi clearance ID: NOC-DEL-9921",
                    timestamp = System.currentTimeMillis() - 3600000 * 4
                ),
                DiagnosticRecord(
                    deviceModel = "Samsung Galaxy S23 Ultra",
                    imei = "354921098471203",
                    healthScore = 92,
                    conditionGrade = "Grade A",
                    touchGridScore = 48,
                    cameraStatus = "PASS",
                    flashStatus = "PASS",
                    audioStatus = "PASS",
                    micStatus = "PASS",
                    batteryCapacity = 91,
                    batteryCycles = 285,
                    batteryStatus = "NORMAL",
                    defectsSummary = "S-Pen pressure latency normal, 100x Space Zoom calibrated",
                    technicianNotes = "Minor hair scuff on corner bezel. Digitizer 100% functional.",
                    timestamp = System.currentTimeMillis() - 3600000 * 18
                ),
                DiagnosticRecord(
                    deviceModel = "OnePlus 11 5G",
                    imei = "864192049182390",
                    healthScore = 94,
                    conditionGrade = "Grade A+",
                    touchGridScore = 48,
                    cameraStatus = "PASS",
                    flashStatus = "PASS",
                    audioStatus = "PASS",
                    micStatus = "PASS",
                    batteryCapacity = 93,
                    batteryCycles = 210,
                    batteryStatus = "NORMAL",
                    defectsSummary = "Hasselblad color calibration passed, 100W SuperVOOC handshake verified",
                    technicianNotes = "Customer trade-in verified offline against CEIR database.",
                    timestamp = System.currentTimeMillis() - 3600000 * 42
                )
            )
            diagnosticDao.insertAll(sampleDiagnostics)
        }
    }
}
