package com.tarasovvp.smartblocker.domain.mappers

import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.presentation.ui_models.*

class ContactWithFilterMapper {

    fun mapToUIModel(contactWithFilter: ContactWithFilter): ContactWithFilterUIModel {
        return ContactWithFilterUIModel(contactUIModel = contactWithFilter.contact?.run {
                ContactUIModel(id, name, photoUrl, number, filter) },
            filterWithCountryCodeUIModel =  FilterWithCountryCodeUIModel(
                filterUIModel = contactWithFilter.filterWithCountryCode?.filter?.run {
                    FilterUIModel(filter, conditionType, filterType, name, countryCode, country, filterWithoutCountryCode, created) },
                countryCodeUIModel = contactWithFilter.filterWithCountryCode?.countryCode?.run {
                    CountryCodeUIModel(country, countryCode, numberFormat) }
            )
        )
    }

    fun mapFromUIModel(contactWithFilterUIModel: ContactWithFilterUIModel): ContactWithFilter {
        return ContactWithFilter(contact = contactWithFilterUIModel.contactUIModel?.run {
            Contact(id, name, photoUrl, number, filter)
        },
            filterWithCountryCode = FilterWithCountryCode(
                filter = contactWithFilterUIModel.filterWithCountryCodeUIModel?.filterUIModel?.run {
                    Filter(filter, conditionType, filterType, name, countryCode, country, filterWithoutCountryCode, created) },
                countryCode = contactWithFilterUIModel.filterWithCountryCodeUIModel?.countryCodeUIModel?.run {
                    CountryCode(country = country, countryCode, numberFormat) }
            )
        )
    }
}