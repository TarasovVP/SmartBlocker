package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.mappers.ContactUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*

class ContactUIMapperImpl : ContactUIMapper {

    override fun mapToUIModel(from: Contact): ContactUIModel {
        return ContactUIModel(from.id, from.name,
            from.photoUrl, from.number, from.phoneNumberValue, from.isPhoneNumberValid)
    }

    override fun mapFromUIModel(to: ContactUIModel): Contact {
        return Contact(to.id.orEmpty(), to.name,
            to.photoUrl, to.number, to.phoneNumberValue, to.isPhoneNumberValid)
    }

    override fun mapToUIModelList(fromList: List<Contact>): List<ContactUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<ContactUIModel>): List<Contact> {
        return toList.map { mapFromUIModel(it) }
    }
}