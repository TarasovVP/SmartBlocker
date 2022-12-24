package com.tarasovvp.smartblocker.local

import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.smartblocker.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.local.Settings.APP_LANG
import com.tarasovvp.smartblocker.local.Settings.APP_THEME
import com.tarasovvp.smartblocker.local.Settings.BLOCK_TURN_OFF
import com.tarasovvp.smartblocker.local.Settings.ON_BOARDING_SEEN
import com.tarasovvp.smartblocker.local.Settings.clearSharedPreferences
import com.tarasovvp.smartblocker.local.Settings.loadBoolean
import com.tarasovvp.smartblocker.local.Settings.loadInt
import com.tarasovvp.smartblocker.local.Settings.loadString
import com.tarasovvp.smartblocker.local.Settings.saveBoolean
import com.tarasovvp.smartblocker.local.Settings.saveInt
import com.tarasovvp.smartblocker.local.Settings.saveString

object SharedPreferencesUtil {

    var isOnBoardingSeen: Boolean
        get() = loadBoolean(ON_BOARDING_SEEN)
        set(isOnBoardingSeen) {
            saveBoolean(
                ON_BOARDING_SEEN,
                isOnBoardingSeen
            )
        }

    var appLang: String?
        get() = loadString(APP_LANG)
        set(appLang) {
            saveString(
                APP_LANG,
                appLang
            )
        }

    var appTheme: Int
        get() = loadInt(APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(appLang) {
            saveInt(
                APP_THEME,
                appLang
            )
        }

    var blockTurnOff: Boolean
        get() = loadBoolean(BLOCK_TURN_OFF)
        set(blockTurnOff) {
            saveBoolean(
                BLOCK_TURN_OFF,
                blockTurnOff
            )
        }

    var blockHidden: Boolean
        get() = loadBoolean(BLOCK_HIDDEN)
        set(blockHidden) {
            saveBoolean(
                BLOCK_HIDDEN,
                blockHidden
            )
        }

    fun clearAll() {
        clearSharedPreferences()
        isOnBoardingSeen = true
    }
}