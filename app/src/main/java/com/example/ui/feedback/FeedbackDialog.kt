package com.example.ui.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PhoneKhojViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFeedbackDialog(
    viewModel: PhoneKhojViewModel,
    modifier: Modifier = Modifier
) {
    val isOpen by viewModel.feedbackDialogOpen.collectAsState()
    val rating by viewModel.feedbackRating.collectAsState()
    val reviewText by viewModel.feedbackReviewText.collectAsState()
    val customerName by viewModel.feedbackCustomerName.collectAsState()
    val deviceModel by viewModel.feedbackDeviceModel.collectAsState()
    val serviceType by viewModel.feedbackServiceType.collectAsState()

    if (!isOpen) return

    BasicAlertDialog(
        onDismissRequest = { viewModel.feedbackDialogOpen.value = false },
        modifier = modifier.fillMaxWidth(0.95f)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF101726),
            borderColor = CyberCyan.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Customer Review & Rating",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$serviceType Completed: $deviceModel",
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }
                    IconButton(
                        onClick = { viewModel.feedbackDialogOpen.value = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { viewModel.feedbackRating.value = i },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("star_rate_$i")
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i Stars",
                                tint = if (i <= rating) AmberWarning else Color(0x40FFFFFF),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Text(
                    text = when (rating) {
                        5 -> "⭐⭐⭐⭐⭐ Outstanding Experience!"
                        4 -> "⭐⭐⭐⭐ Great & Reliable Service"
                        3 -> "⭐⭐⭐ Satisfactory Service"
                        2 -> "⭐⭐ Needs Improvement"
                        else -> "⭐ Unsatisfactory"
                    },
                    color = AmberWarning,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Customer Name Input
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { viewModel.feedbackCustomerName.value = it },
                    label = { Text("Customer Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_customer_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = CyberCyan
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Review Text Input
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { viewModel.feedbackReviewText.value = it },
                    label = { Text("Customer Feedback / Experience Notes") },
                    placeholder = { Text("e.g. Fair valuation, clean CEIR verification, and quick billing.") },
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_review_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = CyberCyan
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeonButton(
                        text = "Cancel",
                        onClick = { viewModel.feedbackDialogOpen.value = false },
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "feedback_cancel_btn"
                    )
                    NeonButton(
                        text = "Submit & Publish",
                        onClick = { viewModel.submitFeedback() },
                        modifier = Modifier.weight(1f),
                        testTag = "feedback_submit_btn"
                    )
                }
            }
        }
    }
}
