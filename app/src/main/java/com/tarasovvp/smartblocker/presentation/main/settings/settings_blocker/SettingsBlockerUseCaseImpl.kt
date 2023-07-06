package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsBlockerUseCaseImpl @Inject constructor(
    private val realDataBaseRepository: RealDataBaseRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val firebaseAuth: FirebaseAuth
): SettingsBlockerUseCase {

    override suspend fun getBlockerTurnOn(): Flow<Boolean?> {
        return dataStoreRepository.blockerTurnOn()
    }

    override suspend fun setBlockerTurnOn(blockerTurnOff: Boolean) {
        dataStoreRepository.setBlockerTurnOn(blockerTurnOff)
    }

    override suspend fun getBlockHidden(): Flow<Boolean?> {
        return dataStoreRepository.blockHidden()
    }

    override suspend fun setBlockHidden(blockHidden: Boolean) {
        dataStoreRepository.setBlockHidden(blockHidden)
    }

    override fun changeBlockHidden(blockHidden: Boolean, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.isAuthorisedUser()) {
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

    override suspend fun getCurrentCountryCode(): Flow<CountryCode?> {
        return dataStoreRepository.getCountryCode()
    }

    override suspend fun setCurrentCountryCode(countryCode: CountryCode) {
        dataStoreRepository.setCountryCode(countryCode)
    }
}