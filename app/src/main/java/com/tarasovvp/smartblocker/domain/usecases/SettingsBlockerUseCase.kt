package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import kotlinx.coroutines.flow.Flow

interface SettingsBlockerUseCase {

    suspend fun getBlockerTurnOff(): Flow<Boolean?>

    suspend fun setBlockerTurnOff(blockerTurnOff: Boolean)

    suspend fun getBlockHidden(): Flow<Boolean?>

    suspend fun setBlockHidden(blockHidden: Boolean)

    fun changeBlockHidden(blockHidden: Boolean, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit)

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>

    suspend fun setCurrentCountryCode(countryCode: CountryCode)
}