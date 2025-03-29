package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class CountryCodeUIMapperImpl(private val dataStoreRepository: DataStoreRepository) :
    CountryCodeUIMapper {
    override fun mapToUIModel(from: CountryCode): CountryCodeUIModel {
        val appLang =
            runBlocking {
                dataStoreRepository.getAppLang().first()
            }.orEmpty()
        return CountryCodeUIModel(
            from.country,
            from.countryCode.orEmpty(),
            from.numberFormat.orEmpty(),
            Locale(appLang, from.country).displayCountry,
        )
    }

    override fun mapFromUIModel(to: CountryCodeUIModel): CountryCode {
        return CountryCode(to.country, to.countryCode, to.numberFormat)
    }

    override fun mapToUIModelList(fromList: List<CountryCode>): List<CountryCodeUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<CountryCodeUIModel>): List<CountryCode> {
        return toList.map { mapFromUIModel(it) }
    }
}
