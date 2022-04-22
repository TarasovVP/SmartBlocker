package com.tarasovvp.blacklister.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.databinding.FragmentSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService

class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>() {

    override fun getViewBinding() = FragmentSettingsBinding.inflate(layoutInflater)

    override val viewModelClass = SettingsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            binding?.settingsBackGroundCb?.isChecked =
                this.isServiceRunning(ForegroundCallService::class.java)
            binding?.settingsBackGroundCb?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startService()
                } else {
                    stopService()
                }
            }
        }
        binding?.settingsSignOutBtn?.setSafeOnClickListener {
            BlackListerApp.instance?.auth?.signOut()
            (activity as MainActivity).apply {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        binding?.settingsDeleteAccBtn?.setSafeOnClickListener {
            FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("signUserTAG", "SettingsFragment currentUser?.delete() task.isSuccessful ${task.isSuccessful}")
                    (activity as MainActivity).apply {
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                } else {
                    Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG", "SettingsFragment currentUser?.delete() task.exception ${task.exception}")
                }
            }
        }
    }

    override fun observeLiveData() {

    }

}