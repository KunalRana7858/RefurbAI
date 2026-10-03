package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.admin.AdminScreen
import com.example.ui.auth.AuthContainer
import com.example.ui.components.ContactSupportModal
import com.example.ui.components.CookieConsentBanner
import com.example.ui.components.FloatingContactFab
import com.example.ui.components.GlobalConfirmationModal
import com.example.ui.components.MobileNavigationDrawerSheet
import com.example.ui.components.PulsingSyncIndicator
import com.example.ui.components.UtmCampaignModal
import com.example.ui.consumer.ConsumerMarketScreen
import com.example.ui.consumer.ConsumerSellScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.diagnostics.DiagnosticsScreen
import com.example.ui.feedback.CustomerFeedbackDialog
import com.example.ui.invoice.InvoiceScreen
import com.example.ui.kyc.KycScreen
import com.example.ui.sancharsaathi.SancharSaathiScreen
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.EmeraldPassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightCardSubtle
import com.example.ui.theme.PhoneKhojTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.valuation.ValuationScreen
import com.example.viewmodel.PhoneKhojViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PhoneKhojViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            PhoneKhojTheme(darkTheme = isDarkMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: PhoneKhojViewModel) {
    val authState by viewModel.authScreenState.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val isMenuOpen by viewModel.isMobileMenuOpen.collectAsState()
    val isContactOpen by viewModel.isContactDialogOpen.collectAsState()
    val isUtmOpen by viewModel.isUtmDialogOpen.collectAsState()
    val isCookieDismissed by viewModel.isCookieBannerDismissed.collectAsState()

    if (authState != "AUTHENTICATED") {
        AuthContainer(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            topBar = {
                RefurbIqTopBar(
                    viewModel = viewModel,
                    userRole = userRole,
                    userName = currentUser?.ownerName ?: if (userRole == "CONSUMER") "Consumer" else if (userRole == "ADMIN") "Administrator" else "Merchant",
                    shopName = currentUser?.shopName ?: "RefurbIQ Store",
                    onLogout = { viewModel.logout() },
                    onOpenMenu = { viewModel.isMobileMenuOpen.value = true }
                )
            },
            bottomBar = {
                RefurbIqBottomBar(
                    userRole = userRole,
                    selectedTab = selectedTab,
                    onSelectTab = { viewModel.selectedTab.value = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (userRole) {
                    "CONSUMER" -> {
                        when (selectedTab) {
                            0 -> SancharSaathiScreen(viewModel = viewModel)
                            1 -> ConsumerMarketScreen(viewModel = viewModel)
                            2 -> ConsumerSellScreen(viewModel = viewModel)
                            3 -> InvoiceScreen(viewModel = viewModel)
                            else -> SancharSaathiScreen(viewModel = viewModel)
                        }
                    }
                    "ADMIN" -> {
                        when (selectedTab) {
                            0 -> AdminScreen(viewModel = viewModel)
                            1 -> SancharSaathiScreen(viewModel = viewModel)
                            2 -> DashboardScreen(viewModel = viewModel)
                            else -> AdminScreen(viewModel = viewModel)
                        }
                    }
                    else -> { // "SELLER"
                        when (selectedTab) {
                            0 -> DashboardScreen(viewModel = viewModel)
                            1 -> DiagnosticsScreen(viewModel = viewModel)
                            2 -> SancharSaathiScreen(viewModel = viewModel)
                            3 -> ValuationScreen(viewModel = viewModel)
                            4 -> KycScreen(viewModel = viewModel)
                            5 -> InvoiceScreen(viewModel = viewModel)
                            else -> DashboardScreen(viewModel = viewModel)
                        }
                    }
                }

                // Floating Contact Support Button in Bottom-Right
                FloatingContactFab(
                    onClick = { viewModel.isContactDialogOpen.value = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                )

                // Simple Cookie / Telemetry Banner
                CookieConsentBanner(
                    isDismissed = isCookieDismissed,
                    onAccept = { viewModel.isCookieBannerDismissed.value = true },
                    onCustomize = { viewModel.isCookieBannerDismissed.value = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp)
                )

                // Global Customer Feedback Overlay Dialog
                CustomerFeedbackDialog(viewModel = viewModel)

                // Global Modals
                MobileNavigationDrawerSheet(
                    viewModel = viewModel,
                    isOpen = isMenuOpen,
                    onDismiss = { viewModel.isMobileMenuOpen.value = false }
                )

                ContactSupportModal(
                    isOpen = isContactOpen,
                    onDismiss = { viewModel.isContactDialogOpen.value = false }
                )

                UtmCampaignModal(
                    viewModel = viewModel,
                    isOpen = isUtmOpen,
                    onDismiss = { viewModel.isUtmDialogOpen.value = false }
                )

                GlobalConfirmationModal(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RefurbIqTopBar(
    viewModel: PhoneKhojViewModel,
    userRole: String,
    userName: String,
    shopName: String,
    onLogout: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val isPassive by viewModel.isPassiveVisitMode.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mobile Menu Hamburger Button
                IconButton(
                    onClick = onOpenMenu,
                    modifier = Modifier.size(36.dp).testTag("topbar_btn_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "RefurbIQ",
                            tint = AzureBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RefurbIQ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Role Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (userRole) {
                                "ADMIN" -> Color(0xFFEEF2FF)
                                "CONSUMER" -> EmeraldPassBg
                                else -> Color(0xFFEFF6FF)
                            }
                        ) {
                            Text(
                                text = userRole,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (userRole) {
                                    "ADMIN" -> ElectricIndigo
                                    "CONSUMER" -> EmeraldPass
                                    else -> AzureBlue
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (userRole == "CONSUMER") "Buyer: $userName" else if (userRole == "ADMIN") "Regulatory Audit Console" else shopName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Passive Visit Privacy Mode Toggle
                IconButton(
                    onClick = { viewModel.isPassiveVisitMode.value = !isPassive },
                    modifier = Modifier.size(32.dp).testTag("topbar_btn_passive")
                ) {
                    Icon(
                        imageVector = if (isPassive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Passive Privacy Mode",
                        tint = if (isPassive) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Dark Mode Toggle Button
                IconButton(
                    onClick = { viewModel.isDarkMode.value = !isDark },
                    modifier = Modifier.size(32.dp).testTag("topbar_btn_dark_mode")
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = if (isDark) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(onClick = onLogout, modifier = Modifier.size(32.dp).testTag("topbar_btn_logout")) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}


data class NavItem(
    val index: Int,
    val title: String,
    val icon: ImageVector
)

@Composable
fun RefurbIqBottomBar(
    userRole: String,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit
) {
    val items = when (userRole) {
        "CONSUMER" -> listOf(
            NavItem(0, "Sanchar Saathi", Icons.Default.Shield),
            NavItem(1, "Market", Icons.Default.ShoppingBag),
            NavItem(2, "Sell Phone", Icons.Default.MonetizationOn),
            NavItem(3, "Invoice", Icons.Default.Receipt)
        )
        "ADMIN" -> listOf(
            NavItem(0, "Admin Desk", Icons.Default.AdminPanelSettings),
            NavItem(1, "Sanchar Saathi", Icons.Default.Shield),
            NavItem(2, "Inventory", Icons.Default.Dashboard)
        )
        else -> listOf( // "SELLER"
            NavItem(0, "Command", Icons.Default.Dashboard),
            NavItem(1, "Diagnostics", Icons.Default.Speed),
            NavItem(2, "Sanchar Saathi", Icons.Default.Shield),
            NavItem(3, "Valuation", Icons.Default.Calculate),
            NavItem(4, "Legal KYC", Icons.Default.Security),
            NavItem(5, "Invoice", Icons.Default.Receipt)
        )
    }

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(1.dp, GlassBorder),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = (selectedTab == item.index)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(item.index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzureBlue,
                    selectedTextColor = AzureBlue,
                    indicatorColor = Color(0xFFEFF6FF),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_${item.index}")
            )
        }
    }
}
