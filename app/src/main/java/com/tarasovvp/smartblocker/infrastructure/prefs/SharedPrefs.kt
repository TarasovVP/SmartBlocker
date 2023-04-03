package com.tarasovvp.smartblocker.infrastructure.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_EN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_THEME
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_TURN_OFF
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_SEEN

@Suppress("UNCHECKED_CAST")
object SharedPrefs {

    private var sharedPref: SharedPreferences? = null

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    var isOnBoardingSeen: Boolean?
        get() = get(ON_BOARDING_SEEN, Boolean::class.java, false)
        set(isOnBoardingSeen) {
            put(ON_BOARDING_SEEN, isOnBoardingSeen)
        }

    var appLang: String?
        get() = get(APP_LANG, String::class.java, APP_LANG_EN)
        set(appLang) {
            put(APP_LANG, appLang)
        }

    var appTheme: Int?
        get() = get(APP_THEME, Int::class.java, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(appTheme) {
            put(APP_THEME, appTheme)
        }

    var smartBlockerTurnOff: Boolean?
        get() = get(BLOCK_TURN_OFF, Boolean::class.java, false)
        set(blockTurnOff) {
            put(BLOCK_TURN_OFF, blockTurnOff)
        }

    var blockHidden: Boolean?
        get() = get(BLOCK_HIDDEN, Boolean::class.java, false)
        set(blockHidden) {
            put(BLOCK_HIDDEN, blockHidden)
        }

    var country: String?
        get() = get(COUNTRY, String::class.java, COUNTRY)
        set(countryCode) {
            put(COUNTRY, countryCode)
        }

    var countryCode: String?
        get() = get(COUNTRY_CODE, String::class.java, COUNTRY_CODE)
        set(countryCode) {
            put(COUNTRY_CODE, countryCode)
        }

    private fun <T> get(key: String, clazz: Class<T>, defaultValue: Any): T =
        when (clazz) {
            String::class.java -> sharedPref?.getString(key, defaultValue.toString())
            Boolean::class.java -> sharedPref?.getBoolean(key, defaultValue as Boolean)
            Int::class.java -> sharedPref?.getInt(key, defaultValue as Int)
            else -> null
        } as T

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref?.edit()
        when (data) {
            is String -> editor?.putString(key, data)
            is Boolean -> editor?.putBoolean(key, data)
            is Int -> editor?.putInt(key, data)
        }
        editor?.apply()
    }
}