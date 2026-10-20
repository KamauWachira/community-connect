package com.example.ui.screens

import androidx.compose.foundation.Image
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
import com.example.ui.components.InterestChip
import com.example.ui.components.SafetyGuidelinesPledgeDialog
import com.example.ui.theme.*

@Composable
fun RegistrationScreen(
    availableInterests: List<String>,
    onCompleteRegistration: (
        name: String,
        age: Int,
        neighborhood: String,
        category: ProfileCategory,
        bio: String,
        interests: List<String>,
        environment: StudyEnvironment,
        hivStatus: HivStatusDeclaration,
        safetyVerified: Boolean,
        availability: String,
        studyGoal: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("Alex Mercer") }
    var ageText by remember { mutableStateOf("22") }
    var neighborhood by remember { mutableStateOf("University North (0.5 km radius)") }
    var selectedCategory by remember { mutableStateOf(ProfileCategory.BOTH) }
    var bio by remember { mutableStateOf("Computer Science & Data Science student. Looking for focused study peers and occasional acoustic jam sessions!") }
    var selectedInterests by remember { mutableStateOf(setOf("Machine Learning", "Algorithms & Data Structures", "Python / PyTorch")) }
    var selectedEnvironment by remember { mutableStateOf(StudyEnvironment.SILENT_LIBRARY) }
    var selectedHivStatus by remember { mutableStateOf(HivStatusDeclaration.NEGATIVE) }
    var safetyGuidelinesVerified by remember { mutableStateOf(false) }
    var availability by remember { mutableStateOf("Weekdays 3 PM - 7 PM, Sat mornings") }
    var studyGoal by remember { mutableStateOf("Ace final exams & build collaborative projects") }

    var showGuidelinesModal by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    if (showGuidelinesModal) {
        SafetyGuidelinesPledgeDialog(onDismiss = { showGuidelinesModal = false })
    }

    Scaffold(
        containerColor = DarkBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Hero Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF312E81), Color(0xFF0F172A))
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_study_hero_1792468219398),
                        contentDescription = "PeerPoint Study Neighborhood",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.45f
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Surface(
                            color = CyanAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📍 5KM NEIGHBORHOOD CIRCLES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Join PeerPoint",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Find compatible study partners & hobby groups near you",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // Step 1: Basic Information
            item {
                Text(
                    text = "1. Personal Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name or Preferred Nickname") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanAccent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it.filter { char -> char.isDigit() }.take(2) },
                        label = { Text("Age") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reg_age_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Your Neighborhood (5km Hub)") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = IndigoLight) },
                        modifier = Modifier
                            .weight(2f)
                            .testTag("reg_neighborhood_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Looking for:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        ProfileCategory.STUDY_PARTNER,
                        ProfileCategory.HOBBY_GROUP,
                        ProfileCategory.BOTH
                    ).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label, fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_cat_${cat.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Short Bio & Goals") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_bio_input"),
                    maxLines = 3
                )
            }

            // Step 2: Interests (MANDATORY AT LEAST 3)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "2. Select at least 3 Interests *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    val countColor = if (selectedInterests.size >= 3) EmeraldSuccess else RoseAccent
                    Surface(
                        color = countColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${selectedInterests.size} of 3 min",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = countColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Select subjects, skills, or hobbies to match with nearby partners.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Wrap-flow layout simulation using chunked rows
                val chunks = availableInterests.chunked(3)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    chunks.forEach { rowInterests ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowInterests.forEach { item ->
                                val isSelected = selectedInterests.contains(item)
                                InterestChip(
                                    name = item,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedInterests = if (isSelected) {
                                            selectedInterests - item
                                        } else {
                                            selectedInterests + item
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedInterests.size < 3) {
                    Text(
                        text = "⚠️ Please select at least ${3 - selectedInterests.size} more interest(s) to continue.",
                        fontSize = 11.sp,
                        color = RoseLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Step 3: Preferred Study Environment
            item {
                Text(
                    text = "3. Preferred Study Environment *",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Ensures compatible atmosphere when meeting up in the 5km radius.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StudyEnvironment.values().forEach { env ->
                        val isSelected = selectedEnvironment == env
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E2638) else DarkSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) CyanAccent else DarkSurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("env_card_${env.name}")
                                .clickable { selectedEnvironment = env }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = env.emoji, fontSize = 24.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = env.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CyanLight else Color.White
                                    )
                                    Text(
                                        text = env.subtitle,
                                        fontSize = 11.sp,
                                        color = TextSecondaryDark
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedEnvironment = env },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = CyanAccent
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Step 4: Health & HIV Status Declaration
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "4. Health & HIV Status Declaration *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = { showGuidelinesModal = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Health disclosure guidelines info",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Promotes community transparency, zero-stigma, and health safety in our social circles. All choices are respected.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HivStatusDeclaration.values().forEach { status ->
                        val isSelected = selectedHivStatus == status
                        Surface(
                            color = if (isSelected) Color(0xFF1E2638) else DarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) EmeraldSuccess else DarkSurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hiv_status_${status.name}")
                                .clickable { selectedHivStatus = status }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedHivStatus = status },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldSuccess)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = status.displayLabel,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF34D399) else Color.White
                                    )
                                    Text(
                                        text = status.description,
                                        fontSize = 11.sp,
                                        color = TextSecondaryDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step 5: Mandatory Safety Guidelines Verification
            item {
                Surface(
                    color = Color(0xFF1E1B4B),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (safetyGuidelinesVerified) 1.5.dp else 1.5.dp,
                        if (safetyGuidelinesVerified) EmeraldSuccess else RoseAccent
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (safetyGuidelinesVerified) EmeraldSuccess else RoseAccent
                            )
                            Text(
                                text = "5. Mandatory Safety Verification *",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "To ensure compatible and safe matches for academic and social engagement, all members must verify adherence to community safety standards.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { safetyGuidelinesVerified = !safetyGuidelinesVerified }
                                .testTag("mandatory_safety_checkbox_row")
                        ) {
                            Checkbox(
                                checked = safetyGuidelinesVerified,
                                onCheckedChange = { safetyGuidelinesVerified = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldSuccess,
                                    uncheckedColor = RoseAccent
                                ),
                                modifier = Modifier.testTag("mandatory_safety_checkbox")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "I verify that I respect and abide by all community safety guidelines, consent rules, health transparency standards, and zero-harassment policies for local 5km study & hobby meetings.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (safetyGuidelinesVerified) Color.White else Color(0xFFFDA4AF),
                                lineHeight = 18.sp
                            )
                        }

                        TextButton(
                            onClick = { showGuidelinesModal = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Read Full Safety Code →",
                                fontSize = 11.sp,
                                color = CyanLight
                            )
                        }
                    }
                }
            }

            // Error display if any
            if (validationError != null) {
                item {
                    Text(
                        text = "⚠️ $validationError",
                        color = RoseAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Submit Button
            item {
                val canSubmit = name.isNotBlank() && selectedInterests.size >= 3 && safetyGuidelinesVerified

                Button(
                    onClick = {
                        if (!safetyGuidelinesVerified) {
                            validationError = "You must verify the mandatory community safety guidelines."
                            return@Button
                        }
                        if (selectedInterests.size < 3) {
                            validationError = "Please select at least 3 interests."
                            return@Button
                        }
                        if (name.isBlank()) {
                            validationError = "Please enter your name."
                            return@Button
                        }

                        val parsedAge = ageText.toIntOrNull() ?: 21
                        onCompleteRegistration(
                            name,
                            parsedAge,
                            neighborhood,
                            selectedCategory,
                            bio,
                            selectedInterests.toList(),
                            selectedEnvironment,
                            selectedHivStatus,
                            safetyGuidelinesVerified,
                            availability,
                            studyGoal
                        )
                    },
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoPrimary,
                        disabledContainerColor = DarkSurfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("complete_registration_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null)
                        Text(
                            text = "Enter 5km Neighborhood Circles",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
