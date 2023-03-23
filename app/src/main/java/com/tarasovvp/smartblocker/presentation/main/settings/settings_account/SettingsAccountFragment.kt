package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
        initViews()
        setOnclickListeners()
        setFragmentResults()
    }

    private fun setFragmentResults() {
        setFragmentResultListener(LOG_OUT) { _, _ ->
            context?.googleSignInClient()?.let {viewModel.signOut(it)}
        }
        setFragmentResultListener(DELETE_USER) { _, _ ->
            context?.googleSignInClient()?.let { viewModel.deleteUser(it)  }
        }
        setFragmentResultListener(CHANGE_PASSWORD) { _, bundle ->
            viewModel.changePassword(bundle.getString(CURRENT_PASSWORD, String.EMPTY),
                bundle.getString(NEW_PASSWORD, String.EMPTY))
        }
    }

    private fun initViews() {
        binding?.apply {
            includeEmptyState.emptyState = EmptyState.EMPTY_STATE_ACCOUNT
            includeEmptyState.root.isVisible =
                SmartBlockerApp.instance?.isLoggedInUser().isNotTrue()
            settingsAccountLogin.isVisible =
                SmartBlockerApp.instance?.isLoggedInUser().isNotTrue()
            settingsAccountName.text = SmartBlockerApp.instance?.auth?.currentUser?.email
            settingsAccountAvatar.setImageDrawable(context?.getInitialDrawable(SmartBlockerApp.instance?.auth?.currentUser?.email.nameInitial()))
        }
    }

    private fun setOnclickListeners() {
        binding?.apply {
            settingsAccountLogOut.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
                findNavController().navigate(
                    SettingsAccountFragmentDirections.startAccountActionDialog(
                        isLogOut = true
                    )
                )
            }
            settingsAccountDelete.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
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