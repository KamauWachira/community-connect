package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DiscoveryScreen(
    profiles: List<UserProfile>,
    maxRadiusKm: Double,
    categoryFilter: ProfileCategory,
    onRadiusChange: (Double) -> Unit,
    onCategoryChange: (ProfileCategory) -> Unit,
    onSwipeLeft: (UserProfile) -> Unit,
    onSwipeRight: (UserProfile) -> Unit,
    onSuperLike: (UserProfile) -> Unit,
    onBroadcastClick: () -> Unit,
    showMatchDialog: Boolean,
    latestMatchedProfile: UserProfile?,
    onDismissMatchDialog: () -> Unit,
    onStartChat: (UserProfile) -> Unit
) {
    // Filter profiles by radius & category
    val filteredProfiles = remember(profiles, maxRadiusKm, categoryFilter) {
        profiles.filter { profile ->
            profile.distanceKm <= maxRadiusKm &&
                    (categoryFilter == ProfileCategory.ALL || profile.category == categoryFilter || profile.category == ProfileCategory.BOTH)
        }
    }

    val currentCandidate = filteredProfiles.firstOrNull()
    var showFullBioModal by remember { mutableStateOf<UserProfile?>(null) }

    // Match Celebration Modal
    if (showMatchDialog && latestMatchedProfile != null) {
        MatchCelebrationDialog(
            partner = latestMatchedProfile,
            onDismiss = onDismissMatchDialog,
            onStartChat = { onStartChat(latestMatchedProfile) }
        )
    }

    if (showFullBioModal != null) {
        ProfileDetailModal(
            profile = showFullBioModal!!,
            onDismiss = { showFullBioModal = null },
            onConnect = {
                val p = showFullBioModal!!
                showFullBioModal = null
                onSwipeRight(p)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Filter Bar
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "5KM NEIGHBORHOOD DISCOVERY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanLight,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "${filteredProfiles.size} matches within ${String.format("%.1f", maxRadiusKm)} km",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }

                    // Broadcast Beacon Button
                    Button(
                        onClick = onBroadcastClick,
                        colors = ButtonDefaults.buttonColors(containerColor = RoseAccent),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("discovery_broadcast_beacon_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Resource Alert",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Radius Slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Radius: ${String.format("%.1f", maxRadiusKm)} km",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(96.dp)
                    )
                    Slider(
                        value = maxRadiusKm.toFloat(),
                        onValueChange = { onRadiusChange(it.toDouble()) },
                        valueRange = 1f..5f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("radius_slider")
                    )
                }

                // Category Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileCategory.values().forEach { cat ->
                        FilterChip(
                            selected = categoryFilter == cat,
                            onClick = { onCategoryChange(cat) },
                            label = { Text(cat.label, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_${cat.name}")
                        )
                    }
                }
            }
        }

        // Tinder Card Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentCandidate != null) {
                TinderProfileCard(
                    profile = currentCandidate,
                    onInfoClick = { showFullBioModal = currentCandidate },
                    onSwipeLeft = { onSwipeLeft(currentCandidate) },
                    onSwipeRight = { onSwipeRight(currentCandidate) },
                    onSuperLike = { onSuperLike(currentCandidate) }
                )
            } else {
                // Empty State
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No More Peers in ${String.format("%.1f", maxRadiusKm)} km",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Expand your radius up to 5km or post a community resource beacon to get noticed by active study circles!",
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onRadiusChange(5.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Expand to 5km Maximum", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onBroadcastClick,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Broadcast Resource Beacon")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TinderProfileCard(
    profile: UserProfile,
    onInfoClick: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSuperLike: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkSurfaceBorder),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("tinder_card_${profile.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Header / Avatar Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(profile.avatarColorHex), DarkSurface)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DistanceRadiusBadge(
                            distanceKm = profile.distanceKm,
                            neighborhood = profile.neighborhood
                        )

                        IconButton(
                            onClick = onInfoClick,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DarkSurface.copy(alpha = 0.6f))
                                .testTag("card_info_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Full Info",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${profile.name}, ${profile.age}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            if (profile.isVerifiedNeighbor) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Neighbor",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            EnvironmentTag(environment = profile.preferredEnvironment)
                            HealthSafetyBadge(
                                hivStatus = profile.hivStatus,
                                verified = profile.safetyGuidelinesVerified
                            )
                        }
                    }
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bio
                Text(
                    text = profile.bio,
                    fontSize = 14.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 20.sp
                )

                // Current Goal
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "CURRENT STUDY / HOBBY GOAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                            Text(
                                text = profile.studyGoal,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Interests Chips
                Text(
                    text = "Key Interests & Focus:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    profile.interests.take(4).forEach { interest ->
                        InterestChip(name = interest)
                    }
                }

                // Availability
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Availability: ${profile.availability}",
                        fontSize = 11.sp,
                        color = CyanLight
                    )
                }
            }

            // Tinder Action Controls (Pass, Super-Like, Connect)
            Surface(
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pass (Left Swipe)
                    IconButton(
                        onClick = onSwipeLeft,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .border(1.5.dp, RoseAccent, CircleShape)
                            .testTag("swipe_pass_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Pass",
                            tint = RoseAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Super-Beacon / Priority Invite
                    IconButton(
                        onClick = onSuperLike,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .border(1.5.dp, AmberAccent, CircleShape)
                            .testTag("super_beacon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Super Beacon",
                            tint = AmberAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Connect / Study Buddy (Right Swipe)
                    IconButton(
                        onClick = onSwipeRight,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary)
                            .border(1.5.dp, CyanAccent, CircleShape)
                            .testTag("swipe_connect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Connect",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCelebrationDialog(
    partner: UserProfile,
    onDismiss: () -> Unit,
    onStartChat: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(IndigoPrimary, CyanAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "It's a Study Match! 🎉",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "You and ${partner.name} both want local collaboration within 5km!",
                    fontSize = 14.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "COMPATIBLE PROFILE HIGHLIGHTS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Environment: ${partner.preferredEnvironment.title} (${partner.preferredEnvironment.emoji})",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "• Common Interest: ${partner.interests.firstOrNull() ?: "Studying"}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "• Health & Safety: ${partner.hivStatus.shortBadge} (Verified Guidelines)",
                            fontSize = 12.sp,
                            color = EmeraldSuccess
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStartChat,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("match_start_chat_button")
            ) {
                Text("Chat & Schedule Session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Swiping")
            }
        }
    )
}

@Composable
fun ProfileDetailModal(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onConnect: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = CyanAccent)
                Text("${profile.name}'s Full Profile", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DistanceRadiusBadge(distanceKm = profile.distanceKm, neighborhood = profile.neighborhood)

                Text(
                    text = "About & Bio:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Text(text = profile.bio, fontSize = 13.sp, color = Color.White)

                Text(
                    text = "Preferred Study Atmosphere:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                EnvironmentTag(environment = profile.preferredEnvironment)

                Text(
                    text = "Declared Health & Safety Status:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                HealthSafetyBadge(hivStatus = profile.hivStatus, verified = profile.safetyGuidelinesVerified)
                Text(
                    text = profile.hivStatus.description,
                    fontSize = 11.sp,
                    color = TextSecondaryDark
                )

                Text(
                    text = "Interests:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    profile.interests.forEach {
                        InterestChip(name = it)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConnect,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Connect with ${profile.name}")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
