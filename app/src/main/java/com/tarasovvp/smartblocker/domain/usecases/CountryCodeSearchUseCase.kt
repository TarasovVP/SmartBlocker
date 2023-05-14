package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import kotlinx.coroutines.flow.Flow

interface CountryCodeSearchUseCase  {

    suspend fun getAppLanguage(): Flow<String?>

    suspend fun getCountryCodeList(): List<CountryCode>
}