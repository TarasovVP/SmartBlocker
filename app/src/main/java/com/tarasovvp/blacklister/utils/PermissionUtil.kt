package com.tarasovvp.blacklister.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtil {

    fun permissionsArray(): Array<String> {
        val permissionsArray: ArrayList<String> = arrayListOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissionsArray.add(Manifest.permission.ANSWER_PHONE_CALLS)
        }
        return permissionsArray.toTypedArray()
    }

    fun Context.checkPermissions(): Boolean {
        for (permission in permissionsArray()) {
            if (!this.isPermissionAccepted(permission)) return false
        }
        return true
    }

    private fun Context.isPermissionAccepted(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}