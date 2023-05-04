package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface SignUpUseCase {

    fun createUserWithEmailAndPassword(email: String, password: String, result: (OperationResult<String?>) -> Unit)
}