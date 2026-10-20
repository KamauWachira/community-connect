package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    availableInterests: List<String>,
    onUpdateProfile: (UserProfile) -> Unit,
    onViewSafetyCode: () -> Unit
) {
    if (currentUser == null) return

    var name by remember { mutableStateOf(currentUser.name) }
    var neighborhood by remember { mutableStateOf(currentUser.neighborhood) }
    var bio by remember { mutableStateOf(currentUser.bio) }
    var studyGoal by remember { mutableStateOf(currentUser.studyGoal) }
    var selectedInterests by remember { mutableStateOf(currentUser.interests.toSet()) }
    var selectedEnv by remember { mutableStateOf(currentUser.preferredEnvironment) }
    var selectedHivStatus by remember { mutableStateOf(currentUser.hivStatus) }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Profile Banner Card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(IndigoDark, Color(0xFF0F172A))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary)
                                .border(2.dp, CyanAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(2).uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Member",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "📍 $neighborhood",
                                fontSize = 12.sp,
                                color = CyanLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HealthSafetyBadge(
                                hivStatus = selectedHivStatus,
                                verified = currentUser.safetyGuidelinesVerified
                            )
                        }
                    }
                }
            }
        }

        // Notification when saved
        if (savedMessage != null) {
            item {
                Surface(
                    color = EmeraldSuccess.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ $savedMessage",
                        color = Color(0xFF34D399),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Safety & Health Agreement Verification Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color(0xFF10B981)
                        )
                        Column {
                            Text(
                                text = "Verified Safety & Health Adherence",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Status: ${selectedHivStatus.displayLabel}",
                                fontSize = 11.sp,
                                color = Color(0xFF34D399)
                            )
                        }
                    }

                    Text(
                        text = "You verified full compliance with peer safety guidelines, respect boundaries, and health disclosure norms for 5km neighborhood meetups.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )

                    OutlinedButton(
                        onClick = onViewSafetyCode,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("review_safety_code_button")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Review Community Safety Code", fontSize = 12.sp)
                    }
                }
            }
        }

        // Editable Preferred Study Environment
        item {
            Text(
                text = "Preferred Study Atmosphere:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StudyEnvironment.values().take(3).forEach { env ->
                    FilterChip(
                        selected = selectedEnv == env,
                        onClick = { selectedEnv = env },
                        label = { Text("${env.emoji} ${env.title.split(" ").first()}", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Editable Interests (Minimum 3 validation)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Interests (min 3):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${selectedInterests.size} selected",
                    fontSize = 11.sp,
                    color = if (selectedInterests.size >= 3) EmeraldSuccess else RoseAccent
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            val interestChunks = availableInterests.take(12).chunked(3)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                interestChunks.forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { item ->
                            val isSelected = selectedInterests.contains(item)
                            InterestChip(
                                name = item,
                                isSelected = isSelected,
                                onClick = {
                                    selectedInterests = if (isSelected) {
                                        if (selectedInterests.size > 3) selectedInterests - item else selectedInterests
                                    } else {
                                        selectedInterests + item
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Editable Goals and Bio
        item {
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("About You & Study Habits") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_bio_input"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = studyGoal,
                onValueChange = { studyGoal = it },
                label = { Text("Current Academic / Project Goal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_goal_input"),
                singleLine = true
            )
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = currentUser.copy(
                        name = name,
                        neighborhood = neighborhood,
                        bio = bio,
                        studyGoal = studyGoal,
                        interests = selectedInterests.toList(),
                        preferredEnvironment = selectedEnv,
                        hivStatus = selectedHivStatus
                    )
                    onUpdateProfile(updated)
                    savedMessage = "Profile successfully updated!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_profile_button")
            ) {
                Text("Save Profile Changes", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
