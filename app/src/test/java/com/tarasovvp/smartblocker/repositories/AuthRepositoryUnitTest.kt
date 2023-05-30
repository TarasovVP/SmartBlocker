package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.data.repositoryImpl.AuthRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class AuthRepositoryUnitTest {

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK
    private lateinit var googleSignInClient: GoogleSignInClient

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authRepository = AuthRepositoryImpl(firebaseAuth, googleSignInClient)
    }

    @Test
    fun sendPasswordResetEmailTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every { firebaseAuth.sendPasswordResetEmail(TEST_EMAIL) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        authRepository.sendPasswordResetEmail(TEST_EMAIL, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInWithGoogleTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.signInWithCredential(any()) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.signInWithGoogle(TEST_EMAIL, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun changePasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun deleteUserTest() {
        val currentUser = mockk<FirebaseUser>(relaxed = true)
        val deleteTask = mockk<Task<Void>>(relaxed = true)
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.delete() } returns deleteTask
        every { deleteTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(deleteTask)
            deleteTask
        }
        authRepository.deleteUser(resultMock)
        verify { deleteTask.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signOutTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every { googleSignInClient.signOut() } returns task
        every { firebaseAuth.signOut() } returns Unit
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        authRepository.signOut(resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { firebaseAuth.signOut() }
        verify { resultMock.invoke(Result.Success()) }
    }
}