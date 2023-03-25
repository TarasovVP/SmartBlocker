package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class SignUpUseCaseTest @Inject constructor(private val authRepository: AuthRepository) {

    fun createUserWithEmailAndPassword(email: String, password: String, result: () -> Unit, ) = authRepository.createUserWithEmailAndPassword(email, password) {
        result.invoke()
    }
}