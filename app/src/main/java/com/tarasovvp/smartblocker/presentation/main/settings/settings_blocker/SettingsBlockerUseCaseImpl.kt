package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SettingsBlockerUseCaseImpl @Inject constructor(
    private val realDataBaseRepository: RealDataBaseRepository,
    private val firebaseAuth: FirebaseAuth
): SettingsBlockerUseCase {

    override fun changeBlockHidden(blockHidden: Boolean, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.changeBlockHidden(blockHidden) {
                    result.invoke(Result.Success())
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            result.invoke(Result.Success())
        }
    }
}