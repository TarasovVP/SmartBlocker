package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.data.repositoryImpl.AuthRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    @MockK
    private lateinit var smartBlockerApp: SmartBlockerApp

    @MockK
    private lateinit var googleSignInClient: GoogleSignInClient

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authRepository = AuthRepositoryImpl(smartBlockerApp)
        every { smartBlockerApp.checkNetworkUnAvailable() } returns false
        every { smartBlockerApp.firebaseAuth } returns mockk()
    }

    @Test
    fun sendPasswordResetEmailTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every { smartBlockerApp.firebaseAuth?.sendPasswordResetEmail(TEST_EMAIL) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        authRepository.sendPasswordResetEmail(TEST_EMAIL, resultMock)
        verify { resultMock.invoke() }
        verify { task.addOnCompleteListener(any()) }
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { smartBlockerApp.firebaseAuth?.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke() }
    }

    @Test
    fun signInWithGoogleTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { smartBlockerApp.firebaseAuth?.signInWithCredential(any()) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.signInWithGoogle(TEST_EMAIL, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke() }
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { smartBlockerApp.firebaseAuth?.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke() }
    }

    @Test
    fun changePasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { smartBlockerApp.firebaseAuth?.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke() }
    }

    @Test
    fun deleteUserTest() {
        val currentUser = mockk<FirebaseUser>(relaxed = true)
        val deleteTask = mockk<Task<Void>>(relaxed = true)
        every { smartBlockerApp.firebaseAuth?.currentUser } returns currentUser
        every { currentUser.delete() } returns deleteTask
        every { deleteTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(deleteTask)
            deleteTask
        }
        authRepository.deleteUser(googleSignInClient, resultMock)
        verify { deleteTask.addOnCompleteListener(any()) }
        verify { resultMock.invoke() }
    }

    @Test
    fun signOutTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every { googleSignInClient.signOut() } returns task
        every {  smartBlockerApp.firebaseAuth?.signOut() } returns Unit
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        authRepository.signOut(googleSignInClient, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { smartBlockerApp.firebaseAuth?.signOut() }
        verify { resultMock.invoke() }
    }
}