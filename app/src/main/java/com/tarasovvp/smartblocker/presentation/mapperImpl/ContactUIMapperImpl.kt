package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.presentation.mappers.ContactUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*

class ContactUIMapperImpl : ContactUIMapper {

    override fun mapToUIModel(from: Contact): ContactUIModel {
        return ContactUIModel(from.id, from.name.orEmpty(), from.photoUrl.orEmpty(),
            from.number.orEmpty(), from.phoneNumberValue.orEmpty(), from.isPhoneNumberValid)
    }

    override fun mapFromUIModel(to: ContactUIModel): Contact {
        return Contact(to.id, to.name, to.photoUrl,
            to.number, to.phoneNumberValue, to.isPhoneNumberValid)
    }

    override fun mapToUIModelList(fromList: List<Contact>): List<ContactUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<ContactUIModel>): List<Contact> {
        return toList.map { mapFromUIModel(it) }
    }
}