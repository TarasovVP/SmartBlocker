package com.tarasovvp.blacklister.ui.settings.account_detals

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.DELETE_USER
import com.tarasovvp.blacklister.databinding.FragmentAccountDetailsBinding
import com.tarasovvp.blacklister.extensions.getViewsFromLayout
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.settings.settings_list.SettingsListFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding, AccountDetailsViewModel>() {

    override fun getViewBinding() = FragmentAccountDetailsBinding.inflate(layoutInflater)

    override val viewModelClass = AccountDetailsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setFragmentResultListener(DELETE_USER) { _, _ ->
            viewModel.deleteUser()
        }

        setFragmentResultListener(Constants.LOG_OUT) { _, _ ->
            viewModel.signOut()
        }
    }

    private fun initViews() {
        binding?.apply {
            includeNoAccount.root.isVisible = BlackListerApp.instance?.isLoggedInUser().isTrue().not()
            accountDetailsMainTitle.text = String.format(getString(R.string.welcome), BlackListerApp.instance?.auth?.currentUser?.email)

            accountDetailsNewPasswordCheck.setOnCheckedChangeListener { _, isChecked ->
                accountDetailsChangePasswordContainer.isVisible = isChecked
                if (isChecked) {
                    accountDetailsChangePasswordContainer.getViewsFromLayout(EditText::class.java).apply {
                        this.forEach { it.text.clear() }
                    }
                }
            }

            accountDetailsLogOut.setSafeOnClickListener {
                findNavController().navigate(SettingsListFragmentDirections.startAccountActionDialog(isLogOut = true))
            }
            accountDetailsDeleteBtn.setSafeOnClickListener {
                findNavController().navigate(AccountDetailsFragmentDirections.startAccountActionDialog())
            }
            accountDetailsNewPasswordBtn.setSafeOnClickListener {
                if (accountDetailsNewPasswordCreate.text.toString() == accountDetailsNewPasswordConfirm.text.toString()) {
                    viewModel.changePassword(accountDetailsCurrentPassword.text.toString(), accountDetailsNewPasswordConfirm.text.toString())
                } else {
                    showMessage(getString(R.string.passwords_different), true)
                }
            }
            includeNoAccount.noAccountBtn.setSafeOnClickListener {
                findNavController().navigate(AccountDetailsFragmentDirections.startLoginFragment())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner) {
                SharedPreferencesUtil.clearAll()
                BlackListerApp.instance?.database?.clearAllTables()
                showMessage(getString(R.string.operation_succeeded), false)
                (activity as MainActivity).apply {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            successChangePasswordLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(String.format(getString(R.string.change_password_succeed)), false)
                binding?.accountDetailsNewPasswordCheck?.isChecked = false
            }
        }
    }

}