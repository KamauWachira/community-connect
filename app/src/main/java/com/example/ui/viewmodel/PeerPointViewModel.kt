package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NeighborhoodRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTab(val label: String, val iconName: String) {
    DISCOVERY("Discovery", "Swipe"),
    RADAR("5km Radar", "Radar"),
    BEACONS("Beacons", "Alerts"),
    MATCHES("Circles", "Matches"),
    PROFILE("Profile", "Profile")
}

data class PeerPointUiState(
    val isRegistered: Boolean = false,
    val currentUser: UserProfile? = null,
    val currentTab: AppTab = AppTab.DISCOVERY,
    val activeChatMatch: MatchRecord? = null,
    val maxRadiusKm: Double = 5.0,
    val categoryFilter: ProfileCategory = ProfileCategory.ALL,
    val showMatchDialog: Boolean = false,
    val latestMatchedProfile: UserProfile? = null,
    val showBroadcastModal: Boolean = false,
    val showSafetyPledgeDialog: Boolean = false,
    val incomingBannerAlert: CommunityAlert? = null,
    val broadcastSuccessNotification: String? = null
)

class PeerPointViewModel(
    val repository: NeighborhoodRepository = NeighborhoodRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PeerPointUiState())
    val uiState: StateFlow<PeerPointUiState> = _uiState.asStateFlow()

    val discoveryProfiles = repository.discoveryProfiles
    val matches = repository.matches
    val communityAlerts = repository.communityAlerts
    val studySpots = repository.studySpots

    // Available interest catalogue
    val availableInterests = listOf(
        "Machine Learning",
        "Algorithms & Data Structures",
        "Organic Chemistry",
        "Calculus & Math",
        "Python / PyTorch",
        "UI/UX Design",
        "Acoustic Guitar",
        "Cell Biology",
        "Chess Strategy",
        "Game Development",
        "Rust Programming",
        "Architectural Sketching",
        "Japanese Language",
        "Film Analysis",
        "Creative Writing",
        "Urban Gardening",
        "Embedded Systems / IoT",
        "Philosophy & Ethics",
        "Cognitive Psychology",
        "Digital Art & 3D"
    )

    init {
        // Pre-simulate a nearby community alert banner after a few seconds
        viewModelScope.launch {
            delay(4000)
            if (_uiState.value.isRegistered && _uiState.value.incomingBannerAlert == null) {
                val firstAlert = repository.communityAlerts.value.firstOrNull()
                if (firstAlert != null) {
                    _uiState.value = _uiState.value.copy(
                        incomingBannerAlert = firstAlert
                    )
                }
            }
        }
    }

    fun completeRegistration(
        name: String,
        age: Int,
        neighborhood: String,
        category: ProfileCategory,
        bio: String,
        interests: List<String>,
        preferredEnvironment: StudyEnvironment,
        hivStatus: HivStatusDeclaration,
        safetyGuidelinesVerified: Boolean,
        availability: String,
        studyGoal: String
    ): Boolean {
        // Validation: must select at least three interests and mandatory verification must be true!
        if (interests.size < 3 || !safetyGuidelinesVerified || name.isBlank()) {
            return false
        }

        val profile = UserProfile(
            id = "user-current",
            name = name.trim(),
            age = age,
            neighborhood = neighborhood.trim(),
            distanceKm = 0.0,
            category = category,
            bio = bio.trim().ifEmpty { "Eager to learn, collaborate, and share knowledge in our neighborhood!" },
            interests = interests,
            preferredEnvironment = preferredEnvironment,
            hivStatus = hivStatus,
            safetyGuidelinesVerified = safetyGuidelinesVerified,
            availability = availability.trim().ifEmpty { "Flexible weekday afternoons" },
            studyGoal = studyGoal.trim().ifEmpty { "Study focus & collaborative hobby projects" },
            avatarColorHex = 0xFF4F46E5
        )

        repository.registerUser(profile)
        _uiState.value = _uiState.value.copy(
            isRegistered = true,
            currentUser = profile
        )
        return true
    }

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            activeChatMatch = null // Reset chat if navigating away
        )
    }

    fun setRadiusFilter(km: Double) {
        _uiState.value = _uiState.value.copy(maxRadiusKm = km)
    }

    fun setCategoryFilter(category: ProfileCategory) {
        _uiState.value = _uiState.value.copy(categoryFilter = category)
    }

    fun swipeRight(profile: UserProfile) {
        val isMatch = repository.swipeRight(profile)
        if (isMatch) {
            _uiState.value = _uiState.value.copy(
                showMatchDialog = true,
                latestMatchedProfile = profile
            )
        }
    }

    fun swipeLeft(profile: UserProfile) {
        repository.swipeLeft(profile)
    }

    fun superLike(profile: UserProfile) {
        // Priority connection
        swipeRight(profile)
    }

    fun dismissMatchDialog() {
        _uiState.value = _uiState.value.copy(
            showMatchDialog = false,
            latestMatchedProfile = null
        )
    }

    fun openChatFromMatch(profile: UserProfile) {
        val match = matches.value.find { it.partner.id == profile.id }
        _uiState.value = _uiState.value.copy(
            showMatchDialog = false,
            latestMatchedProfile = null,
            currentTab = AppTab.MATCHES,
            activeChatMatch = match
        )
    }

    fun openChat(match: MatchRecord) {
        _uiState.value = _uiState.value.copy(
            activeChatMatch = match
        )
    }

    fun closeChat() {
        _uiState.value = _uiState.value.copy(
            activeChatMatch = null
        )
    }

    fun sendMessage(text: String, isProposal: Boolean = false, proposalInfo: String? = null) {
        val activeMatch = _uiState.value.activeChatMatch ?: return
        repository.sendMessage(activeMatch.id, text, isProposal, proposalInfo)
        // Refresh active chat reference
        val updated = repository.matches.value.find { it.id == activeMatch.id }
        _uiState.value = _uiState.value.copy(activeChatMatch = updated)
    }

    fun toggleBroadcastModal(show: Boolean) {
        _uiState.value = _uiState.value.copy(showBroadcastModal = show)
    }

    fun toggleSafetyPledgeDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSafetyPledgeDialog = show)
    }

    fun broadcastAlert(
        title: String,
        description: String,
        category: AlertCategory,
        urgency: AlertUrgency,
        locationName: String,
        radiusKm: Double
    ) {
        val created = repository.broadcastAlert(
            title = title,
            description = description,
            category = category,
            urgency = urgency,
            locationName = locationName,
            targetRadiusKm = radiusKm
        )
        _uiState.value = _uiState.value.copy(
            showBroadcastModal = false,
            broadcastSuccessNotification = "Beacon sent to nearby members within ${radiusKm.toInt()}km! You'll receive instant alerts when someone responds."
        )

        // Simulate a neighbor responding after a short delay to demonstrate active engagement
        viewModelScope.launch {
            delay(5000)
            repository.respondToAlert(
                created.id,
                "Hey! I saw your beacon near $locationName. I'm nearby and can help out!"
            )
        }
    }

    fun respondToBeacon(alertId: String, message: String) {
        repository.respondToAlert(alertId, message)
    }

    fun dismissBannerAlert() {
        _uiState.value = _uiState.value.copy(incomingBannerAlert = null)
    }

    fun dismissBroadcastSuccess() {
        _uiState.value = _uiState.value.copy(broadcastSuccessNotification = null)
    }

    fun updateProfile(updated: UserProfile) {
        repository.updateProfile(updated)
        _uiState.value = _uiState.value.copy(currentUser = updated)
    }
}
