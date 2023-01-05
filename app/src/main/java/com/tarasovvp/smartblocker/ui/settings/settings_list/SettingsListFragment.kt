package com.tarasovvp.smartblocker.ui.settings.settings_list

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.SETTINGS_REVIEW
import com.tarasovvp.smartblocker.databinding.FragmentSettingsListBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.models.Review
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

class SettingsListFragment : BaseFragment<FragmentSettingsListBinding, SettingsListViewModel>() {

    override var layoutId = R.layout.fragment_settings_list
    override val viewModelClass = SettingsListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            settingsLanguage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_settings_language,
                0,
                SharedPreferencesUtil.appLang.orEmpty().flagDrawable(),
                0)
            settingsReview.isVisible = SmartBlockerApp.instance?.isLoggedInUser().isTrue()
            container.getViewsFromLayout(TextView::class.java).forEach {
                it.setSafeOnClickListener { view ->
                    val direction = when (view.id) {
                        settingsAccount.id -> SettingsListFragmentDirections.startSettingsAccountFragment()
                        settingsLanguage.id -> SettingsListFragmentDirections.startSettingsLanguageFragment()
                        settingsTheme.id -> SettingsListFragmentDirections.startSettingsThemeFragment()
                        settingsReview.id -> SettingsListFragmentDirections.startSettingsReviewDialog()
                        settingsPrivacy.id -> SettingsListFragmentDirections.startSettingsPrivacyFragment()
                        else -> SettingsListFragmentDirections.startSettingsBlockerFragment()
                    }
                    findNavController().navigate(direction)
                }
            }
            setFragmentResultListener(SETTINGS_REVIEW) { _, bundle ->
                viewModel.insertReview(Review(SmartBlockerApp.instance?.auth?.currentUser?.email.toString(),
                    bundle.getString(SETTINGS_REVIEW, String.EMPTY),
                    Date().time))
            }
        }
    }

    override fun observeLiveData() {
        viewModel.successReviewLiveData.safeSingleObserve(viewLifecycleOwner) { review ->
            showMessage(String.format(getString(R.string.settings_review_send_success), review),
                false)
        }
    }

}