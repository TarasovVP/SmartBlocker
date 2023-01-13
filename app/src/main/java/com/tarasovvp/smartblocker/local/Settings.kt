package com.tarasovvp.smartblocker.local

import android.content.Context
import android.content.SharedPreferences
import com.tarasovvp.smartblocker.extensions.orZero

object Settings {

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