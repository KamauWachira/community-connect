package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BroadcastBeaconModal
import com.example.ui.components.IncomingBeaconBanner
import com.example.ui.components.SafetyGuidelinesPledgeDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.PeerPointViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PeerPointViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PeerPointApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PeerPointApp(viewModel: PeerPointViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profiles by viewModel.discoveryProfiles.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val communityAlerts by viewModel.communityAlerts.collectAsStateWithLifecycle()
    val spots = viewModel.studySpots

    if (uiState.showBroadcastModal) {
        BroadcastBeaconModal(
            onDismiss = { viewModel.toggleBroadcastModal(false) },
            onBroadcast = { title, desc, cat, urg, loc, rad ->
                viewModel.broadcastAlert(title, desc, cat, urg, loc, rad)
            }
        )
    }

    if (uiState.showSafetyPledgeDialog) {
        SafetyGuidelinesPledgeDialog(
            onDismiss = { viewModel.toggleSafetyPledgeDialog(false) }
        )
    }

    if (!uiState.isRegistered) {
        RegistrationScreen(
            availableInterests = viewModel.availableInterests,
            onCompleteRegistration = { name, age, nbr, cat, bio, interests, env, hiv, safety, avail, goal ->
                viewModel.completeRegistration(
                    name = name,
                    age = age,
                    neighborhood = nbr,
                    category = cat,
                    bio = bio,
                    interests = interests,
                    preferredEnvironment = env,
                    hivStatus = hiv,
                    safetyGuidelinesVerified = safety,
                    availability = avail,
                    studyGoal = goal
                )
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBg,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                // If direct chat is active, hide bottom bar for full screen chat experience
                if (uiState.activeChatMatch == null) {
                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .testTag("main_bottom_nav")
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        NavigationBarItem(
                            selected = uiState.currentTab == AppTab.DISCOVERY,
                            onClick = { viewModel.selectTab(AppTab.DISCOVERY) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == AppTab.DISCOVERY) Icons.Filled.Style else Icons.Outlined.Style,
                                    contentDescription = "Discovery"
                                )
                            },
                            label = { Text("Discover", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanAccent,
                                selectedTextColor = CyanLight,
                                indicatorColor = DarkSurfaceElevated,
                                unselectedIconColor = TextSecondaryDark,
                                unselectedTextColor = TextSecondaryDark
                            ),
                            modifier = Modifier.testTag("nav_tab_discovery")
                        )

                        NavigationBarItem(
                            selected = uiState.currentTab == AppTab.RADAR,
                            onClick = { viewModel.selectTab(AppTab.RADAR) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == AppTab.RADAR) Icons.Filled.Radar else Icons.Outlined.Radar,
                                    contentDescription = "5km Radar"
                                )
                            },
                            label = { Text("5km Radar", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanAccent,
                                selectedTextColor = CyanLight,
                                indicatorColor = DarkSurfaceElevated,
                                unselectedIconColor = TextSecondaryDark,
                                unselectedTextColor = TextSecondaryDark
                            ),
                            modifier = Modifier.testTag("nav_tab_radar")
                        )

                        NavigationBarItem(
                            selected = uiState.currentTab == AppTab.BEACONS,
                            onClick = { viewModel.selectTab(AppTab.BEACONS) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (communityAlerts.isNotEmpty()) {
                                            Badge(containerColor = RoseAccent) {
                                                Text("${communityAlerts.size}", fontSize = 9.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (uiState.currentTab == AppTab.BEACONS) Icons.Filled.Campaign else Icons.Outlined.Campaign,
                                        contentDescription = "Beacons"
                                    )
                                }
                            },
                            label = { Text("Alerts", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoseAccent,
                                selectedTextColor = RoseLight,
                                indicatorColor = DarkSurfaceElevated,
                                unselectedIconColor = TextSecondaryDark,
                                unselectedTextColor = TextSecondaryDark
                            ),
                            modifier = Modifier.testTag("nav_tab_beacons")
                        )

                        NavigationBarItem(
                            selected = uiState.currentTab == AppTab.MATCHES,
                            onClick = { viewModel.selectTab(AppTab.MATCHES) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        val unreadTotal = matches.sumOf { it.unreadCount }
                                        if (unreadTotal > 0) {
                                            Badge(containerColor = CyanAccent) {
                                                Text("$unreadTotal", fontSize = 9.sp, color = Color(0xFF0F172A))
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (uiState.currentTab == AppTab.MATCHES) Icons.Filled.Forum else Icons.Outlined.Forum,
                                        contentDescription = "Circles"
                                    )
                                }
                            },
                            label = { Text("Circles", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanAccent,
                                selectedTextColor = CyanLight,
                                indicatorColor = DarkSurfaceElevated,
                                unselectedIconColor = TextSecondaryDark,
                                unselectedTextColor = TextSecondaryDark
                            ),
                            modifier = Modifier.testTag("nav_tab_matches")
                        )

                        NavigationBarItem(
                            selected = uiState.currentTab == AppTab.PROFILE,
                            onClick = { viewModel.selectTab(AppTab.PROFILE) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentTab == AppTab.PROFILE) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanAccent,
                                selectedTextColor = CyanLight,
                                indicatorColor = DarkSurfaceElevated,
                                unselectedIconColor = TextSecondaryDark,
                                unselectedTextColor = TextSecondaryDark
                            ),
                            modifier = Modifier.testTag("nav_tab_profile")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Screen content
                when (uiState.currentTab) {
                    AppTab.DISCOVERY -> {
                        DiscoveryScreen(
                            profiles = profiles,
                            maxRadiusKm = uiState.maxRadiusKm,
                            categoryFilter = uiState.categoryFilter,
                            onRadiusChange = { viewModel.setRadiusFilter(it) },
                            onCategoryChange = { viewModel.setCategoryFilter(it) },
                            onSwipeLeft = { viewModel.swipeLeft(it) },
                            onSwipeRight = { viewModel.swipeRight(it) },
                            onSuperLike = { viewModel.superLike(it) },
                            onBroadcastClick = { viewModel.toggleBroadcastModal(true) },
                            showMatchDialog = uiState.showMatchDialog,
                            latestMatchedProfile = uiState.latestMatchedProfile,
                            onDismissMatchDialog = { viewModel.dismissMatchDialog() },
                            onStartChat = { viewModel.openChatFromMatch(it) }
                        )
                    }

                    AppTab.RADAR -> {
                        RadarMapScreen(
                            profiles = profiles,
                            alerts = communityAlerts,
                            spots = spots,
                            maxRadiusKm = uiState.maxRadiusKm,
                            onRadiusChange = { viewModel.setRadiusFilter(it) },
                            onConnectProfile = { viewModel.swipeRight(it) },
                            onBroadcastAlert = { viewModel.toggleBroadcastModal(true) }
                        )
                    }

                    AppTab.BEACONS -> {
                        BeaconsScreen(
                            alerts = communityAlerts,
                            onBroadcastClick = { viewModel.toggleBroadcastModal(true) },
                            onRespondToBeacon = { id, msg -> viewModel.respondToBeacon(id, msg) },
                            broadcastSuccessMessage = uiState.broadcastSuccessNotification,
                            onDismissSuccess = { viewModel.dismissBroadcastSuccess() }
                        )
                    }

                    AppTab.MATCHES -> {
                        MatchesChatScreen(
                            matches = matches,
                            activeChatMatch = uiState.activeChatMatch,
                            studySpots = spots,
                            onOpenChat = { viewModel.openChat(it) },
                            onCloseChat = { viewModel.closeChat() },
                            onSendMessage = { text, isProposal, info ->
                                viewModel.sendMessage(text, isProposal, info)
                            }
                        )
                    }

                    AppTab.PROFILE -> {
                        ProfileScreen(
                            currentUser = uiState.currentUser,
                            availableInterests = viewModel.availableInterests,
                            onUpdateProfile = { viewModel.updateProfile(it) },
                            onViewSafetyCode = { viewModel.toggleSafetyPledgeDialog(true) }
                        )
                    }
                }

                // Incoming Beacon Popup Alert Banner (Simulated Real-Time Alert)
                AnimatedVisibility(
                    visible = uiState.incomingBannerAlert != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    uiState.incomingBannerAlert?.let { alert ->
                        IncomingBeaconBanner(
                            alert = alert,
                            onDismiss = { viewModel.dismissBannerAlert() },
                            onViewAlert = {
                                viewModel.dismissBannerAlert()
                                viewModel.selectTab(AppTab.BEACONS)
                            }
                        )
                    }
                }
            }
        }
    }
}
