package com.tarasovvp.blacklister.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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
        binding?.settingsNameTv?.text = String.format(getString(R.string.welcome), BlackListerApp.instance?.auth?.currentUser?.displayName)
        binding?.settingsSignOutBtn?.setSafeOnClickListener {
            viewModel.signOut()
        }
        binding?.settingsDeleteAccBtn?.setSafeOnClickListener {
            viewModel.deleteUser()
        }
        binding?.settingsDeleteAccBtn?.setSafeOnClickListener {
            viewModel.renameUser(binding?.settingsChangeNameInput?.text.toString())
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner, {
                Log.e("signUserTAG", "SettingsFragment observeLiveData successLiveData $it")
                (activity as MainActivity).apply {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            })
            successRenameUserLiveData.safeSingleObserve(viewLifecycleOwner, { name ->
                binding?.settingsNameTv?.text = String.format(getString(R.string.welcome), name)
            })
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                Log.e("signUserTAG",
                    "SettingsFragment observeLiveData exceptionLiveData exception $exception")
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
            })
        }
    }

}