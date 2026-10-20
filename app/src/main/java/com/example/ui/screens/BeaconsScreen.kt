package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun BeaconsScreen(
    alerts: List<CommunityAlert>,
    onBroadcastClick: () -> Unit,
    onRespondToBeacon: (alertId: String, message: String) -> Unit,
    broadcastSuccessMessage: String?,
    onDismissSuccess: () -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf<AlertCategory?>(null) }
    var respondingAlertId by remember { mutableStateOf<String?>(null) }
    var responseMessageText by remember { mutableStateOf("") }

    val filteredAlerts = remember(alerts, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) alerts
        else alerts.filter { it.category == selectedCategoryFilter }
    }

    Scaffold(
        containerColor = DarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onBroadcastClick,
                containerColor = RoseAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("broadcast_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = "Broadcast Alert")
                    Text("Broadcast Alert", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Hero Banner
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_beacon_broadcast_1792468232014),
                        contentDescription = "Community Connection within 5km",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = RoseAccent.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⚡ INSTANT NEIGHBORHOOD ALERTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoseLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Community Resource Radar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Need a calculator, textbook, or study partner now? Nearby peers are instantly notified.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // Success Notification Banner if user just broadcasted
            if (broadcastSuccessMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess
                                )
                                Text(
                                    text = broadcastSuccessMessage,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            IconButton(onClick = onDismissSuccess, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Categories Filter Bar
            item {
                Text(
                    text = "Filter Active Beacons:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All Beacons", fontSize = 11.sp) }
                    )
                    AlertCategory.values().take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat },
                            label = { Text("${cat.emoji} ${cat.sampleTag.split(",").first()}", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Alert Cards
            items(filteredAlerts) { alert ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (alert.isMine) Color(0xFF1E2638) else DarkSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (alert.isMine) RoseAccent else DarkSurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("alert_card_${alert.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color(alert.urgency.colorHex).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = alert.urgency.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(alert.urgency.colorHex),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    color = DarkSurfaceElevated,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${alert.category.emoji} ${alert.category.title}",
                                        fontSize = 10.sp,
                                        color = CyanLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${String.format("%.1f", alert.distanceKm)} km • ${alert.timeAgo}",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }

                        Text(
                            text = alert.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = alert.description,
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${alert.locationName} (${alert.creatorName})",
                                fontSize = 11.sp,
                                color = CyanLight
                            )
                        }

                        // Existing Responses
                        if (alert.responses.isNotEmpty()) {
                            HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.8.dp)
                            Text(
                                text = "💬 Community Responses (${alert.responses.size}):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                            alert.responses.forEach { resp ->
                                Surface(
                                    color = DarkSurfaceElevated,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "${resp.responderName} (${resp.distanceKm} km away)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = resp.message,
                                                fontSize = 12.sp,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        }
                                        Text(
                                            text = resp.timestamp,
                                            fontSize = 10.sp,
                                            color = TextSecondaryDark
                                        )
                                    }
                                }
                            }
                        }

                        // Respond Action / Quick input
                        if (respondingAlertId == alert.id) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = responseMessageText,
                                    onValueChange = { responseMessageText = it },
                                    placeholder = { Text("e.g. I have a spare calculator / can join now!") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("beacon_reply_input"),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        if (responseMessageText.isNotBlank()) {
                                            onRespondToBeacon(alert.id, responseMessageText)
                                            responseMessageText = ""
                                            respondingAlertId = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                    modifier = Modifier.testTag("send_beacon_reply_button")
                                ) {
                                    Text("Send", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        respondingAlertId = alert.id
                                    },
                                    modifier = Modifier.testTag("open_reply_${alert.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Reply,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = CyanAccent
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Respond to Neighbor", color = CyanAccent, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
