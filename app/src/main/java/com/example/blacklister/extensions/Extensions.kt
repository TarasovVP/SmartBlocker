package com.example.blacklister.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.blacklister.R
import com.example.blacklister.databinding.DialogInfoBinding
import com.example.blacklister.model.Contact
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import android.provider.CallLog
import kotlin.collections.ArrayList


private const val PHONE_NUMBER_CODE = "+380"
private const val PHONE_NUMBER_CODE_ = "380"

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
                                Contact(id = id, name = name, photoUrl = photoUrl, phone = data.formattedPhoneNumber())
                        }
                    }
                }
            }
            close()
            contactsById.values.toList().sortedWith(compareBy { it.name })
        }
    return ArrayList(contacts.toList())
}

fun Context.callLogList(): ArrayList<com.example.blacklister.model.CallLog> {
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE
    )

    val callLogList = ArrayList<com.example.blacklister.model.CallLog>()
    val cursor: Cursor? = this.contentResolver.query(Uri.parse("content://call_log/calls"), projection, null, null, null)
    while (cursor?.moveToNext() == true) {
        val name: String = cursor.getString(0)
        val phone: String = cursor.getString(1)
        val type: String =
            cursor.getString(2)
        val time: String =
            cursor.getString(3)
        callLogList.add(com.example.blacklister.model.CallLog(name = name, phone = phone, type = type, time = time))
    }
    cursor?.close()
    return callLogList
}

fun ImageView.loadCircleImage(photoUrl: String?) {
    if (photoUrl.isNullOrEmpty()) {
        this.setImageResource(R.drawable.ic_avatar)
    } else {
        Glide.with(this.context)
            .load(photoUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
            .placeholder(R.drawable.ic_avatar)
            .error(R.drawable.ic_avatar)
            .circleCrop()
            .into(this)
    }
}

fun Context.isPermissionAccepted(permission: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.breakCallNougatAndLower() {
    val telephony = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    try {
        val c = Class.forName(telephony.javaClass.name)
        val m = c.getDeclaredMethod("getITelephony")
        m.isAccessible = true
        val telephonyService: ITelephony = m.invoke(telephony) as ITelephony
        telephonyService.endCall()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.breakCallPieAndHigher() {
    val telecomManager = this.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    try {
        telecomManager.javaClass.getMethod("endCall").invoke(telecomManager)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun String.formattedPhoneNumber(): String {
    var phone = Regex("[^0-9]").replace(this, "")
    if (phone.isEmpty() || phone.length < 10) return ""
    phone = when {
        phone.startsWith(PHONE_NUMBER_CODE_) && phone.length > 3 -> {
            phone.substring(3)
        }
        else -> {
            phone
        }
    }
    return String.format("%s%s", PHONE_NUMBER_CODE, phone)
}