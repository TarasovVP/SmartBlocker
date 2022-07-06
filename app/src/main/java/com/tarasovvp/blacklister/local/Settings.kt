package com.tarasovvp.blacklister.local

import android.content.Context
import android.content.SharedPreferences
import com.tarasovvp.blacklister.extensions.orZero

object Settings {

    const val ON_BOARDING_SEEN = "onBoardingSeen"
    const val APP_LANG = "appLang"
    const val APP_THEME = "appTheme"
    const val PRIORITY = "priority"

    private var sharedPreferences: SharedPreferences? = null

    fun loadSettingsHelper(context: Context, name: String) {
        sharedPreferences =
            context.getSharedPreferences(
                name,
                Context.MODE_PRIVATE
            )
    }

    fun saveString(key: String?, value: String?) {
        val editor =
            sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun loadString(key: String?): String? {
        return sharedPreferences?.getString(
            key,
            null
        )
    }

    fun loadInt(key: String?, defaultInt: Int): Int {
        return sharedPreferences?.getInt(
            key,
            defaultInt
        ).orZero()
    }

    fun saveInt(key: String?, value: Int?) {
        val editor =
            sharedPreferences?.edit()
        value?.let { editor?.putInt(key, it) }
        editor?.apply()
    }

    fun saveBoolean(key: String?, value: Boolean) {
        val editor =
            sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun loadBoolean(key: String?): Boolean {
        return sharedPreferences?.getBoolean(
            key,
            false
        ) ?: false
    }

    fun clearSharedPreferences() {
        sharedPreferences?.edit()?.clear()?.apply()
    }
}