package com.example.blacklister.extensions

import android.content.Context
import android.provider.ContactsContract
import com.example.blacklister.model.Contact
import java.util.*

fun Context.contactList(): ArrayList<Contact> {
    val projection = arrayOf(
        ContactsContract.Data.MIMETYPE,
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.PHOTO_URI,
        ContactsContract.CommonDataKinds.Contactables.DATA

    )
    val selection = "${ContactsContract.Data.MIMETYPE} in (?, ?)"

    val selectionArgs = arrayOf(
        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
    )

    val contacts = this
        .contentResolver
        .query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null)
        .run {
            if (this == null) {
                throw IllegalStateException("Cursor null")
            }
            val contactsById = mutableMapOf<String, Contact>()
            val mimeTypeField = getColumnIndex(ContactsContract.Data.MIMETYPE)
            val idField = getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nameField = getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoUri = getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            val dataField = getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)

            while (moveToNext()) {
                when (getString(mimeTypeField)) {
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                        val data = getString(dataField)
                        if (data.length > 9) {
                            val id = getString(idField)
                            val photoUrl = getString(photoUri)
                            val name = getString(nameField)
                            contactsById[data] =
                                Contact(id = id, name = name, photoUrl = photoUrl, phone = data)
                        }
                    }
                }
            }
            close()
            contactsById.values.toList().sortedWith(compareBy { it.name })
        }
    return ArrayList<Contact>(contacts.toList())
}