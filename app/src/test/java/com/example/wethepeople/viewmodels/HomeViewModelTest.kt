package com.example.wethepeople.viewmodels

import app.cash.turbine.test
import com.example.wethepeople.models.Election
import com.example.wethepeople.models.UserActivity
import com.example.wethepeople.models.UserModels.UserProfile
import com.example.wethepeople.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @Mock
    private lateinit var auth: FirebaseAuth
    
    @Mock
    private lateinit var firestore: FirebaseFirestore
    
    @Mock
    private lateinit var firebaseUser: FirebaseUser
    
    @Mock
    private lateinit var userDocumentReference: DocumentReference
    
    @Mock
    private lateinit var userDocumentSnapshot: DocumentSnapshot
    
    @Mock
    private lateinit var electionsCollectionReference: CollectionReference
    
    @Mock
    private lateinit var activitiesCollectionReference: CollectionReference
    
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn("test-user-id")
        viewModel = HomeViewModel(auth, firestore, testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `when initialized with logged in user, should load initial data`() = runTest {
        // Given
        val userProfile = mockUserProfile()
        val elections = mockElections()
        val activities = mockActivities()
        setupMocksForSuccessfulDataLoad(userProfile, elections, activities)
        
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.contentState).isInstanceOf(HomeContentState.Success::class.java)
            val successState = state.contentState as HomeContentState.Success
            assertThat(successState.userStats.username).isEqualTo(userProfile.displayName)
            assertThat(successState.activeElections).hasSize(elections.size)
            assertThat(successState.recentActivities).hasSize(activities.size)
        }
    }
    
    @Test
    fun `when refresh event is submitted, should reload data`() = runTest {
        // Given
        val userProfile = mockUserProfile()
        val elections = mockElections()
        val activities = mockActivities()
        setupMocksForSuccessfulDataLoad(userProfile, elections, activities)
        
        // When
        viewModel.submitEvent(HomeEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.contentState).isInstanceOf(HomeContentState.Success::class.java)
            verify(firestore, times(2)).collection("users")
        }
    }
    
    @Test
    fun `when data loading fails, should show error state`() = runTest {
        // Given
        val errorMessage = "Failed to load data"
        whenever(firestore.collection("users")).thenThrow(Exception(errorMessage))
        
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.contentState).isInstanceOf(HomeContentState.Error::class.java)
            assertThat((state.contentState as HomeContentState.Error).message).isEqualTo(errorMessage)
        }
    }
    
    @Test
    fun `when dismissing achievement, should update pending achievements`() = runTest {
        // Given
        val userProfile = mockUserProfile()
        val elections = mockElections()
        val activities = mockActivities()
        setupMocksForSuccessfulDataLoad(userProfile, elections, activities)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.submitEvent(HomeEvent.DismissAchievement("test-achievement"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.contentState).isInstanceOf(HomeContentState.Success::class.java)
            val successState = state.contentState as HomeContentState.Success
            assertThat(successState.pendingAchievements).isEmpty()
        }
    }
    
    private fun mockUserProfile() = UserProfile.create(
        id = "test-user-id",
        name = "Test User",
        email = "test@example.com"
    )
    
    private fun mockElections() = listOf(
        Election(
            id = "election1",
            title = "Test Election 1",
            endDate = System.currentTimeMillis() + 86400000,
            participantCount = 100
        ),
        Election(
            id = "election2",
            title = "Test Election 2",
            endDate = System.currentTimeMillis() + 172800000,
            participantCount = 200
        )
    )
    
    private fun mockActivities() = listOf(
        UserActivity(
            id = "activity1",
            type = "vote",
            title = "Voted in election",
            description = "You voted in Test Election 1",
            timestamp = System.currentTimeMillis()
        ),
        UserActivity(
            id = "activity2",
            type = "debate",
            title = "Participated in debate",
            description = "You debated on topic XYZ",
            timestamp = System.currentTimeMillis() - 3600000
        )
    )
    
    private fun setupMocksForSuccessfulDataLoad(
        userProfile: UserProfile,
        elections: List<Election>,
        activities: List<UserActivity>
    ) {
        // Mock user profile
        whenever(firestore.collection("users")).thenReturn(mock())
        whenever(firestore.collection("users").document(any())).thenReturn(userDocumentReference)
        whenever(userDocumentReference.get()).thenReturn(mock<Task<DocumentSnapshot>>().apply {
            whenever(isSuccessful).thenReturn(true)
            whenever(result).thenReturn(userDocumentSnapshot)
        })
        whenever(userDocumentSnapshot.toObject(UserProfile::class.java)).thenReturn(userProfile)
        
        // Mock elections
        whenever(firestore.collection("elections")).thenReturn(electionsCollectionReference)
        whenever(electionsCollectionReference.whereEqualTo(any<String>(), any()))
            .thenReturn(electionsCollectionReference)
        whenever(electionsCollectionReference.orderBy(any<String>(), any()))
            .thenReturn(electionsCollectionReference)
        whenever(electionsCollectionReference.get()).thenReturn(mock<Task<QuerySnapshot>>().apply {
            whenever(isSuccessful).thenReturn(true)
            whenever(result).thenReturn(mock<QuerySnapshot>().apply {
                whenever(documents).thenReturn(elections.map { election ->
                    mock<DocumentSnapshot>().apply {
                        whenever(id).thenReturn(election.id)
                        whenever(toObject(Election::class.java)).thenReturn(election)
                    }
                })
            })
        })
        
        // Mock activities
        whenever(userDocumentReference.collection("activities")).thenReturn(activitiesCollectionReference)
        whenever(activitiesCollectionReference.orderBy(any<String>(), any()))
            .thenReturn(activitiesCollectionReference)
        whenever(activitiesCollectionReference.limit(any())).thenReturn(activitiesCollectionReference)
        whenever(activitiesCollectionReference.get()).thenReturn(mock<Task<QuerySnapshot>>().apply {
            whenever(isSuccessful).thenReturn(true)
            whenever(result).thenReturn(mock<QuerySnapshot>().apply {
                whenever(documents).thenReturn(activities.map { activity ->
                    mock<DocumentSnapshot>().apply {
                        whenever(id).thenReturn(activity.id)
                        whenever(toObject(UserActivity::class.java)).thenReturn(activity)
                    }
                })
            })
        })
    }
} 