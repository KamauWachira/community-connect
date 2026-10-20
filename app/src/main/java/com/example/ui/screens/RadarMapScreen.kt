package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.DistanceRadiusBadge
import com.example.ui.components.EnvironmentTag
import com.example.ui.components.HealthSafetyBadge
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

sealed class RadarItem {
    data class PeerItem(val profile: UserProfile, val angleRad: Float) : RadarItem()
    data class BeaconItem(val alert: CommunityAlert, val angleRad: Float) : RadarItem()
    data class SpotItem(val spot: StudySpot, val angleRad: Float) : RadarItem()
}

@Composable
fun RadarMapScreen(
    profiles: List<UserProfile>,
    alerts: List<CommunityAlert>,
    spots: List<StudySpot>,
    maxRadiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    onConnectProfile: (UserProfile) -> Unit,
    onBroadcastAlert: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<RadarItem?>(null) }

    // Map profiles, alerts, and spots to simulated radial coordinates (distance & angle)
    val radarItems = remember(profiles, alerts, spots) {
        val items = mutableListOf<RadarItem>()
        // Fixed angles for stable node positions
        val angles = listOf(0.4f, 1.2f, 2.1f, 2.9f, 3.8f, 4.6f, 5.3f, 5.9f)
        profiles.forEachIndexed { idx, p ->
            items.add(RadarItem.PeerItem(p, angles[idx % angles.size]))
        }
        val beaconAngles = listOf(0.9f, 2.5f, 4.1f, 5.7f)
        alerts.forEachIndexed { idx, a ->
            items.add(RadarItem.BeaconItem(a, beaconAngles[idx % beaconAngles.size]))
        }
        val spotAngles = listOf(1.7f, 3.3f, 4.9f, 0.1f, 2.2f, 3.9f)
        spots.forEachIndexed { idx, s ->
            items.add(RadarItem.SpotItem(s, spotAngles[idx % spotAngles.size]))
        }
        items
    }

    // Filter items within maxRadiusKm
    val visibleItems = remember(radarItems, maxRadiusKm) {
        radarItems.filter { item ->
            when (item) {
                is RadarItem.PeerItem -> item.profile.distanceKm <= maxRadiusKm
                is RadarItem.BeaconItem -> item.alert.distanceKm <= maxRadiusKm
                is RadarItem.SpotItem -> item.spot.distanceKm <= maxRadiusKm
            }
        }
    }

    // Radar rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Control Header
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "5KM INTERACTIVE NEIGHBORHOOD RADAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanLight,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Showing ${visibleItems.size} active nodes in ${String.format("%.1f", maxRadiusKm)} km",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = IndigoPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "👤 Study Peers",
                                fontSize = 10.sp,
                                color = IndigoLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = RoseAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⚡ Beacons",
                                fontSize = 10.sp,
                                color = RoseLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📍 Hubs",
                                fontSize = 10.sp,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Radius slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Zoom: ${String.format("%.1f", maxRadiusKm)} km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.width(96.dp)
                    )
                    Slider(
                        value = maxRadiusKm.toFloat(),
                        onValueChange = { onRadiusChange(it.toDouble()) },
                        valueRange = 1f..5f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Radar Canvas & Nodes Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Radar concentric rings Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxR = minOf(size.width, size.height) / 2f - 24f

                // Draw concentric 1km, 2km, 3km, 4km, 5km rings
                val rings = 5
                for (i in 1..rings) {
                    val r = maxR * (i.toFloat() / rings)
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = r,
                        center = center,
                        style = Stroke(width = 1.2f)
                    )
                }

                // Crosshairs
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(center.x - maxR, center.y),
                    end = Offset(center.x + maxR, center.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(center.x, center.y - maxR),
                    end = Offset(center.x, center.y + maxR),
                    strokeWidth = 1f
                )

                // Rotating radar sweep beam
                val rad = Math.toRadians(sweepAngle.toDouble())
                val beamEnd = Offset(
                    (center.x + maxR * cos(rad)).toFloat(),
                    (center.y + maxR * sin(rad)).toFloat()
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(CyanAccent.copy(alpha = 0.8f), Color.Transparent),
                        start = center,
                        end = beamEnd
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.5f
                )

                // Pulsing Center (You)
                drawCircle(
                    color = CyanAccent.copy(alpha = 0.25f),
                    radius = 20f,
                    center = center
                )
                drawCircle(
                    color = CyanAccent,
                    radius = 6f,
                    center = center
                )
            }

            // Overlay Node Pins as Composables for easy clickability
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val centerX = maxWidth / 2
                val centerY = maxHeight / 2
                val maxRadiusDp = minOf(maxWidth, maxHeight) / 2 - 28.dp

                // Center tag: "You"
                Surface(
                    color = CyanAccent,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "YOU",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                visibleItems.forEach { item ->
                    val (distKm, angleRad, color, icon) = when (item) {
                        is RadarItem.PeerItem -> Quad(item.profile.distanceKm, item.angleRad, Color(item.profile.avatarColorHex), Icons.Default.Person)
                        is RadarItem.BeaconItem -> Quad(item.alert.distanceKm, item.angleRad, RoseAccent, Icons.Default.Campaign)
                        is RadarItem.SpotItem -> Quad(item.spot.distanceKm, item.angleRad, EmeraldSuccess, Icons.Default.Place)
                    }

                    // Fractional distance relative to maxRadiusKm
                    val fraction = (distKm / maxRadiusKm).coerceIn(0.15, 0.95)
                    val r = maxRadiusDp * fraction.toFloat()
                    val xOffset = r * cos(angleRad)
                    val yOffset = r * sin(angleRad)

                    val isSelected = selectedItem == item

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = xOffset, y = yOffset)
                            .size(if (isSelected) 36.dp else 28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else color)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) CyanAccent else Color.White,
                                shape = CircleShape
                            )
                            .clickable { selectedItem = item }
                            .testTag("radar_node_${distKm}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Radar Node",
                            tint = if (isSelected) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.size(if (isSelected) 18.dp else 14.dp)
                        )
                    }
                }
            }
        }

        // Bottom Selected Node Detail Card
        if (selectedItem != null) {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radar_detail_sheet")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (val itm = selectedItem!!) {
                        is RadarItem.PeerItem -> {
                            val p = itm.profile
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${p.name}, ${p.age}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    DistanceRadiusBadge(distanceKm = p.distanceKm, neighborhood = p.neighborhood)
                                }
                                IconButton(onClick = { selectedItem = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                EnvironmentTag(environment = p.preferredEnvironment)
                                HealthSafetyBadge(hivStatus = p.hivStatus, verified = p.safetyGuidelinesVerified)
                            }

                            Text(
                                text = p.bio,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                maxLines = 2
                            )

                            Button(
                                onClick = {
                                    onConnectProfile(p)
                                    selectedItem = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("radar_connect_button")
                            ) {
                                Text("Connect & Request Study Session")
                            }
                        }

                        is RadarItem.BeaconItem -> {
                            val a = itm.alert
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Surface(
                                        color = RoseAccent.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "⚡ ACTIVE BEACON",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoseLight,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = a.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                IconButton(onClick = { selectedItem = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                                }
                            }

                            Text(
                                text = "${a.description} • ${a.locationName} (${String.format("%.1f", a.distanceKm)} km away)",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )

                            Button(
                                onClick = {
                                    selectedItem = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RoseAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Respond to Resource Alert")
                            }
                        }

                        is RadarItem.SpotItem -> {
                            val s = itm.spot
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "📍 ${s.name}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${s.type} • ${s.distanceKm} km • ${s.openStatus}",
                                        fontSize = 11.sp,
                                        color = TextSecondaryDark
                                    )
                                }
                                IconButton(onClick = { selectedItem = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = EmeraldSuccess.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Noise: ${s.noiseLevel}",
                                        fontSize = 11.sp,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "👥 ${s.currentActiveBuddies} active buddies currently here",
                                    fontSize = 12.sp,
                                    color = CyanLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Small helper quad data class
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
