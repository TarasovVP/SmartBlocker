package com.example.blacklister.ui.callloglist

import com.example.blacklister.model.CallLog

interface CallLogClickListener {
    fun onCallLogClicked(callLog: CallLog)
}