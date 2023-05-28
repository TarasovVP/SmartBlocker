package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.orZero

class FilterWithFilteredNumberUIMapperImpl : FilterWithFilteredNumberUIMapper {

    override fun mapToUIModel(from: FilterWithFilteredNumber): FilterWithFilteredNumberUIModel {
        return FilterWithFilteredNumberUIModel(from.filter?.filter.orEmpty(), from.filter?.conditionType.orZero(), from.filter?.filterType.orZero(),
            from.filter?.filterName.orEmpty(), from.filter?.countryCode.orEmpty(), from.filter?.country.orEmpty(), from.filter?.created ?: 0,
            from.filteredContacts ?: 0, from.filteredCalls ?: 0)
    }

    override fun mapFromUIModel(to: FilterWithFilteredNumberUIModel): FilterWithFilteredNumber {
        return FilterWithFilteredNumber(filter = Filter(to.filter, to.conditionType, to.filterType,
            to.filterName, to.countryCode, to.country, to.created),
            filteredContacts = to.filteredContacts, filteredCalls = to.filteredCalls)
    }

    override fun mapToUIModelList(fromList: List<FilterWithFilteredNumber>): List<FilterWithFilteredNumberUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<FilterWithFilteredNumberUIModel>): List<FilterWithFilteredNumber> {
        return toList.map { mapFromUIModel(it) }
    }
}