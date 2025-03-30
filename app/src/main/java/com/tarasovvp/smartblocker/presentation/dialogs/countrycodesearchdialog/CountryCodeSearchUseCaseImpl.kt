package com.tarasovvp.smartblocker.presentation.dialogs.countrycodesearchdialog

import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CountryCodeSearchUseCaseImpl
    @Inject
    constructor(
        private val countryCodeRepository: CountryCodeRepository,
        private val dataStoreRepository: DataStoreRepository,
    ) : CountryCodeSearchUseCase {
        override suspend fun getAppLanguage(): Flow<String?> {
            return dataStoreRepository.getAppLang()
        }

        override suspend fun getCountryCodeList(): List<CountryCode> {
            return countryCodeRepository.allCountryCodes()
        }
    }
