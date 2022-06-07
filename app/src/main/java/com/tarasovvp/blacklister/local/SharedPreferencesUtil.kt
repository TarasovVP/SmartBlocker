package com.tarasovvp.blacklister.local

import com.tarasovvp.blacklister.constants.Constants.APP_LANG_RU
import com.tarasovvp.blacklister.local.Settings.APP_LANG
import com.tarasovvp.blacklister.local.Settings.ON_BOARDING_SEEN
import com.tarasovvp.blacklister.local.Settings.loadBoolean
import com.tarasovvp.blacklister.local.Settings.loadString
import com.tarasovvp.blacklister.local.Settings.saveBoolean
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

    var appLang: String
        get() = loadString(APP_LANG) ?: APP_LANG_RU
        set(appLang) {
            saveString(
                APP_LANG,
                appLang
            )
        }
}