package com.example.wethepeople.viewmodels

import app.cash.turbine.test
import com.example.wethepeople.models.UserModels.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {
    @Mock
    private lateinit var auth: FirebaseAuth
    
    @Mock
    private lateinit var firestore: FirebaseFirestore
    
    @Mock
    private lateinit var firebaseUser: FirebaseUser
    
    @Mock
    private lateinit var documentReference: DocumentReference
    
    @Mock
    private lateinit var documentSnapshot: DocumentSnapshot
    
    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(auth, firestore)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `when user is not logged in, authState should show unauthenticated`() = runTest {
        // Given
        whenever(auth.currentUser).thenReturn(null)
        
        // When
        val initialState = viewModel.authState.first()
        
        // Then
        assertThat(initialState.isAuthenticated).isFalse()
        assertThat(initialState.userProfile).isNull()
    }
    
    @Test
    fun `when user signs in successfully, authState should update accordingly`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val authResult: AuthResult = mock()
        val userProfile = UserProfile.create(
            id = "test-id",
            name = "Test User",
            email = email
        )
        
        whenever(auth.signInWithEmailAndPassword(email, password))
            .thenReturn(mock<Task<AuthResult>>().apply {
                whenever(isSuccessful).thenReturn(true)
                whenever(result).thenReturn(authResult)
            })
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn("test-id")
        whenever(firestore.collection("users")).thenReturn(mock())
        whenever(firestore.collection("users").document(any())).thenReturn(documentReference)
        whenever(documentReference.get()).thenReturn(mock<Task<DocumentSnapshot>>().apply {
            whenever(isSuccessful).thenReturn(true)
            whenever(result).thenReturn(documentSnapshot)
        })
        whenever(documentSnapshot.toObject(UserProfile::class.java)).thenReturn(userProfile)
        
        // When
        viewModel.submitEvent(AuthEvent.SignIn(email, password))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertThat(state.isAuthenticated).isTrue()
            assertThat(state.userProfile).isEqualTo(userProfile)
            assertThat(state.error).isNull()
        }
    }
    
    @Test
    fun `when sign in fails, authState should contain error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Invalid credentials"
        
        whenever(auth.signInWithEmailAndPassword(email, password))
            .thenReturn(mock<Task<AuthResult>>().apply {
                whenever(isSuccessful).thenReturn(false)
                whenever(exception).thenReturn(Exception(errorMessage))
            })
            
        // When
        viewModel.submitEvent(AuthEvent.SignIn(email, password))
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertThat(state.isAuthenticated).isFalse()
            assertThat(state.error).isEqualTo(errorMessage)
        }
    }
    
    @Test
    fun `when user signs out, authState should be reset`() = runTest {
        // Given
        val initialProfile = UserProfile.create(
            id = "test-id",
            name = "Test User",
            email = "test@example.com"
        )
        viewModel = AuthViewModel(auth, firestore)
        
        // When
        viewModel.submitEvent(AuthEvent.SignOut)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.authState.test {
            val state = awaitItem()
            assertThat(state.isAuthenticated).isFalse()
            assertThat(state.userProfile).isNull()
            assertThat(state.error).isNull()
        }
        verify(auth).signOut()
    }
} 