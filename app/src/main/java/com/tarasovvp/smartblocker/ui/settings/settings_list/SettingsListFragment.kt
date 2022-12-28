package com.tarasovvp.smartblocker.ui.settings.settings_list

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsListBinding
import com.tarasovvp.smartblocker.extensions.getViewsFromLayout
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class SettingsListFragment : BaseBindingFragment<FragmentSettingsListBinding>() {

    override var layoutId = R.layout.fragment_settings_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            container.getViewsFromLayout(TextView::class.java).forEach {
                it.setSafeOnClickListener { view ->
                    val direction = when (view.id) {
                        settingsAccount.id -> SettingsListFragmentDirections.startSettingsAccountFragment()
                        settingsLanguage.id -> SettingsListFragmentDirections.startSettingsLanguageFragment()
                        settingsTheme.id -> SettingsListFragmentDirections.startSettingsThemeFragment()
                        settingsEvaluate.id -> SettingsListFragmentDirections.startSettingsEvaluateDialog()
                        settingsReview.id -> SettingsListFragmentDirections.startSettingsReviewFragment()
                        settingsPrivacy.id -> SettingsListFragmentDirections.startSettingsPrivacyFragment()
                        else -> SettingsListFragmentDirections.startSettingsBlockerFragment()
                    }
                    findNavController().navigate(direction)
                }
            }
        }
    }
}