package com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up

interface SignUpUseCase {

    fun createUserWithEmailAndPassword(email: String, password: String, result: (String?) -> Unit)
}