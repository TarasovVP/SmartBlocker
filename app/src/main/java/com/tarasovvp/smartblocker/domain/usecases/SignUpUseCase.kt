package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface SignUpUseCase {

    fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<Unit>) -> Unit)
}