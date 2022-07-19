package com.tarasovvp.blacklister.extensions

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.CALL_LOG_CALL
import com.tarasovvp.blacklister.constants.Constants.DATE
import com.tarasovvp.blacklister.constants.Constants.DESC
import com.tarasovvp.blacklister.constants.Constants.EIGHT_ZERO
import com.tarasovvp.blacklister.constants.Constants.END_CALL
import com.tarasovvp.blacklister.constants.Constants.GET_IT_TELEPHONY
import com.tarasovvp.blacklister.constants.Constants.NUMBER
import com.tarasovvp.blacklister.constants.Constants.PHONE_NUMBER_CODE
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.constants.Constants.THREE_EIGHT_ZERO
import com.tarasovvp.blacklister.constants.Constants.TYPE
import com.tarasovvp.blacklister.constants.Constants.ZERO
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.MainActivity
import java.text.SimpleDateFormat
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
                                Contact(
                                    id = id,
                                    name = name,
                                    photoUrl = photoUrl,
                                    phone = data.toFormattedPhoneNumber()
                                )
                        }
                    }
                }
            }
            close()
            contactsById.values.toList().sortedWith(compareBy { it.name })
        }
    return ArrayList(contacts.toList())
}

fun Context.callLogList(): ArrayList<com.tarasovvp.blacklister.model.CallLog> {
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE
    )

    val callLogList = ArrayList<com.tarasovvp.blacklister.model.CallLog>()
    val cursor: Cursor? = this.contentResolver.query(
        Uri.parse(CALL_LOG_CALL),
        projection,
        null,
        null,
        null
    )
    cursor?.use { callLogCursor ->
        while (callLogCursor.moveToNext()) {
            val time: String? =
                callLogCursor.getString(3)
            time?.let {
                com.tarasovvp.blacklister.model.CallLog(
                    name = callLogCursor.getString(0),
                    phone = callLogCursor.getString(1),
                    type = callLogCursor.getString(2),
                    time = it
                )
            }?.let { callLog ->
                callLogList.add(callLog)
            }
        }
    }
    callLogList.sortByDescending {
        it.time?.toMillisecondsFromString()
    }
    return callLogList
}

fun Context.deleteLastMissedCall(phone: String): Boolean {
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE
    )

    val cursor: Cursor? = this.contentResolver.query(
        Uri.parse(CALL_LOG_CALL),
        projection,
        null,
        null,
        "${CallLog.Calls.DATE} $DESC"
    )
    cursor?.use {
        while (cursor.moveToNext()) {
            val phoneNumber: String = cursor.getString(1)
            val time: String? =
                cursor.getString(3)
            val queryString = "${NUMBER}'$phone' AND ${DATE}'$time' AND ${TYPE}'$REJECTED_CALL'"
            if (phone == phoneNumber.toFormattedPhoneNumber()) {
                try {
                    BlackListerApp.instance?.database?.blockedCallDao()
                        ?.insertBlockedCall(time?.let {
                            BlockedCall(
                                name = cursor.getString(0),
                                phone = phoneNumber,
                                time = it
                            )
                        })
                    this.contentResolver.delete(Uri.parse(CALL_LOG_CALL), queryString, null)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    return false
                }
                return true
            }
        }
    }
    return false
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

fun Context.breakCallNougatAndLower() {
    val telephony = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    try {
        val c = Class.forName(telephony.javaClass.name)
        val m = c.getDeclaredMethod(GET_IT_TELEPHONY)
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
        telecomManager.javaClass.getMethod(END_CALL).invoke(telecomManager)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun String.toFormattedPhoneNumber(): String {
    var phone = Regex("[^0-9]").replace(this, "")
    if (phone.isEmpty() || phone.length < 10) return ""
    phone = when {
        phone.startsWith(ZERO) && phone.length > 1 -> {
            phone.substring(1)
        }
        phone.startsWith(EIGHT_ZERO) && phone.length > 2 -> {
            phone.substring(2)
        }
        phone.startsWith(THREE_EIGHT_ZERO) && phone.length > 3 -> {
            phone.substring(3)
        }
        else -> {
            phone
        }
    }
    return String.format("%s%s", PHONE_NUMBER_CODE, phone)
}

fun String.toDateFromMilliseconds(dateFormat: String): String {
    val millis = this.toMillisecondsFromString()
    val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    return if (millis <= 0) "" else dateFormatter.format(Date(millis))
}

fun String.toMillisecondsFromString(): Long {
    return try {
        this.toLong()
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun Activity.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    manager?.getRunningServices(Int.MAX_VALUE)?.forEach {
        if (serviceClass.name == it.service.className) {
            return true
        }
    }
    return false
}

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val chan = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL,
            Constants.FOREGROUND_CALL_SERVICE, NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
    }
}

fun Context.notificationBuilder(): NotificationCompat.Builder {
    val notificationIntent = Intent(this, MainActivity::class.java)

    val pendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)

    builder.setSmallIcon(R.drawable.ic_logo)
        .setColor(ContextCompat.getColor(this, R.color.blue))
        .setContentTitle(getString(R.string.blacklister_is_on))
        .setContentText("")
        .setContentIntent(pendingIntent)

    return builder
}

fun <T> List<T>.toHashMapFromList(): LinkedHashMap<String, List<T>> {
    val hashMapFromList = LinkedHashMap<String, List<T>>()
    val keyList = ArrayList<Any>(this.map {
        when (it) {
            is BlackNumber -> {
                it.number.substring(0, 1)
            }
            is WhiteNumber -> {
                it.number.substring(0, 1)
            }
            is Contact -> {
                it.name?.substring(0, 1)
            }
            is com.tarasovvp.blacklister.model.CallLog -> {
                it.calendarFromTime()
            }
            else -> {
                return@map null
            }
        }
    }.toList().distinct())
    for (key in keyList) {
        val valueList = this.filter {
            key == when (it) {
                is BlackNumber -> {
                    it.number.substring(0, 1)
                }
                is WhiteNumber -> {
                    it.number.substring(0, 1)
                }
                is Contact -> {
                    it.name?.substring(0, 1)
                }
                is com.tarasovvp.blacklister.model.CallLog -> {
                    it.calendarFromTime()
                }
                else -> it
            }
        }
        if (key is Calendar) {
            val callLog = valueList[0] as com.tarasovvp.blacklister.model.CallLog
            hashMapFromList[callLog.dateFromTime().toString()] = valueList
        } else if (key is String) {
            hashMapFromList[key] = valueList
        }

    }
    return hashMapFromList
}

fun <T> LiveData<T>.safeObserve(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) {
        it?.let(observer)
    }
}

fun <T> MutableLiveData<T>.safeSingleObserve(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    safeObserve(owner, observer)
    value = null
}

fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
    this.view.setBackgroundColor(colorInt)
    return this
}

fun Any?.isNotNull() = this != null

fun Boolean?.isTrue() = this == true

fun Int?.orZero() = this ?: 0

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}