package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NeighborhoodRepository
import com.example.model.*
import com.example.ui.viewmodel.PeerPointViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PeerPoint", appName)
    }

    @Test
    fun registrationValidation_requiresThreeInterestsAndSafetyGuidelines() {
        val viewModel = PeerPointViewModel(NeighborhoodRepository())

        // Less than 3 interests should fail
        val failUnderThreeInterests = viewModel.completeRegistration(
            name = "Test User",
            age = 22,
            neighborhood = "Campus Quad",
            category = ProfileCategory.BOTH,
            bio = "Bio",
            interests = listOf("Machine Learning", "Calculus"),
            preferredEnvironment = StudyEnvironment.SILENT_LIBRARY,
            hivStatus = HivStatusDeclaration.NEGATIVE,
            safetyGuidelinesVerified = true,
            availability = "Evenings",
            studyGoal = "Study"
        )
        assertFalse("Registration should fail if fewer than 3 interests are selected", failUnderThreeInterests)

        // Unverified safety guidelines should fail
        val failUnverifiedSafety = viewModel.completeRegistration(
            name = "Test User",
            age = 22,
            neighborhood = "Campus Quad",
            category = ProfileCategory.BOTH,
            bio = "Bio",
            interests = listOf("Machine Learning", "Calculus", "Physics"),
            preferredEnvironment = StudyEnvironment.SILENT_LIBRARY,
            hivStatus = HivStatusDeclaration.NEGATIVE,
            safetyGuidelinesVerified = false,
            availability = "Evenings",
            studyGoal = "Study"
        )
        assertFalse("Registration should fail if safety guidelines are not verified", failUnverifiedSafety)

        // Valid registration with >= 3 interests and safety guidelines verified should succeed
        val validRegistration = viewModel.completeRegistration(
            name = "Test User",
            age = 22,
            neighborhood = "Campus Quad",
            category = ProfileCategory.BOTH,
            bio = "Bio",
            interests = listOf("Machine Learning", "Calculus", "Physics"),
            preferredEnvironment = StudyEnvironment.SILENT_LIBRARY,
            hivStatus = HivStatusDeclaration.NEGATIVE,
            safetyGuidelinesVerified = true,
            availability = "Evenings",
            studyGoal = "Study"
        )
        assertTrue("Registration should succeed when requirements are met", validRegistration)
        assertTrue(viewModel.uiState.value.isRegistered)
    }

    @Test
    fun broadcastBeacon_notifiesNearbyMembers() {
        val repo = NeighborhoodRepository()
        val viewModel = PeerPointViewModel(repo)

        viewModel.completeRegistration(
            name = "Sender",
            age = 23,
            neighborhood = "Library Commons",
            category = ProfileCategory.STUDY_PARTNER,
            bio = "Bio",
            interests = listOf("Machine Learning", "Calculus", "Algorithms"),
            preferredEnvironment = StudyEnvironment.SILENT_LIBRARY,
            hivStatus = HivStatusDeclaration.UNDETECTABLE,
            safetyGuidelinesVerified = true,
            availability = "Now",
            studyGoal = "Exam prep"
        )

        val beforeCount = repo.communityAlerts.value.size
        viewModel.broadcastAlert(
            title = "Need Calculator",
            description = "Calc test in 30 mins",
            category = AlertCategory.RESOURCE_EQUIPMENT,
            urgency = AlertUrgency.HIGH_1HR,
            locationName = "Science Hall",
            radiusKm = 2.0
        )
        assertEquals(beforeCount + 1, repo.communityAlerts.value.size)
    }
}
