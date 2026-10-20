package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun HealthSafetyBadge(
    hivStatus: HivStatusDeclaration,
    verified: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF10B981).copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = "Verified Health & Safety",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "${hivStatus.shortBadge} • Guidelines Verified",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF34D399)
            )
        }
    }
}

@Composable
fun DistanceRadiusBadge(
    distanceKm: Double,
    neighborhood: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF06B6D4).copy(alpha = 0.16f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location distance",
                tint = CyanAccent,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "${String.format("%.1f", distanceKm)} km • $neighborhood",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = CyanLight
            )
        }
    }
}

@Composable
fun EnvironmentTag(
    environment: StudyEnvironment,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF818CF8).copy(alpha = 0.16f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8).copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = environment.emoji, fontSize = 12.sp)
            Text(
                text = environment.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = IndigoLight
            )
        }
    }
}

@Composable
fun InterestChip(
    name: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) IndigoPrimary else DarkSurfaceElevated
    val textColor = if (isSelected) Color.White else TextSecondaryDark
    val borderColor = if (isSelected) CyanAccent else DarkSurfaceBorder

    Surface(
        color = bg,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
            .testTag("interest_chip_${name.replace(" ", "_")}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
fun IncomingBeaconBanner(
    alert: CommunityAlert,
    onDismiss: () -> Unit,
    onViewAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1B4B)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, RoseAccent),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("incoming_beacon_banner")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(RoseAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = alert.category.emoji, fontSize = 16.sp)
                    }
                    Column {
                        Text(
                            text = "⚡ NEARBY RESOURCE ALERT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoseLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${alert.distanceKm} km away • ${alert.locationName}",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("dismiss_banner_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Alert",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = alert.description,
                fontSize = 13.sp,
                color = Color(0xFFCBD5E1),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onViewAlert,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("view_beacon_button")
                ) {
                    Text(
                        text = "I Can Help / Connect",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BroadcastBeaconModal(
    onDismiss: () -> Unit,
    onBroadcast: (title: String, desc: String, category: AlertCategory, urgency: AlertUrgency, location: String, radius: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AlertCategory.RESOURCE_EQUIPMENT) }
    var selectedUrgency by remember { mutableStateOf(AlertUrgency.HIGH_1HR) }
    var locationName by remember { mutableStateOf("Central Library Study Commons") }
    var radiusKm by remember { mutableStateOf(3.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = RoseAccent
                )
                Text("Broadcast Community Alert", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Instantly notify members in your neighborhood radius for study supplies, instant study sessions, or assistance.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What do you need?") },
                    placeholder = { Text("e.g. Graphing Calculator, Python debugging...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("beacon_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details & Urgency") },
                    placeholder = { Text("Describe location details, what you're working on...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("beacon_desc_input"),
                    maxLines = 3
                )

                Text("Alert Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AlertCategory.values().take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text("${cat.emoji} ${cat.title.split(" ").take(2).joinToString(" ")}", fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Meetup Location (within radius)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Broadcast Radius: ${radiusKm.toInt()} km",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = radiusKm.toFloat(),
                    onValueChange = { radiusKm = it.toDouble() },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanAccent,
                        activeTrackColor = CyanAccent
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onBroadcast(
                            title,
                            description.ifEmpty { "Looking for local peer connection or resource support." },
                            selectedCategory,
                            selectedUrgency,
                            locationName,
                            radiusKm
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = RoseAccent),
                modifier = Modifier.testTag("confirm_broadcast_button")
            ) {
                Text("Broadcast Instantly")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SafetyGuidelinesPledgeDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Community Safety & Health Guidelines",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PeerPoint is dedicated to creating a safe, respectful, and stigma-free neighborhood for local academic and social enrichment.",
                    fontSize = 13.sp,
                    color = TextPrimaryDark
                )

                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "1. Mutual Respect & Zero Harassment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CyanLight
                        )
                        Text(
                            text = "Every member must be treated with dignity. Harassment, discrimination, or non-consensual behavior results in immediate account removal.",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )

                        Text(
                            text = "2. Health Transparency & Confidentiality",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CyanLight
                        )
                        Text(
                            text = "Declared health statuses (such as HIV status, U=U, and testing commitments) are shared with mutual respect. Disclosed health information must never be broadcast or shared without explicit consent.",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )

                        Text(
                            text = "3. Public Meetups in 5km Study Hubs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CyanLight
                        )
                        Text(
                            text = "Always conduct initial study sessions and hobby meetups in well-lit public venues (libraries, cafes, campus common lounges).",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Understood & Agreed")
            }
        }
    )
}
