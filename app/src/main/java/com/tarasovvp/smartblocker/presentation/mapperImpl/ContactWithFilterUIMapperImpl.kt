package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.presentation.mappers.ContactUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*

class ContactWithFilterUIMapperImpl(private val contactUIMapper: ContactUIMapper, private val filterUIMapper: FilterUIMapper) : ContactWithFilterUIMapper {

    override fun mapToUIModel(from: ContactWithFilter): ContactWithFilterUIModel {
        return ContactWithFilterUIModel(contactUIModel = from.contact?.let { contactUIMapper.mapToUIModel(it) },
            filterUIModel = from.filterWithCountryCode?.filter?.let { filterUIMapper.mapToUIModel(it) })
    }

    override fun mapFromUIModel(to: ContactWithFilterUIModel): ContactWithFilter {
        return ContactWithFilter(contact = to.contactUIModel?.let { contactUIMapper.mapFromUIModel(it) },
            filterWithCountryCode = FilterWithCountryCode(filter = to.filterUIModel?.let { filterUIMapper.mapFromUIModel(it) }))
    }

    override fun mapToUIModelList(fromList: List<ContactWithFilter>): List<ContactWithFilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<ContactWithFilterUIModel>): List<ContactWithFilter> {
        return toList.map { mapFromUIModel(it) }
    }
}