package com.tarasovvp.blacklister.ui.main.settings.settingslist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentSettingsListBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class SettingsListFragment : BaseFragment<FragmentSettingsListBinding, SettingsListViewModel>() {

    override fun getViewBinding() = FragmentSettingsListBinding.inflate(layoutInflater)

    override val viewModelClass = SettingsListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.settingsListLogOut?.setSafeOnClickListener {
            viewModel.signOut()
        }
        binding?.settingsListBlockSettings?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startBlockSettingsFragment())
        }
        binding?.settingsListAccountDetails?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startAccountDetailsFragment())
        }
        binding?.settingsListAppLanguage?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startAppLanguageFragment())
        }
        binding?.settingsListAppTheme?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startAppThemeFragment())
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
        }
    }

}