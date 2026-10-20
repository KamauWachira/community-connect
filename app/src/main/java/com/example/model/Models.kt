package com.example.model

enum class ProfileCategory(val label: String) {
    ALL("All Connections"),
    STUDY_PARTNER("Study Partner"),
    HOBBY_GROUP("Hobby Group"),
    BOTH("Study & Hobby")
}

enum class StudyEnvironment(
    val title: String,
    val subtitle: String,
    val emoji: String
) {
    SILENT_LIBRARY("Silent Library", "Deep focus, zero distraction, individual tables", "📚"),
    COZY_CAFE("Bustling Coffee Shop", "Ambient background hum, lattes, casual vibe", "☕"),
    CAMPUS_LOUNGE("Campus Common Lounge", "Whiteboards, group tables, collaborative energy", "🏛️"),
    OUTDOOR_PARK("Outdoor Park / Green Space", "Fresh air, shaded benches, sunlight", "🌳"),
    LATE_NIGHT_HALL("Late-Night Study Hall", "24/7 access, night owls, intense prep", "🌙"),
    VIRTUAL_DISCORD("Virtual / Discord Hub", "Screen sharing, pomodoro voice channels", "🎧")
}

enum class HivStatusDeclaration(
    val displayLabel: String,
    val shortBadge: String,
    val description: String
) {
    NEGATIVE(
        "HIV Negative (Recently Tested)",
        "Negative (Verified)",
        "Tested negative within the last 6 months. Committed to safer health practices."
    ),
    UNDETECTABLE(
        "Undetectable / On PrEP or ART",
        "U=U / PrEP Protected",
        "On active PrEP prevention or antiretroviral therapy (U=U: Undetectable = Untransmittable)."
    ),
    POSITIVE_SUPPORTIVE(
        "HIV Positive (Open & Supportive)",
        "Positive (Empowered)",
        "Open status, seeking inclusive, stigma-free academic and social peer groups."
    ),
    CONFIDENTIAL(
        "Private / Disclose in 1-on-1 Chat",
        "Confidential / Private",
        "Status kept private on public profile, shared in private direct communication if relevant."
    )
}

data class UserProfile(
    val id: String,
    val name: String,
    val age: Int,
    val neighborhood: String,
    val distanceKm: Double, // distance within 5.0 km
    val category: ProfileCategory,
    val bio: String,
    val interests: List<String>,
    val preferredEnvironment: StudyEnvironment,
    val hivStatus: HivStatusDeclaration,
    val safetyGuidelinesVerified: Boolean,
    val availability: String,
    val studyGoal: String,
    val avatarColorHex: Long = 0xFF4F46E5,
    val isVerifiedNeighbor: Boolean = true,
    val rating: Double = 4.9
)

enum class AlertCategory(val title: String, val emoji: String, val sampleTag: String) {
    RESOURCE_EQUIPMENT("Need Resource / Equipment", "📐", "Calculators, Chargers, Books"),
    STUDY_BUDDY_NOW("Immediate Study Partner", "⚡", "Next 1-2 hours sprint"),
    CONCEPT_HELP("Quick Concept / Code Help", "💡", "Debugging, Proofreading, Math"),
    HOBBY_MEETUP("Hobby / Activity Gathering", "🎸", "Jam, Chess, Language exchange"),
    QUIET_SPACE("Shared Study Spot Alert", "📍", "Found open group study room")
}

enum class AlertUrgency(val label: String, val colorHex: Long) {
    HIGH_1HR("Urgent (Next 60 min)", 0xFFF43F5E),
    TODAY("Today", 0xFFF59E0B),
    THIS_WEEK("Flexible / This Week", 0xFF06B6D4)
}

data class BeaconResponse(
    val id: String,
    val responderName: String,
    val message: String,
    val distanceKm: Double,
    val timestamp: String
)

data class CommunityAlert(
    val id: String,
    val creatorName: String,
    val creatorNeighborhood: String,
    val title: String,
    val description: String,
    val category: AlertCategory,
    val urgency: AlertUrgency,
    val locationName: String,
    val distanceKm: Double,
    val timeAgo: String,
    val responses: List<BeaconResponse> = emptyList(),
    val isMine: Boolean = false
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val isStudyProposal: Boolean = false,
    val proposalTimeLocation: String? = null
)

data class MatchRecord(
    val id: String,
    val partner: UserProfile,
    val matchedDate: String,
    val lastMessage: String,
    val lastMessageTimestamp: String,
    val unreadCount: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

data class StudySpot(
    val id: String,
    val name: String,
    val type: String,
    val distanceKm: Double,
    val address: String,
    val openStatus: String,
    val noiseLevel: String,
    val currentActiveBuddies: Int
)
