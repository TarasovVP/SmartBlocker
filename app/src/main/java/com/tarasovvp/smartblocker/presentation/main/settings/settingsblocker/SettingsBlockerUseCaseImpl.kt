package com.tarasovvp.smartblocker.presentation.main.settings.settingsblocker

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SettingsBlockerUseCaseImpl
    @Inject
    constructor(
        private val realDataBaseRepository: RealDataBaseRepository,
        private val dataStoreRepository: DataStoreRepository,
        private val firebaseAuth: FirebaseAuth,
    ) : SettingsBlockerUseCase {
        override suspend fun getBlockerTurnOn(): Flow<Boolean?> {
            return dataStoreRepository.blockerTurnOn()
        }

        override suspend fun changeBlockHidden(
            blockHidden: Boolean,
            isNetworkAvailable: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            if (firebaseAuth.isAuthorisedUser()) {
                if (isNetworkAvailable) {
                    realDataBaseRepository.changeBlockHidden(blockHidden) {
                        runBlocking {
                            dataStoreRepository.setBlockHidden(blockHidden)
                        }
                        result.invoke(Result.Success())
                    }
                } else {
                    result.invoke(Result.Failure())
                }
            } else {
                dataStoreRepository.setBlockHidden(blockHidden)
                result.invoke(Result.Success())
            }
        }

        override suspend fun getBlockHidden(): Flow<Boolean?> {
            return dataStoreRepository.blockHidden()
        }

        override suspend fun changeBlockTurnOn(
            blockTurnOn: Boolean,
            isNetworkAvailable: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            if (firebaseAuth.isAuthorisedUser()) {
                if (isNetworkAvailable) {
                    realDataBaseRepository.changeBlockTurnOn(blockTurnOn) {
                        runBlocking {
                            dataStoreRepository.setBlockerTurnOn(blockTurnOn)
                        }
                        result.invoke(Result.Success())
                    }
                } else {
                    dataStoreRepository.setBlockerTurnOn(blockTurnOn)
                    result.invoke(Result.Failure())
                }
            } else {
                result.invoke(Result.Success())
            }
        }

        override suspend fun getCurrentCountryCode(): Flow<CountryCode?> {
            return dataStoreRepository.getCountryCode()
        }

        override suspend fun changeCountryCode(
            countryCode: CountryCode,
            isNetworkAvailable: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            if (firebaseAuth.isAuthorisedUser()) {
                if (isNetworkAvailable) {
                    realDataBaseRepository.changeCountryCode(countryCode) {
                        runBlocking {
                            dataStoreRepository.setCountryCode(countryCode)
                        }
                        result.invoke(Result.Success())
                    }
                } else {
                    dataStoreRepository.setCountryCode(countryCode)
                    result.invoke(Result.Failure())
                }
            } else {
                result.invoke(Result.Success())
            }
        }
    }
