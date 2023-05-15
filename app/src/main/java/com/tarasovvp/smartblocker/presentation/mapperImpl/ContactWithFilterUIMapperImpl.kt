package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.isTrue

class ContactWithFilterUIMapperImpl(private val filterUIMapper: FilterUIMapper) : ContactWithFilterUIMapper {

    override fun mapToUIModel(from: ContactWithFilter): ContactWithFilterUIModel {
        return ContactWithFilterUIModel(from.contact?.contactId.orEmpty(), from.contact?.name.orEmpty(), from.contact?.photoUrl.orEmpty(),
            from.contact?.number.orEmpty(), from.contact?.phoneNumberValue.orEmpty(), from.contact?.isPhoneNumberValid.isTrue(),
            filterUIModel = from.filterWithCountryCode?.filter?.let { filterUIMapper.mapToUIModel(it) })
    }

    override fun mapFromUIModel(to: ContactWithFilterUIModel): ContactWithFilter {
        return ContactWithFilter(contact = Contact(to.contactId, to.contactName, to.photoUrl,
            to.number, to.phoneNumberValue, to.isPhoneNumberValid),
            filterWithCountryCode = FilterWithCountryCode(filter = to.filterUIModel?.let { filterUIMapper.mapFromUIModel(it) }))
    }

    override fun mapToUIModelList(fromList: List<ContactWithFilter>): List<ContactWithFilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<ContactWithFilterUIModel>): List<ContactWithFilter> {
        return toList.map { mapFromUIModel(it) }
    }
}