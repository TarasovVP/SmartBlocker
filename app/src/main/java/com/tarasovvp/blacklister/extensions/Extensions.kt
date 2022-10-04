package com.tarasovvp.blacklister.extensions

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
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
import com.tarasovvp.blacklister.constants.Constants.CALL_ID
import com.tarasovvp.blacklister.constants.Constants.END_CALL
import com.tarasovvp.blacklister.constants.Constants.GET_IT_TELEPHONY
import com.tarasovvp.blacklister.constants.Constants.LOG_CALL_CALL
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.databinding.PopUpWindowInfoBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import com.tarasovvp.blacklister.repository.ContactRepository
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max


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
    val cursor: Cursor? = this
        .contentResolver
        .query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null)
    val contactList = arrayListOf<Contact>()
    cursor?.use { contactCursor ->
        while (contactCursor.moveToNext()) {
            when (contactCursor.getString(0)) {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                    contactList.add(Contact(
                        id = contactCursor.getString(1),
                        name = contactCursor.getString(2),
                        photoUrl = contactCursor.getString(3),
                        phone = contactCursor.getString(4)
                    ))
                }
            }
        }
    }
    contactList.sortBy { it.name }
    return contactList
}

fun Context.systemCallLogCursor(): Cursor? {
    val projection = arrayListOf(
        CallLog.Calls._ID,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.CACHED_NORMALIZED_NUMBER,
        CallLog.Calls.COUNTRY_ISO,
        CallLog.Calls.NUMBER_PRESENTATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        projection.add(CallLog.Calls.CACHED_PHOTO_URI)
    }
    return this.contentResolver.query(
        Uri.parse(LOG_CALL_CALL),
        projection.toTypedArray(),
        null,
        null,
        null
    )
}

fun Cursor.createCallObject(isBlockedCall: Boolean): Call {
    val logCall = if (isBlockedCall) BlockedCall() else LogCall()
    logCall.callId = this.getString(0)
    logCall.name = this.getString(1)
    logCall.number = this.getString(2)
    logCall.type = this.getString(3)
    logCall.time = this.getString(4)
    logCall.normalizedNumber = this.getString(5)
    logCall.countryIso = this.getString(6)
    logCall.numberPresentation = this.getString(7)
    logCall.photoUrl =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) this.getString(7) else ContactRepository.getContactByPhone(
            logCall.number.orEmpty())?.photoUrl
    return logCall
}

fun Context.systemLogCallList(): ArrayList<LogCall> {
    val logCallList = ArrayList<LogCall>()
    systemCallLogCursor()?.use { callLogCursor ->
        while (callLogCursor.moveToNext()) {
            logCallList.add(callLogCursor.createCallObject(false) as LogCall)
        }
    }
    return logCallList
}

fun Context.deleteLastBlockedCall(number: String) {
    systemCallLogCursor()?.use { cursor ->
        while (cursor.moveToNext()) {
            val blockedCall = cursor.createCallObject(true) as BlockedCall
            if (number == blockedCall.number && REJECTED_CALL == blockedCall.type) {
                try {
                    Log.e("blockTAG",
                        "Extensions deleteLastMissedCall phone == number && type == REJECTED_CALL number $number name ${blockedCall.name} time ${blockedCall.time} phone ${blockedCall.number} type ${blockedCall.type} id ${blockedCall.callId}")
                    val result = this.contentResolver.delete(Uri.parse(LOG_CALL_CALL),
                        "${CALL_ID}'${blockedCall.callId}'",
                        null)
                    blockedCall.type = BLOCKED_CALL
                    BlockedCallRepository.insertBlockedCall(blockedCall)
                    Log.e("blockTAG",
                        "Extensions delete callId ${blockedCall.callId} result $result")
                    break
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    BlockedCallRepository.insertBlockedCall(blockedCall)
                    Log.e("blockTAG", "Extensions delete Exception ${e.localizedMessage}")
                }
            }
        }
    }
}

@BindingAdapter(value = ["circleImageUrl", "nameInitial"], requireAll = false)
fun ImageView.loadCircleImage(photoUrl: String?, nameInitial: String?) {
    val placeHolder = if (nameInitial.isNullOrEmpty()) ContextCompat.getDrawable(context,
        R.drawable.ic_avatar) else RoundedBitmapDrawableFactory.create(resources,
        nameInitial.let { context.getInitialBitmap(it) }).apply {
            isCircular = true
    }
    Glide
        .with(this.context)
        .load(photoUrl)
        .apply(RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeHolder)
            .error(placeHolder))
        .into(this)
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

fun EditText?.inputText(): String {
    return this?.text?.toString().orEmpty()
}

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
            @Suppress("UNCHECKED_CAST")
            val targetView = this.getChildAt(i) as T
            views.add(targetView)
        } else if (view is ViewGroup) {
            view.getViewsFromLayout(views, viewType)
        }
    }
    return views
}

fun String?.trimmed() = this?.filter { it.isDigit() || it == Constants.PLUS_CHAR }.orEmpty()

fun Context.filterDataList(conditionList: ArrayList<Condition>, result: () -> Unit): Boolean {
    val builder =
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.MultiChoiceAlertDialog))
    builder.setTitle(R.string.condition_dialog_title)
    builder.setMultiChoiceItems(conditionList.map { getString(it.title) }.toTypedArray(),
        conditionList.map { it.isSelected }.toBooleanArray()) { _, position, isChecked ->
        conditionList[position].isSelected = isChecked
    }
    builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
    builder.setPositiveButton(R.string.ok) { _, _ ->
        result.invoke()
    }
    builder.show()
    return true
}

fun Context.contactListMap(
    contactList: List<Contact>,
    isBlackFilter: Boolean,
): LinkedHashMap<String, List<Contact>> {
    val title = String.format(getString(R.string.contact_by_filter_list_title), contactList.size)
    val contactListMap = linkedMapOf<String, List<Contact>>(title to listOf())
    val affectedContactList = contactList.filterNot {
        if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
    }
    if (affectedContactList.isNotEmpty()) {
        val affectedContacts = String.format(getString(R.string.block_add_info),
            if (isBlackFilter) getString(R.string.can_block) else getString(R.string.can_unblock),
            affectedContactList.size)
        contactListMap[affectedContacts] = affectedContactList
    }

    val nonAffectedContactList = contactList.filter {
        if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
    }
    if (nonAffectedContactList.isNotEmpty()) {
        val nonAffectedContacts = String.format(getString(R.string.not_block_add_info),
            if (isBlackFilter) getString(R.string.can_block) else getString(R.string.can_unblock),
            nonAffectedContactList.size)
        contactListMap[nonAffectedContacts] = nonAffectedContactList
    }
    return contactListMap
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.getInitialBitmap(text: String): Bitmap? {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = spToPx(18F)
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.LEFT
    }
    val textBound = Rect()
    paint.getTextBounds(text, 0, text.length, textBound)
    val textHeight = textBound.height()
    val textWidth = textBound.width()
    val rectSize = max(textHeight + dpToPx(16f) * 2, textWidth + dpToPx(16f) * 2)

    val initialBitmap = Bitmap.createBitmap(
        rectSize.toInt(), rectSize.toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(initialBitmap)
    val rectF = RectF(0f, 0f, rectSize, rectSize)
    canvas.drawRect(rectF, paint)

    paint.color = Color.WHITE
    paint.strokeWidth = dpToPx(2F)
    paint.style = Paint.Style.STROKE
    canvas.drawCircle(rectSize / 2, rectSize / 2, rectSize / 2, paint)

    paint.style = Paint.Style.FILL
    canvas.drawText(
        text, (rectSize - textWidth) / 2, (rectSize - textHeight) / 2 + textHeight, paint
    )
    return initialBitmap
}

fun Context.dpToPx(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.spToPx(sp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}