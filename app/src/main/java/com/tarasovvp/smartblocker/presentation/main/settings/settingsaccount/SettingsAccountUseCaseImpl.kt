package com.tarasovvp.smartblocker.presentation.main.settings.settingsaccount

import androidx.datastore.preferences.core.Preferences
import com.google.firebase.auth.AuthCredential
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import javax.inject.Inject

class SettingsAccountUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val realDataBaseRepository: RealDataBaseRepository,
        private val dataStoreRepository: DataStoreRepository,
    ) : SettingsAccountUseCase {
        override fun signOut(result: (Result<Unit>) -> Unit) =
            authRepository.signOut { authResult ->
                result.invoke(authResult)
            }

        override fun changePassword(
            currentPassword: String,
            newPassword: String,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.changePassword(currentPassword, newPassword) { authResult ->
            result.invoke(authResult)
        }

        override fun reAuthenticate(
            authCredential: AuthCredential,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.reAuthenticate(authCredential) { authResult ->
            result.invoke(authResult)
        }

        override fun deleteUser(result: (Result<Unit>) -> Unit) =
            realDataBaseRepository.deleteCurrentUser {
                authRepository.deleteUser(result)
            }

        override suspend fun clearDataByKeys(keys: List<Preferences.Key<*>>) = dataStoreRepository.clearDataByKeys(keys)
    }
