package com.tarasovvp.blacklister.local

import android.content.Context
import android.content.SharedPreferences
import com.tarasovvp.blacklister.extensions.orZero

object Settings {

    const val ON_BOARDING_SEEN = "onBoardingSeen"
    const val APP_LANG = "appLang"
    const val APP_THEME = "appTheme"

    private var sharedPreferences: SharedPreferences? = null

    @JvmStatic
    fun loadSettingsHelper(context: Context, name: String) {
        sharedPreferences =
            context.getSharedPreferences(
                name,
                Context.MODE_PRIVATE
            )
    }

    @JvmStatic
    fun saveString(key: String?, value: String?) {
        val editor =
            sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    @JvmStatic
    fun loadString(key: String?): String? {
        return sharedPreferences?.getString(
            key,
            null
        )
    }

    @JvmStatic
    fun loadInt(key: String?, defaultInt: Int): Int {
        return sharedPreferences?.getInt(
            key,
            defaultInt
        ).orZero()
    }

    @JvmStatic
    fun saveInt(key: String?, value: Int?) {
        val editor =
            sharedPreferences?.edit()
        value?.let { editor?.putInt(key, it) }
        editor?.apply()
    }

    @JvmStatic
    fun loadLong(key: String?): Long {
        return sharedPreferences?.getLong(
            key,
            0
        ) ?: 0
    }

    @JvmStatic
    fun saveLong(key: String?, value: Long?) {
        val editor =
            sharedPreferences?.edit()
        value?.let { editor?.putLong(key, it) }
        editor?.apply()
    }

    @JvmStatic
    fun loadFloat(key: String?): Float {
        return sharedPreferences?.getFloat(
            key,
            0f
        ) ?: 0f
    }

    @JvmStatic
    fun saveFloat(key: String?, value: Float?) {
        val editor =
            sharedPreferences?.edit()
        value?.let { editor?.putFloat(key, it) }
        editor?.apply()
    }

    @JvmStatic
    fun saveBoolean(key: String?, value: Boolean) {
        val editor =
            sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    @JvmStatic
    fun loadBoolean(key: String?): Boolean {
        return sharedPreferences?.getBoolean(
            key,
            false
        ) ?: false
    }
}