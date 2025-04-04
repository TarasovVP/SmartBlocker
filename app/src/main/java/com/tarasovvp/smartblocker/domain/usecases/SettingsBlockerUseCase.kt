package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import kotlinx.coroutines.flow.Flow

interface SettingsBlockerUseCase {
    suspend fun getBlockerTurnOn(): Flow<Boolean?>

    suspend fun changeBlockTurnOn(
        blockTurnOn: Boolean,
        isNetworkAvailable: Boolean,
        result: (Result<Unit>) -> Unit,
    )

    suspend fun getBlockHidden(): Flow<Boolean?>

    suspend fun changeBlockHidden(
        blockHidden: Boolean,
        isNetworkAvailable: Boolean,
        result: (Result<Unit>) -> Unit,
    )

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>

    suspend fun changeCountryCode(
        countryCode: CountryCode,
        isNetworkAvailable: Boolean,
        result: (Result<Unit>) -> Unit,
    )
}
