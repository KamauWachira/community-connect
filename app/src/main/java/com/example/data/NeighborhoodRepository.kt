package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class NeighborhoodRepository {

    // Current Logged-in User
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Discovery Feed (nearby candidates within 5km radius)
    private val _discoveryProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val discoveryProfiles: StateFlow<List<UserProfile>> = _discoveryProfiles.asStateFlow()

    // User's Matches
    private val _matches = MutableStateFlow<List<MatchRecord>>(emptyList())
    val matches: StateFlow<List<MatchRecord>> = _matches.asStateFlow()

    // Active Neighborhood Beacons / Resource Alerts
    private val _communityAlerts = MutableStateFlow<List<CommunityAlert>>(emptyList())
    val communityAlerts: StateFlow<List<CommunityAlert>> = _communityAlerts.asStateFlow()

    // Interactive 5km Study Spots for Radar
    val studySpots = listOf(
        StudySpot("sp-1", "Central Memorial Library", "Public Library", 0.6, "124 Campus Blvd", "Open until 11 PM", "Whisper Quiet", 8),
        StudySpot("sp-2", "The Roastery & Tech Lab", "Coffee & Coworking", 1.1, "405 5th Ave", "Open 24/7", "Ambient Music", 12),
        StudySpot("sp-3", "Science Innovation Commons", "University Building", 1.8, "Engineering Quad Bldg 3", "Open until 10 PM", "Collaborative", 15),
        StudySpot("sp-4", "Green Leaf Shaded Pavilion", "Community Park", 2.4, "Highland Park West", "Dawn to Dusk", "Fresh Air / Birds", 4),
        StudySpot("sp-5", "Artisan Brew & Books", "Independent Cafe", 3.2, "88 Oakwood St", "Open until 9 PM", "Moderate Hum", 6),
        StudySpot("sp-6", "Midnight Owl Reading Room", "Quiet Study Hall", 4.5, "1200 North Loop", "24/7 Keycard", "Silent Zone", 9)
    )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val defaultProfiles = listOf(
            UserProfile(
                id = "user-maya",
                name = "Maya Lin",
                age = 22,
                neighborhood = "Campus North (0.4 km away)",
                distanceKm = 0.4,
                category = ProfileCategory.STUDY_PARTNER,
                bio = "Senior CS student working on machine learning & distributed systems. Love whiteboard brainstorms and high-intensity pomodoro sessions!",
                interests = listOf("Machine Learning", "Algorithms & Data Structures", "Python / PyTorch", "UI/UX Design"),
                preferredEnvironment = StudyEnvironment.SILENT_LIBRARY,
                hivStatus = HivStatusDeclaration.NEGATIVE,
                safetyGuidelinesVerified = true,
                availability = "Weekdays 2 PM - 7 PM",
                studyGoal = "Prepping for Technical Interviews & Final Project",
                avatarColorHex = 0xFF6366F1
            ),
            UserProfile(
                id = "user-ethan",
                name = "Ethan Cole",
                age = 24,
                neighborhood = "Downtown Arts Quarter (0.8 km away)",
                distanceKm = 0.8,
                category = ProfileCategory.BOTH,
                bio = "Bioengineering grad student & acoustic guitar enthusiast. Down for rigorous organic chemistry review or acoustic jam sessions after study hours.",
                interests = listOf("Organic Chemistry", "Acoustic Guitar", "Biochemistry", "Chess Strategy"),
                preferredEnvironment = StudyEnvironment.COZY_CAFE,
                hivStatus = HivStatusDeclaration.UNDETECTABLE,
                safetyGuidelinesVerified = true,
                availability = "Mon/Wed/Fri 4 PM - 9 PM",
                studyGoal = "MCAT prep + learning fingerstyle arrangements",
                avatarColorHex = 0xFF06B6D4
            ),
            UserProfile(
                id = "user-zara",
                name = "Zara Patel",
                age = 21,
                neighborhood = "Oakwood Hill (1.3 km away)",
                distanceKm = 1.3,
                category = ProfileCategory.STUDY_PARTNER,
                bio = "Pre-Med junior. Looking for an accountability partner for long weekend library grinds. Big fan of color-coded flashcards and matcha.",
                interests = listOf("Human Anatomy", "Cell Biology", "Spanish Language", "Digital Art"),
                preferredEnvironment = StudyEnvironment.CAMPUS_LOUNGE,
                hivStatus = HivStatusDeclaration.NEGATIVE,
                safetyGuidelinesVerified = true,
                availability = "Tues/Thurs afternoons + Saturdays",
                studyGoal = "Finishing Anatomy lab reports & flashcard mastery",
                avatarColorHex = 0xFFF43F5E
            ),
            UserProfile(
                id = "user-liam",
                name = "Liam Vance",
                age = 25,
                neighborhood = "Westside Commons (2.1 km away)",
                distanceKm = 2.1,
                category = ProfileCategory.HOBBY_GROUP,
                bio = "Full-stack developer organizing weekend game dev jams and board game strategy nights. Always excited to teach Godot and Rust!",
                interests = listOf("Game Development", "Rust & WebAssembly", "Board Games", "Sci-Fi Book Club"),
                preferredEnvironment = StudyEnvironment.CAMPUS_LOUNGE,
                hivStatus = HivStatusDeclaration.CONFIDENTIAL,
                safetyGuidelinesVerified = true,
                availability = "Evenings after 6 PM & Sundays",
                studyGoal = "Ship indie multiplayer game prototype",
                avatarColorHex = 0xFF10B981
            ),
            UserProfile(
                id = "user-elena",
                name = "Elena Rostova",
                age = 23,
                neighborhood = "South Green Park (2.8 km away)",
                distanceKm = 2.8,
                category = ProfileCategory.BOTH,
                bio = "Architecture masters student. I work mostly in cafes with ambient music or outdoors when sunny. Let's sketch building facades or review design history.",
                interests = listOf("Architectural Sketching", "3D Modeling / Blender", "Art History", "Urban Gardening"),
                preferredEnvironment = StudyEnvironment.OUTDOOR_PARK,
                hivStatus = HivStatusDeclaration.NEGATIVE,
                safetyGuidelinesVerified = true,
                availability = "Daily 10 AM - 3 PM",
                studyGoal = "Drafting sustainable urban housing pavilion",
                avatarColorHex = 0xFFF59E0B
            ),
            UserProfile(
                id = "user-marcus",
                name = "Marcus Reed",
                age = 26,
                neighborhood = "Riverside District (3.6 km away)",
                distanceKm = 3.6,
                category = ProfileCategory.STUDY_PARTNER,
                bio = "Applied Mathematics PhD candidate. Specialized in stochastic calculus and mathematical modeling. Looking for focused peers for quiet study.",
                interests = listOf("Advanced Calculus", "Linear Algebra", "Data Science", "Philosophy"),
                preferredEnvironment = StudyEnvironment.SILENT_LIBRARY,
                hivStatus = HivStatusDeclaration.POSITIVE_SUPPORTIVE,
                safetyGuidelinesVerified = true,
                availability = "Late afternoons & evenings",
                studyGoal = "Writing thesis chapter on differential equations",
                avatarColorHex = 0xFF8B5CF6
            ),
            UserProfile(
                id = "user-chloe",
                name = "Chloe Nguyen",
                age = 20,
                neighborhood = "Midtown Square (4.2 km away)",
                distanceKm = 4.2,
                category = ProfileCategory.BOTH,
                bio = "Cognitive Science major & film enthusiast. Love dissecting human behavior, UX research, and debating cinema directors at coffee shops.",
                interests = listOf("Cognitive Psychology", "UI/UX Design", "Film Analysis", "Creative Writing"),
                preferredEnvironment = StudyEnvironment.COZY_CAFE,
                hivStatus = HivStatusDeclaration.NEGATIVE,
                safetyGuidelinesVerified = true,
                availability = "Flexible weekdays",
                studyGoal = "UX Case study portfolio & research paper",
                avatarColorHex = 0xFFEC4899
            ),
            UserProfile(
                id = "user-daniel",
                name = "Daniel Kim",
                age = 23,
                neighborhood = "Pine Crest (4.8 km away)",
                distanceKm = 4.8,
                category = ProfileCategory.STUDY_PARTNER,
                bio = "Night owl electrical engineering student. Currently building IoT sensors and preparing for FE exam. Discord or late night hall.",
                interests = listOf("Embedded Systems", "Circuit Design", "C++ Programming", "Mechanical Keyboards"),
                preferredEnvironment = StudyEnvironment.LATE_NIGHT_HALL,
                hivStatus = HivStatusDeclaration.UNDETECTABLE,
                safetyGuidelinesVerified = true,
                availability = "Night owl (8 PM - 1 AM)",
                studyGoal = "FE Electrical Exam prep & breadboard debugging",
                avatarColorHex = 0xFF0EA5E9
            )
        )
        _discoveryProfiles.value = defaultProfiles

        // Pre-loaded Mutual Match for instant rich experience
        val matchedPartner = defaultProfiles[0] // Maya Lin
        val initialMatch = MatchRecord(
            id = "match-maya-1",
            partner = matchedPartner,
            matchedDate = "Today, 1:15 PM",
            lastMessage = "Hey! I saw you study ML too. Want to grab a study table at the Library?",
            lastMessageTimestamp = "1:20 PM",
            unreadCount = 1,
            messages = listOf(
                ChatMessage(
                    id = "msg-1",
                    senderId = matchedPartner.id,
                    senderName = matchedPartner.name,
                    text = "Hey there! 👋 Saw you're within 0.5km and also study Machine Learning & algorithms!",
                    timestamp = "1:16 PM",
                    isFromMe = false
                ),
                ChatMessage(
                    id = "msg-2",
                    senderId = "me",
                    senderName = "Me",
                    text = "Hi Maya! Yes! I've been grinding PyTorch neural nets all week. Need a silent library partner.",
                    timestamp = "1:18 PM",
                    isFromMe = true
                ),
                ChatMessage(
                    id = "msg-3",
                    senderId = matchedPartner.id,
                    senderName = matchedPartner.name,
                    text = "Hey! I saw you study ML too. Want to grab a study table at the Library?",
                    timestamp = "1:20 PM",
                    isFromMe = false,
                    isStudyProposal = true,
                    proposalTimeLocation = "Central Library • 2nd Floor Silent Room • Today 4:00 PM"
                )
            )
        )
        _matches.value = listOf(initialMatch)

        // Pre-loaded Active Beacons (Resource Alerts within 5km)
        _communityAlerts.value = listOf(
            CommunityAlert(
                id = "alert-1",
                creatorName = "Jordan Taylor",
                creatorNeighborhood = "Campus North",
                title = "Need TI-84 Plus Graphing Calculator!",
                description = "Exam in 45 minutes at Science Hall! Left mine in my dorm across town. Can anyone lend one for 2 hours? Can leave my student ID as collateral!",
                category = AlertCategory.RESOURCE_EQUIPMENT,
                urgency = AlertUrgency.HIGH_1HR,
                locationName = "Central Memorial Library Lobby",
                distanceKm = 0.5,
                timeAgo = "6m ago",
                responses = listOf(
                    BeaconResponse("resp-1", "Marcus R.", "I have a spare TI-84 Silver at 1st floor help desk!", 0.2, "4m ago")
                )
            ),
            CommunityAlert(
                id = "alert-2",
                creatorName = "Ethan Cole",
                creatorNeighborhood = "Downtown Arts Quarter",
                title = "Organic Chem Review Group Forming (3-4 spots)",
                description = "Doing practice problems on electrophilic aromatic substitution before Friday's quiz. Grabbed a corner booth with power outlets!",
                category = AlertCategory.STUDY_BUDDY_NOW,
                urgency = AlertUrgency.HIGH_1HR,
                locationName = "The Roastery & Tech Lab",
                distanceKm = 0.8,
                timeAgo = "18m ago",
                responses = listOf(
                    BeaconResponse("resp-2", "Zara P.", "On my way! Bringing summary synthesis sheets.", 0.6, "12m ago")
                )
            ),
            CommunityAlert(
                id = "alert-3",
                creatorName = "Sora Takahashi",
                creatorNeighborhood = "Westside Commons",
                title = "Japanese JLPT N3 Conversation Practice",
                description = "Practicing listening & speaking for 30 minutes over iced hojicha. All levels welcome to practice together!",
                category = AlertCategory.HOBBY_MEETUP,
                urgency = AlertUrgency.TODAY,
                locationName = "Artisan Brew & Books",
                distanceKm = 2.2,
                timeAgo = "45m ago",
                responses = emptyList()
            ),
            CommunityAlert(
                id = "alert-4",
                creatorName = "Devon Brooks",
                creatorNeighborhood = "Tech Quarter",
                title = "Python PyTorch CUDA memory leak debugging",
                description = "Training a Transformer vision model, getting CUDA out of memory error despite batch size 4. Looking for quick 15-min pair eyes!",
                category = AlertCategory.CONCEPT_HELP,
                urgency = AlertUrgency.TODAY,
                locationName = "Science Innovation Commons Room 210",
                distanceKm = 1.7,
                timeAgo = "1h ago",
                responses = listOf(
                    BeaconResponse("resp-3", "Maya L.", "Check torch.cuda.empty_cache() and detach loss!", 0.4, "50m ago")
                )
            )
        )
    }

    fun registerUser(profile: UserProfile) {
        _currentUser.value = profile
    }

    fun updateProfile(profile: UserProfile) {
        _currentUser.value = profile
    }

    fun swipeRight(profile: UserProfile): Boolean {
        // Remove from discovery
        _discoveryProfiles.value = _discoveryProfiles.value.filter { it.id != profile.id }

        // Mutual match logic: 80% chance of immediate match for rich demo experience!
        val isMatch = true
        if (isMatch) {
            val newMatch = MatchRecord(
                id = "match-${profile.id}-${System.currentTimeMillis()}",
                partner = profile,
                matchedDate = "Just now",
                lastMessage = "You both connected! Say hello to your new study partner.",
                lastMessageTimestamp = "Just now",
                unreadCount = 0,
                messages = listOf(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        senderId = profile.id,
                        senderName = profile.name,
                        text = "Hi! Excited to connect as nearby study buddies. I saw we both like ${profile.interests.firstOrNull() ?: "studying"}!",
                        timestamp = "Just now",
                        isFromMe = false
                    )
                )
            )
            _matches.value = listOf(newMatch) + _matches.value
        }
        return isMatch
    }

    fun swipeLeft(profile: UserProfile) {
        _discoveryProfiles.value = _discoveryProfiles.value.filter { it.id != profile.id }
    }

    fun broadcastAlert(
        title: String,
        description: String,
        category: AlertCategory,
        urgency: AlertUrgency,
        locationName: String,
        targetRadiusKm: Double
    ): CommunityAlert {
        val user = _currentUser.value
        val alert = CommunityAlert(
            id = "alert-${System.currentTimeMillis()}",
            creatorName = user?.name ?: "You",
            creatorNeighborhood = user?.neighborhood ?: "Near You",
            title = title,
            description = description,
            category = category,
            urgency = urgency,
            locationName = locationName,
            distanceKm = 0.1, // local beacon
            timeAgo = "Just now",
            responses = emptyList(),
            isMine = true
        )
        _communityAlerts.value = listOf(alert) + _communityAlerts.value
        return alert
    }

    fun respondToAlert(alertId: String, message: String): Boolean {
        val currentList = _communityAlerts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == alertId }
        if (index != -1) {
            val alert = currentList[index]
            val newResponse = BeaconResponse(
                id = UUID.randomUUID().toString(),
                responderName = _currentUser.value?.name ?: "You",
                message = message,
                distanceKm = 0.2,
                timestamp = "Just now"
            )
            currentList[index] = alert.copy(
                responses = alert.responses + newResponse
            )
            _communityAlerts.value = currentList
            return true
        }
        return false
    }

    fun sendMessage(matchId: String, text: String, isProposal: Boolean = false, proposalInfo: String? = null) {
        val currentMatches = _matches.value.toMutableList()
        val matchIndex = currentMatches.indexOfFirst { it.id == matchId }
        if (matchIndex != -1) {
            val record = currentMatches[matchIndex]
            val newMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                senderId = "me",
                senderName = _currentUser.value?.name ?: "Me",
                text = text,
                timestamp = "Just now",
                isFromMe = true,
                isStudyProposal = isProposal,
                proposalTimeLocation = proposalInfo
            )
            currentMatches[matchIndex] = record.copy(
                lastMessage = if (isProposal) "📅 Study Session Proposal: $text" else text,
                lastMessageTimestamp = "Just now",
                messages = record.messages + newMsg
            )
            _matches.value = currentMatches
        }
    }
}
