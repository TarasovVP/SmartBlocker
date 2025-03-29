package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface SignUpUseCase {
    fun fetchSignInMethodsForEmail(
        email: String,
        result: (Result<List<String>>) -> Unit,
    )

    fun createUserWithGoogle(
        idToken: String,
        result: (Result<Unit>) -> Unit,
    )

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        result: (Result<String>) -> Unit,
    )

    fun createCurrentUser(
        currentUser: CurrentUser,
        result: (Result<Unit>) -> Unit,
    )
}
