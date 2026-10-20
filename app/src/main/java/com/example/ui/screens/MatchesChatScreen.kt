package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.DistanceRadiusBadge
import com.example.ui.components.EnvironmentTag
import com.example.ui.components.HealthSafetyBadge
import com.example.ui.theme.*

@Composable
fun MatchesChatScreen(
    matches: List<MatchRecord>,
    activeChatMatch: MatchRecord?,
    studySpots: List<StudySpot>,
    onOpenChat: (MatchRecord) -> Unit,
    onCloseChat: () -> Unit,
    onSendMessage: (text: String, isProposal: Boolean, proposalInfo: String?) -> Unit
) {
    if (activeChatMatch != null) {
        BackHandler {
            onCloseChat()
        }
        DirectChatView(
            match = activeChatMatch,
            studySpots = studySpots,
            onBack = onCloseChat,
            onSendMessage = onSendMessage
        )
    } else {
        MatchesListView(
            matches = matches,
            onOpenChat = onOpenChat
        )
    }
}

@Composable
fun MatchesListView(
    matches: List<MatchRecord>,
    onOpenChat: (MatchRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "STUDY & HOBBY CIRCLES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanLight,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Connected Neighbors (${matches.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // New Mutual Matches Carousel
            if (matches.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent Mutual Matches (within 5km):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(matches) { match ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onOpenChat(match) }
                                    .padding(6.dp)
                                    .testTag("match_bubble_${match.partner.id}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(match.partner.avatarColorHex))
                                        .border(2.dp, CyanAccent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = match.partner.name.take(2).uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = match.partner.name.split(" ").first(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${String.format("%.1f", match.partner.distanceKm)} km",
                                    fontSize = 10.sp,
                                    color = CyanLight
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = DarkSurfaceBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }

            // Conversations List
            item {
                Text(
                    text = "Messages & Study Coordination:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
            }

            if (matches.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PeopleOutline,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Connected Circles Yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Swipe right on discovery cards or broadcast a community resource beacon to connect with nearby peers!",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            } else {
                items(matches) { match ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenChat(match) }
                            .testTag("conversation_item_${match.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(match.partner.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = match.partner.name.take(2).uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = match.partner.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = match.lastMessageTimestamp,
                                        fontSize = 11.sp,
                                        color = TextSecondaryDark
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${match.partner.preferredEnvironment.emoji} • ${String.format("%.1f", match.partner.distanceKm)} km away",
                                        fontSize = 11.sp,
                                        color = CyanLight
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = match.lastMessage,
                                    fontSize = 13.sp,
                                    color = Color(0xFFCBD5E1),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DirectChatView(
    match: MatchRecord,
    studySpots: List<StudySpot>,
    onBack: () -> Unit,
    onSendMessage: (text: String, isProposal: Boolean, proposalInfo: String?) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var showProposeModal by remember { mutableStateOf(false) }

    if (showProposeModal) {
        ProposeStudySessionDialog(
            partner = match.partner,
            spots = studySpots,
            onDismiss = { showProposeModal = false },
            onSendProposal = { spotName, time, topic ->
                val proposalSummary = "$spotName • $time • Topic: $topic"
                onSendMessage(
                    "📅 Proposed Study Session at $spotName ($time) for $topic",
                    true,
                    proposalSummary
                )
                showProposeModal = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Chat Top Bar
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(match.partner.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = match.partner.name.take(2).uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = match.partner.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Health & Safety",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${match.partner.neighborhood} • ${match.partner.hivStatus.shortBadge}",
                            fontSize = 10.sp,
                            color = Color(0xFF34D399)
                        )
                    }
                }

                Button(
                    onClick = { showProposeModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("propose_study_session_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Propose Meetup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(match.messages) { msg ->
                ChatBubble(message = msg)
            }
        }

        // Chat Input Bar
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Message ${match.partner.name}...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp)
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText, false, null)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isMe = message.isFromMe

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (message.isStudyProposal && message.proposalTimeLocation != null) {
            // Structured Study Session Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CyanAccent),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .testTag("study_proposal_card")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = CyanAccent)
                        Text(
                            text = "STUDY SESSION PROPOSAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanLight
                        )
                    }

                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📍 ${message.proposalTimeLocation}",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        } else {
            Surface(
                color = if (isMe) IndigoPrimary else DarkSurfaceElevated,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isMe) CyanAccent.copy(alpha = 0.4f) else DarkSurfaceBorder
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message.timestamp,
                        fontSize = 9.sp,
                        color = if (isMe) Color(0xFFC7D2FE) else TextSecondaryDark,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun ProposeStudySessionDialog(
    partner: UserProfile,
    spots: List<StudySpot>,
    onDismiss: () -> Unit,
    onSendProposal: (spotName: String, time: String, topic: String) -> Unit
) {
    var selectedSpot by remember { mutableStateOf(spots.firstOrNull()?.name ?: "Central Memorial Library") }
    var selectedTime by remember { mutableStateOf("Today at 4:30 PM") }
    var studyTopic by remember { mutableStateOf("Machine Learning & Code Review") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.EventAvailable, contentDescription = null, tint = CyanAccent)
                Text("Propose 5km Meetup", fontWeight = FontWeight.Bold)
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
                    text = "Invite ${partner.name} to study together at a verified 5km neighborhood spot.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Text("Choose 5km Study Spot:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                spots.take(3).forEach { sp ->
                    FilterChip(
                        selected = selectedSpot == sp.name,
                        onClick = { selectedSpot = sp.name },
                        label = { Text("📍 ${sp.name} (${sp.distanceKm} km)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    label = { Text("When (Date & Time)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = studyTopic,
                    onValueChange = { studyTopic = it },
                    label = { Text("Focus Topic / Subject") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSendProposal(selectedSpot, selectedTime, studyTopic) },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("send_proposal_confirm_button")
            ) {
                Text("Send Study Proposal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
