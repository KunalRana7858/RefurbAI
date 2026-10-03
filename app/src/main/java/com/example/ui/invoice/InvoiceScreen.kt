package com.example.ui.invoice

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.ui.theme.EmeraldPassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianCardGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.util.SecurityUtils
import com.example.viewmodel.PhoneKhojViewModel

@Composable
fun InvoiceScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()

    val model by viewModel.invoiceDeviceModel.collectAsState()
    val imei by viewModel.invoiceImei.collectAsState()
    val customerName by viewModel.invoiceCustomerName.collectAsState()
    val customerPhone by viewModel.invoiceCustomerPhone.collectAsState()
    val basePrice by viewModel.invoiceBasePrice.collectAsState()
    val serviceFee by viewModel.invoiceServiceFee.collectAsState()
    val discount by viewModel.invoiceDiscount.collectAsState()
    val warranty by viewModel.invoiceWarranty.collectAsState()
    val repairLog by viewModel.invoiceRepairLog.collectAsState()

    val finalAmount = viewModel.getFinalInvoiceAmount()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WhatsApp Digital Invoice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Instant 1-Click WhatsApp Delivery & Repair Log",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
                StatusBadge(status = "CERTIFIED")
            }
        }

        // WARRANTY SELECTION CHIPS
        item {
            WarrantySelectorCard(
                selectedWarranty = warranty,
                onSelectWarranty = { viewModel.invoiceWarranty.value = it }
            )
        }

        // DIGITAL INVOICE RECEIPT CARD (LUXURY FINTECH DESIGN)
        item {
            DigitalInvoiceCard(
                shopName = user?.shopName ?: "PhoneKhoj - Apex Mobile Hub",
                shopAddress = user?.shopAddress ?: "Shop #12, Electronics Mall, New Delhi",
                gst = user?.gstNumber ?: "07AAAAA1234F1Z9",
                invoiceNo = "PK-2026-9042",
                date = "28-Sep-2026, 08:35 PM",
                customerName = customerName,
                customerPhone = customerPhone,
                deviceModel = model,
                imei = imei,
                warranty = warranty,
                repairLog = repairLog,
                basePrice = basePrice,
                serviceFee = serviceFee,
                discount = discount,
                finalAmount = finalAmount
            )
        }

        // ACTION BUTTONS: WHATSAPP SHARE, PRINT, CUSTOMER FEEDBACK
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 1-Click WhatsApp Share Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WhatsAppGreen)
                        .clickable {
                            val billText = viewModel.generateWhatsAppBillText()
                            shareWhatsAppBill(context, billText)
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = ObsidianBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send Invoice via WhatsApp",
                            color = ObsidianBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeonButton(
                        text = "Export Receipt PDF",
                        onClick = {
                            val billText = viewModel.generateWhatsAppBillText()
                            exportPdfIntent(context, billText)
                        },
                        icon = { Icon(Icons.Default.Download, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp)) },
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "export_pdf_btn"
                    )

                    // Customer Feedback Trigger Button
                    NeonButton(
                        text = "Leave Feedback",
                        onClick = {
                            viewModel.openFeedbackDialog(customerName, model, "Sale")
                        },
                        icon = { Icon(Icons.Default.Star, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f),
                        testTag = "leave_feedback_btn"
                    )
                }
            }
        }
    }
}

@Composable
fun WarrantySelectorCard(
    selectedWarranty: String,
    onSelectWarranty: (String) -> Unit
) {
    val warranties = listOf(
        "No Warranty",
        "7-Day Testing Guarantee",
        "1-Month Shop Warranty",
        "6-Month Certified Warranty"
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Select Shop Warranty Tier",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                warranties.forEach { w ->
                    val selected = (w == selectedWarranty)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) CyberCyan else Color(0xFF161F30))
                            .border(1.dp, if (selected) CyberCyan else GlassBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { onSelectWarranty(w) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = w,
                            color = if (selected) ObsidianBackground else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalInvoiceCard(
    shopName: String,
    shopAddress: String,
    gst: String,
    invoiceNo: String,
    date: String,
    customerName: String,
    customerPhone: String,
    deviceModel: String,
    imei: String,
    warranty: String,
    repairLog: String,
    basePrice: Double,
    serviceFee: Double,
    discount: Double,
    finalAmount: Double
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberCyan.copy(alpha = 0.5f),
        backgroundColor = Color(0xFF0D1524)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Receipt Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = shopName, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(text = shopAddress, fontSize = 10.sp, color = TextSecondary)
                    Text(text = "GSTIN: $gst", fontSize = 10.sp, color = TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = invoiceNo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                    Text(text = date, fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))
            Spacer(modifier = Modifier.height(10.dp))

            // Customer & Device Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "BILLED TO:", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(text = customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = customerPhone, fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "DEVICE:", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(text = deviceModel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = "IMEI: ${SecurityUtils.formatImei(imei)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Warranty and CEIR Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3300F5D4))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Warranty: $warranty", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text(text = "CEIR Clean NOC", fontSize = 10.sp, color = EmeraldPass, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Repair & QC Service Log
            Text(text = "QC & SERVICE LOG:", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Text(
                text = repairLog,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))
            Spacer(modifier = Modifier.height(10.dp))

            // Financial Line Items
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Base Device Price", fontSize = 12.sp, color = TextSecondary)
                Text(text = SecurityUtils.formatCurrency(basePrice), fontSize = 12.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Refurbishment & Certification Fee", fontSize = 12.sp, color = TextSecondary)
                Text(text = "+${SecurityUtils.formatCurrency(serviceFee)}", fontSize = 12.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Promotional Discount", fontSize = 12.sp, color = TextSecondary)
                Text(text = "-${SecurityUtils.formatCurrency(discount)}", fontSize = 12.sp, color = EmeraldPass)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))
            Spacer(modifier = Modifier.height(10.dp))

            // Grand Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TOTAL PAYABLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(text = "Inclusive of all taxes", fontSize = 9.sp, color = TextMuted)
                }
                Text(
                    text = SecurityUtils.formatCurrency(finalAmount),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Verification Seal & Sign Stamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPass, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Digital Tamper-Proof Hash: #9042A", fontSize = 9.sp, color = TextMuted)
                }
                Text(
                    text = "Authorized Signature Stamp",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }
        }
    }
}

// Helpers
private fun shareWhatsAppBill(context: Context, billText: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, billText)
        type = "text/plain"
        `package` = "com.whatsapp"
    }

    try {
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        val genericIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, billText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(genericIntent, "Share WhatsApp Invoice"))
    }
}

private fun exportPdfIntent(context: Context, billText: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, billText)
        putExtra(Intent.EXTRA_TITLE, "PhoneKhoj_Invoice_PK2026.pdf")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Export / Print Digital Invoice"))
}
