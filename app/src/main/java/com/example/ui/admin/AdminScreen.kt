package com.example.ui.admin

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiagnosticRecord
import com.example.data.model.User
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.PulsingSyncIndicator
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningBg
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.CrimsonFailBg
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.EmeraldPassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhoneKhojViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val diagnosticsHistory by viewModel.diagnosticHistory.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val totalStock by viewModel.activeStockCount.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6366F1))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEEF2FF)
                        ) {
                            Text(
                                text = "ADMIN AUDIT & REGULATORY DESK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4F46E5),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        PulsingSyncIndicator(text = "Regulatory Node")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "RefurbIQ National Audit Console",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Supervising merchant compliance, Sanchar Saathi CEIR blacklist synchronizations, and Room SQLite diagnostic records.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatCard(title = "Merchants", value = allUsers.size.toString(), modifier = Modifier.weight(1f))
                        AdminStatCard(title = "QC Records", value = diagnosticsHistory.size.toString(), modifier = Modifier.weight(1f))
                        AdminStatCard(title = "Active Stock", value = totalStock.toString(), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Role Switch Bar
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Preview As Another Role", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Seamlessly test merchant or buyer views", fontSize = 11.sp, color = TextSecondary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.switchRole("SELLER")
                                    Toast.makeText(context, "Switched to Seller View", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(text = "Seller", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AzureBlue, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.switchRole("CONSUMER")
                                    Toast.makeText(context, "Switched to Consumer View", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5)
                        ) {
                            Text(text = "Consumer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPass, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }

        // Merchant Accounts List
        item {
            Text(
                text = "Registered System Accounts (${allUsers.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(allUsers) { user ->
            MerchantUserCard(user = user)
        }

        // SQLite QC Diagnostic Logs
        item {
            Text(
                text = "Room SQLite Diagnostic History (${diagnosticsHistory.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (diagnosticsHistory.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No diagnostic records saved yet in SQLite database.", fontSize = 13.sp, color = TextMuted)
                        Text(text = "Run a 2-min hardware test from Seller mode to generate audit logs.", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(diagnosticsHistory) { record ->
                AdminDiagnosticLogCard(record = record, onDelete = {
                    viewModel.deleteDiagnostic(record)
                    Toast.makeText(context, "Deleted record #${record.id}", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = LightCardSubtle,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }
    }
}

@Composable
fun MerchantUserCard(user: User) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = if (user.role == "ADMIN") Color(0xFFEEF2FF) else if (user.role == "SELLER") Color(0xFFEFF6FF) else Color(0xFFECFDF5)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (user.role == "ADMIN") Icons.Default.Security else if (user.role == "SELLER") Icons.Default.Store else Icons.Default.People,
                            contentDescription = null,
                            tint = if (user.role == "ADMIN") Color(0xFF4F46E5) else if (user.role == "SELLER") AzureBlue else EmeraldPass,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = user.ownerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "${user.email} • ${user.phone}", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (user.role == "ADMIN") Color(0xFFEEF2FF) else if (user.role == "SELLER") Color(0xFFEFF6FF) else Color(0xFFECFDF5)
            ) {
                Text(
                    text = user.role,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (user.role == "ADMIN") Color(0xFF4F46E5) else if (user.role == "SELLER") AzureBlue else EmeraldPass,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AdminDiagnosticLogCard(record: DiagnosticRecord, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
    val dateStr = sdf.format(Date(record.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = record.deviceModel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "IMEI: ${record.imei} • $dateStr", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (record.healthScore >= 90) EmeraldPassBg else AmberWarningBg
                    ) {
                        Text(
                            text = "${record.healthScore}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (record.healthScore >= 90) EmeraldPass else AmberWarning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonFail, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
