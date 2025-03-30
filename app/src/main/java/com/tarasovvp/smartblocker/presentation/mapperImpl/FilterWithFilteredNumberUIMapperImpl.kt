package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.orZero

class FilterWithFilteredNumberUIMapperImpl : FilterWithFilteredNumberUIMapper {
    override fun mapToUIModel(from: FilterWithFilteredNumber): FilterWithFilteredNumberUIModel {
        return FilterWithFilteredNumberUIModel(
            from.filter?.filter.orEmpty(),
            from.filter?.conditionType.orZero(),
            from.filter?.filterType.orZero(),
            from.filter?.countryCode.orEmpty(),
            from.filter?.country.orEmpty(),
            from.filter?.created ?: 0,
            from.filteredContacts ?: 0,
        )
    }

    override fun mapFromUIModel(to: FilterWithFilteredNumberUIModel): FilterWithFilteredNumber {
        return FilterWithFilteredNumber(
            filter =
                Filter(
                    to.filter,
                    to.conditionType,
                    to.filterType,
                    to.countryCode,
                    to.country,
                    to.created,
                ),
            filteredContacts = to.filteredContacts,
        )
    }

    override fun mapToUIModelList(fromList: List<FilterWithFilteredNumber>): List<FilterWithFilteredNumberUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<FilterWithFilteredNumberUIModel>): List<FilterWithFilteredNumber> {
        return toList.map { mapFromUIModel(it) }
    }
}
