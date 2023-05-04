package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.AppDatabase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CHANGE_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CURRENT_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DELETE_USER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_OUT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NEW_PASSWORD
import com.tarasovvp.smartblocker.databinding.FragmentSettingsAccountBinding
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsAccountFragment :
    BaseFragment<FragmentSettingsAccountBinding, SettingsAccountViewModel>() {

    override var layoutId = R.layout.fragment_settings_account
    override val viewModelClass = SettingsAccountViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.includeEmptyState?.setDescription(EmptyState.EMPTY_STATE_ACCOUNT.descriptionRes())
        initViews()
        setOnclickListeners()
        setFragmentResults()
    }

    private fun setFragmentResults() {
        setFragmentResultListener(LOG_OUT) { _, _ ->
           viewModel.signOut()
        }
        setFragmentResultListener(DELETE_USER) { _, _ ->
           viewModel.deleteUser()
        }
        setFragmentResultListener(CHANGE_PASSWORD) { _, bundle ->
            viewModel.changePassword(bundle.getString(CURRENT_PASSWORD, String.EMPTY),
                bundle.getString(NEW_PASSWORD, String.EMPTY))
        }
    }

    private fun initViews() {
        binding?.apply {
            isLoggedInUser = SmartBlockerApp.instance?.isLoggedInUser()
            settingsAccountName.text = SharedPrefs.accountEmail
            settingsAccountAvatar.setImageDrawable(context?.getInitialDrawable(SharedPrefs.accountEmail.nameInitial()))
        }
    }

    private fun setOnclickListeners() {
        binding?.apply {
            settingsAccountLogOut.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
                findNavController().navigate(SettingsAccountFragmentDirections.startAccountActionDialog(isLogOut = true))
            }
            settingsAccountDelete.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startAccountActionDialog())
            }
            settingsAccountChangePassword.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startChangePasswordDialog())
            }
            settingsAccountLogin.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startLoginFragment())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner) {
                context?.let { context -> AppDatabase.getDatabase(context).clearAllTables() }
                (activity as MainActivity).apply {
                    stopBlocker()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            successChangePasswordLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(String.format(getString(R.string.settings_account_change_password_succeed)),
                    false)
            }
        }
    }

}