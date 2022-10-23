package com.tarasovvp.blacklister.local

import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.blacklister.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.blacklister.local.Settings.APP_LANG
import com.tarasovvp.blacklister.local.Settings.APP_THEME
import com.tarasovvp.blacklister.local.Settings.BLOCK_TURN_OFF
import com.tarasovvp.blacklister.local.Settings.FOREGROUND
import com.tarasovvp.blacklister.local.Settings.ON_BOARDING_SEEN
import com.tarasovvp.blacklister.local.Settings.clearSharedPreferences
import com.tarasovvp.blacklister.local.Settings.loadBoolean
import com.tarasovvp.blacklister.local.Settings.loadInt
import com.tarasovvp.blacklister.local.Settings.loadString
import com.tarasovvp.blacklister.local.Settings.saveBoolean
import com.tarasovvp.blacklister.local.Settings.saveInt
import com.tarasovvp.blacklister.local.Settings.saveString

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

    var foreGround: Boolean
        get() = loadBoolean(FOREGROUND)
        set(foreGround) {
            saveBoolean(
                FOREGROUND,
                foreGround
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