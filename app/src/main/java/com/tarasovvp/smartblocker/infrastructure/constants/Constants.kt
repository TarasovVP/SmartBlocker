package com.tarasovvp.smartblocker.infrastructure.constants

object Constants {
    //General
    const val ACCEPT_PERMISSIONS_SCREEN = 3
    const val APP_EXIT = "appExit"
    const val DIALOG = "dialog"
    const val IS_INSTRUMENTAL_TEST = "isInstrumentalTest"
    const val FILTER_INDEXES = "filterIndexes"
    const val SEARCH_QUERY = "searchQuery"
    const val RESUME_SCREEN = "resumeScreen"

    //Telephony
    const val LOG_CALL_CALL = "content://call_log/calls"
    const val GET_IT_TELEPHONY = "getITelephony"
    const val END_CALL = "endCall"
    const val PHONE_STATE = "android.intent.action.PHONE_STATE"
    const val CALL_ID = "_ID="
    const val CALL_RECEIVE = "callReceive"

    //Call type
    const val IN_COMING_CALL = "1"
    const val MISSED_CALL = "3"
    const val REJECTED_CALL = "5"
    const val BLOCKED_CALL = "6"

    //Notification
    const val NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL"
    const val FOREGROUND_CALL_SERVICE = "foregroundCallService"
    const val FOREGROUND_ID = 99

    //Auth
    const val ON_BOARDING_PAGE = "onBoardingPage"
    const val UNAUTHORIZED_ENTER = "unauthorizedEnter"
    const val EMAIL = "email"
    const val DELETE_USER = "deleteUser"
    const val LOG_OUT = "logOut"

    //Passwords
    const val FORGOT_PASSWORD = "forgotPassword"
    const val CHANGE_PASSWORD = "changePassword"
    const val CURRENT_PASSWORD = "currentPassword"
    const val NEW_PASSWORD = "newPassword"

    //Data base
    const val CONTACTS = "contacts"
    const val FILTERS = "filters"
    const val LOG_CALLS = "log_calls"
    const val FILTERED_CALLS = "filtered_calls"
    const val COUNTRY_CODES = "country_codes"

    //Real data base
    const val USERS = "users"
    const val REVIEWS = "reviews"
    const val BLOCK_HIDDEN = "blockHidden"
    const val FILTER_LIST = "filterList"
    const val FILTERED_CALL_LIST = "filteredCallList"
    const val DESC = "DESC"
    const val ASC = "ASC"

    //Filter
    const val DEFAULT_FILTER = -1
    const val BLOCKER = 1
    const val PERMISSION = 2
    const val FILTER_CONDITION_LIST = "filterConditionList"
    const val FILTER_ACTION = "filterAction"
    const val CALL_DELETE = "callDelete"

    //CountryCode
    const val COUNTRY_CODE_START = "+%s"
    const val COUNTRY_CODE = "countryCode"
    const val COUNTRY_DEFAULT = "UA"
    const val COUNTRY_CODE_DEFAULT = "+380"
    const val NUMBER_FORMAT_DEFAULT = "50 123 4567"

    //Number
    const val PLUS_CHAR = '+'
    const val MASK_CHAR = '#'
    const val SPACE = " "
    const val NUMBER_DATA_TYPE = 0
    const val HEADER_TYPE = 1
    const val NUMBER_TYPE = "numberType"

    //DateTime
    const val TIME_FORMAT = "HH:mm:ss"
    const val DATE_FORMAT = "dd.MM.yyyy"
    const val SECOND = 1000L

    //App languages
    const val APP_LANG_RU = "ru"
    const val APP_LANG_UK = "uk"
    const val APP_LANG_EN = "en"

    //Settings
    const val ON_BOARDING_SEEN = "onBoardingSeen"
    const val APP_LANG = "appLang"
    const val APP_THEME = "appTheme"
    const val BLOCK_TURN_ON = "blockTurnOff"
    const val SETTINGS_REVIEW = "settingsReview"

    //WebView
    const val MIME_TYPE = "text/html; charset=utf-8"
    const val ENCODING = "UTF-8"
    const val DRAWABLE = "drawable"
    const val DRAWABLE_RES = "\"file:///android_res/drawable/\""
    const val DARK_MODE_TEXT = "javascript:document.body.style.setProperty(\"color\", \"white\");"
    const val WHITE_MODE_TEXT = "javascript:document.body.style.setProperty(\"color\", \"black\");"
}