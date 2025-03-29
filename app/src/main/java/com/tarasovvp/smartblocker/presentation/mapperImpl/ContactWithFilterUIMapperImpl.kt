package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue

class ContactWithFilterUIMapperImpl(private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper) :
    ContactWithFilterUIMapper {
    override fun mapToUIModel(from: ContactWithFilter): ContactWithFilterUIModel {
        return ContactWithFilterUIModel(
            from.contact?.contactId.orEmpty(),
            from.contact?.name.orEmpty(),
            from.contact?.photoUrl.orEmpty(),
            from.contact?.number.orEmpty(),
            from.contact?.digitsTrimmedNumber,
            from.contact?.phoneNumberValue.orEmpty(),
            from.contact?.isPhoneNumberValid.isTrue(),
            filterWithFilteredNumberUIModel =
                from.filterWithFilteredNumber?.let {
                    filterWithFilteredNumberUIMapper.mapToUIModel(
                        it,
                    )
                } ?: FilterWithFilteredNumberUIModel(),
        )
    }

    override fun mapFromUIModel(to: ContactWithFilterUIModel): ContactWithFilter {
        return ContactWithFilter(
            contact =
                Contact(
                    to.contactId,
                    to.contactName,
                    to.photoUrl,
                    to.number,
                    to.digitsTrimmedNumber,
                    to.phoneNumberValue,
                    to.isPhoneNumberValid,
                ),
            filterWithFilteredNumber =
                FilterWithFilteredNumber(
                    filter = filterWithFilteredNumberUIMapper.mapFromUIModel(to.filterWithFilteredNumberUIModel).filter,
                    filteredContacts = to.filterWithFilteredNumberUIModel.filteredContacts,
                ),
        )
    }

    override fun mapToUIModelList(fromList: List<ContactWithFilter>): List<ContactWithFilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<ContactWithFilterUIModel>): List<ContactWithFilter> {
        return toList.map { mapFromUIModel(it) }
    }
}
