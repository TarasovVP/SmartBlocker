package com.tarasovvp.blacklister.ui.main.settings.accountdetals

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentAccountDetailsBinding
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
            viewModel.deleteUser()
        }
        binding?.accountDetailsNewNameBtn?.setSafeOnClickListener {
            viewModel.renameUser(binding?.accountDetailsNewNameInput?.text.toString())
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner, {
                showMessage(getString(R.string.operation_succeeded), false)
                (activity as MainActivity).apply {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            })
            successRenameUserLiveData.safeSingleObserve(viewLifecycleOwner, { name ->
                showMessage(String.format(getString(R.string.rename_succeed), name), false)
                binding?.accountDetailsMainTitle?.text =
                    String.format(getString(R.string.welcome), name)
                binding?.accountDetailsNewNameInput?.text?.clear()
            })
        }
    }

}