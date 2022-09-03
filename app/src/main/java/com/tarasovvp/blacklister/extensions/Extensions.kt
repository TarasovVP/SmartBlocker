package com.tarasovvp.blacklister.extensions

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.DATE
import com.tarasovvp.blacklister.constants.Constants.DESC
import com.tarasovvp.blacklister.constants.Constants.EIGHT_ZERO
import com.tarasovvp.blacklister.constants.Constants.END_CALL
import com.tarasovvp.blacklister.constants.Constants.GET_IT_TELEPHONY
import com.tarasovvp.blacklister.constants.Constants.LOG_CALL_CALL
import com.tarasovvp.blacklister.constants.Constants.NUMBER
import com.tarasovvp.blacklister.constants.Constants.PHONE_NUMBER_CODE
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.constants.Constants.THREE_EIGHT_ZERO
import com.tarasovvp.blacklister.constants.Constants.TYPE
import com.tarasovvp.blacklister.constants.Constants.ZERO
import com.tarasovvp.blacklister.databinding.PopUpWindowInfoBinding
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


fun CoroutineScope.launchIO(
    onError: (Throwable, suspend CoroutineScope.() -> Unit) -> Any?,
    block: suspend CoroutineScope.() -> Unit,
): Job =
    launch(CoroutineExceptionHandler { _, exception ->
        onError(exception, block)
    }) {
        withContext(Dispatchers.IO) {
            block()
        }
    }

fun View.showMessage(message: String, isError: Boolean) {
    ContextCompat.getColor(context, if (isError) android.R.color.holo_red_light else R.color.blue)
        .let { color ->
            Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
                .apply {
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.width = FrameLayout.LayoutParams.MATCH_PARENT
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines =
                        Int.MAX_VALUE
                }.withColor(color).show()
        }
}

fun View.showPopUpWindow(info: Info) {
    val popupView = PopUpWindowInfoBinding.inflate(LayoutInflater.from(context))
    popupView.popUpWindowTitle.text = info.title
    popupView.popUpWindowDescription.text = info.description
    popupView.popUpWindowIcon.setImageResource(info.icon)
    popupView.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val popupWindow = PopupWindow(
        popupView.root,
        (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    )
    val locationScreen = intArrayOf(0, 0)
    this.getLocationOnScreen(locationScreen)
    val isBelowScreenMiddle =
        locationScreen[1] > Resources.getSystem().displayMetrics.heightPixels / 2
    popupWindow.showAsDropDown(this,
        this.measuredWidth,
        if (isBelowScreenMiddle) -popupView.root.measuredHeight else 0)
    popupView.popUpWindowClose.setSafeOnClickListener {
        popupWindow.dismiss()
    }
}

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
                        val id = getString(idField)
                        val photoUrl = getString(photoUri)
                        val name = getString(nameField)
                        contactsById[data] =
                            Contact(
                                id = id,
                                name = name,
                                photoUrl = photoUrl,
                                phone = data
                            )
                    }
                }
            }
            close()
            contactsById.values.toList().sortedWith(compareBy { it.name })
        }
    return ArrayList(contacts.toList())
}

fun Context.systemLogCallList(): ArrayList<LogCall> {
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE
    )
    val logCallList = ArrayList<LogCall>()
    val cursor: Cursor? = this.contentResolver.query(
        Uri.parse(LOG_CALL_CALL),
        projection,
        null,
        null,
        null
    )
    cursor?.use { logCallCursor ->
        while (logCallCursor.moveToNext()) {
            val logCall = LogCall()
            logCall.time = logCallCursor.getString(3)
            logCall.name = logCallCursor.getString(0)
            logCall.phone = logCallCursor.getString(1)
            logCall.type = logCallCursor.getString(1)
            logCall.photoUrl =
                ContactRepository.getContactByPhone(logCall.phone.orEmpty())?.photoUrl
            logCallList.add(logCall)
        }
    }
    logCallList.sortByDescending {
        it.time?.toMillisecondsFromString()
    }
    return logCallList
}

fun Context.deleteLastMissedCall(phone: String): Boolean {
    val projection = arrayOf(
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE
    )

    val cursor: Cursor? = this.contentResolver.query(
        Uri.parse(LOG_CALL_CALL),
        projection,
        null,
        null,
        "${CallLog.Calls.DATE} $DESC"
    )
    cursor?.use {
        while (cursor.moveToNext()) {
            val phoneNumber: String = cursor.getString(1)
            val time: String? = cursor.getString(3)
            val queryString = "${NUMBER}'$phone' AND ${DATE}'$time' AND ${TYPE}'$REJECTED_CALL'"
            if (phone == phoneNumber.toFormattedPhoneNumber()) {
                try {
                    val blockedCall = BlockedCall()
                    blockedCall.time = time
                    blockedCall.name = cursor.getString(0)
                    blockedCall.phone = phoneNumber
                    blockedCall.type = BLOCKED_CALL
                    blockedCall.photoUrl =
                        ContactRepository.getContactByPhone(phoneNumber)?.photoUrl
                    BlockedCallRepository.insertBlockedCall(blockedCall)
                    this.contentResolver.delete(Uri.parse(LOG_CALL_CALL), queryString, null)
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


@BindingAdapter("bind:circleImageUrl")
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

fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL,
            Constants.FOREGROUND_CALL_SERVICE, NotificationManager.IMPORTANCE_HIGH
        )
        channel.lightColor = Color.BLUE
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
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
            is Call -> {
                it.calendarFromTime()
            }
            is Contact -> {
                it.name?.first().toString()
            }
            is Filter -> {
                it.filter.first().toString()
            }
            else -> {
                return@map null
            }
        }
    }.toList().distinct())
    keyList.forEach { key ->
        val valueList = this.filter {
            key == when (it) {
                is Call -> {
                    it.calendarFromTime()
                }
                is Contact -> {
                    it.name?.first().toString()
                }
                is Filter -> {
                    it.filter.first().toString()
                }
                else -> it
            }
        }
        if (key is Calendar) {
            val call = valueList[0] as Call
            hashMapFromList[call.dateFromTime().toString()] = valueList
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

val String.Companion.EMPTY: String
    get() = ""

fun Locale.flagEmoji(): String {
    if (country.isEmpty()) return String.EMPTY
    val firstLetter = Character.codePointAt(country, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(country, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}

fun Context.getUserCountry(): String? {
    try {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        when {
            simCountry != null && simCountry.length == 2 -> {
                return simCountry.lowercase(Locale.US)
            }
            tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA -> {
                val networkCountry = tm.networkCountryIso
                if (networkCountry.isNotNull() && networkCountry.length == 2) {
                    return networkCountry.lowercase(Locale.US)
                }
            }
        }
    } catch (e: java.lang.Exception) {
        return null
    }
    return null
}

fun <T> ViewGroup.getViewsFromLayout(
    viewType: Class<T>,
): ArrayList<T> {
    return this.getViewsFromLayout(ArrayList(), viewType)
}

private fun <T> ViewGroup.getViewsFromLayout(
    views: ArrayList<T>,
    viewType: Class<T>,
): ArrayList<T> {
    val childCount = this.childCount
    for (i in 0 until childCount) {
        val view = this.getChildAt(i)
        if (viewType.isInstance(view)) {
            val targetView = this.getChildAt(i) as T
            views.add(targetView)
        } else if (view is ViewGroup) {
            view.getViewsFromLayout(views, viewType)
        }
    }
    return views
}