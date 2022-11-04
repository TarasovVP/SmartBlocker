package com.tarasovvp.blacklister.ui.settings.account_detals

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.DELETE_USER
import com.tarasovvp.blacklister.databinding.FragmentAccountDetailsBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.settings.settings_list.SettingsListFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding, AccountDetailsViewModel>() {

    override var layoutId = R.layout.fragment_account_details
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
            includeEmptyState.emptyState = EmptyState.EMPTY_STATE_ACCOUNT
            includeEmptyState.root.isVisible =
                BlackListerApp.instance?.isLoggedInUser().isTrue().not()
            loginButton.isVisible =
                BlackListerApp.instance?.isLoggedInUser().isTrue().not()
            accountDetailsMainTitle.text = String.format(getString(R.string.welcome),
                BlackListerApp.instance?.auth?.currentUser?.email)
            (root as ViewGroup).hideKeyboardWithLayoutTouch()
            setOnclickListeners()
            initNewPasswordSet(accountDetailsChangePasswordContainer.getViewsFromLayout(EditText::class.java))
        }
    }

    private fun setOnclickListeners() {
        binding?.apply {
            accountDetailsLogOut.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
                findNavController().navigate(SettingsListFragmentDirections.startAccountActionDialog(
                    isLogOut = true))
            }
            accountDetailsDeleteBtn.setSafeOnClickListener {
                (activity as MainActivity).stopBlocker()
                findNavController().navigate(AccountDetailsFragmentDirections.startAccountActionDialog())
            }
            accountDetailsNewPasswordBtn.setSafeOnClickListener {
                if (accountDetailsNewPasswordCreate.inputText() == accountDetailsNewPasswordConfirm.text.toString()) {
                    viewModel.changePassword(accountDetailsCurrentPassword.inputText(),
                        accountDetailsNewPasswordConfirm.text.toString())
                } else {
                    showMessage(getString(R.string.passwords_different), true)
                }
            }
            loginButton.setSafeOnClickListener {
                findNavController().navigate(AccountDetailsFragmentDirections.startLoginFragment())
            }
        }
    }

    private fun initNewPasswordSet(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            accountDetailsNewPasswordCheck.setOnCheckedChangeListener { _, isChecked ->
                accountDetailsChangePasswordContainer.isVisible = isChecked
                if (isChecked) {
                    editTextList?.onEach { it.text.clear() }
                }
            }
            accountDetailsNewPasswordBtn.isEnabled =
                editTextList?.none { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    binding?.accountDetailsNewPasswordBtn?.isEnabled =
                        editTextList.none { it.text.isNullOrEmpty() }.isTrue()
                }
            }
            accountDetailsNewPasswordBtn.setSafeOnClickListener {
                if (accountDetailsNewPasswordCreate.inputText() == accountDetailsNewPasswordConfirm.inputText()) {
                    viewModel.changePassword(binding?.accountDetailsCurrentPassword.inputText(),
                        accountDetailsNewPasswordConfirm.text.toString())
                } else {
                    showMessage(getString(R.string.passwords_different), true)
                }
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