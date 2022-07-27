package com.tarasovvp.blacklister.ui.main.settings.accountdetals

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_USER
import com.tarasovvp.blacklister.databinding.FragmentAccountDetailsBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AccountDetailsFragment :
    BaseFragment<FragmentAccountDetailsBinding, AccountDetailsViewModel>() {

    override fun getViewBinding() = FragmentAccountDetailsBinding.inflate(layoutInflater)

    override val viewModelClass = AccountDetailsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.accountDetailsMainTitle?.text = String.format(getString(R.string.welcome),
            BlackListerApp.instance?.auth?.currentUser?.displayName)

        binding?.accountDetailsDeleteBtn?.setSafeOnClickListener {
            findNavController().navigate(AccountDetailsFragmentDirections.startAccountActionDialog())
        }

        binding?.accountDetailsNewNameBtn?.setSafeOnClickListener {
            viewModel.renameUser(binding?.accountDetailsNewNameInput?.text.toString())
        }

        binding?.accountDetailsNewPasswordBtn?.setSafeOnClickListener {
            if (binding?.accountDetailsNewPasswordCreate?.text.toString() == binding?.accountDetailsNewPasswordConfirm?.text.toString()) {
                viewModel.changePassword(binding?.accountDetailsNewPasswordConfirm?.text.toString())
            } else {
                showMessage(getString(R.string.passwords_different), true)
            }
        }

        binding?.includeNoAccount?.root?.isVisible =
            BlackListerApp.instance?.isLoggedInUser().isTrue().not()
        binding?.includeNoAccount?.noAccountBtn?.setSafeOnClickListener {
            findNavController().navigate(AccountDetailsFragmentDirections.startLoginFragment())
        }

        setFragmentResultListener(DELETE_USER) { _, _ ->
            viewModel.deleteUser()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).apply {
                    showMessage(getString(R.string.operation_succeeded), false)
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            successRenameUserLiveData.safeSingleObserve(viewLifecycleOwner) { name ->
                showMessage(String.format(getString(R.string.rename_succeed), name), false)
                binding?.accountDetailsMainTitle?.text =
                    String.format(getString(R.string.welcome), name)
                binding?.accountDetailsNewNameInput?.text?.clear()
            }
            successChangePasswordLiveData.safeSingleObserve(viewLifecycleOwner) { name ->
                showMessage(String.format(getString(R.string.change_password_succeed)), false)
                binding?.accountDetailsNewPasswordCreate?.text?.clear()
                binding?.accountDetailsNewPasswordConfirm?.text?.clear()
            }
        }
    }

}