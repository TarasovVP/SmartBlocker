package com.tarasovvp.smartblocker.presentation.main.settings.settings_list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsListBinding
import com.tarasovvp.smartblocker.domain.entities.models.Feedback
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SETTINGS_FEEDBACK
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.flagDrawable
import com.tarasovvp.smartblocker.utils.extensions.getViewsFromLayout
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class SettingsListFragment : BaseFragment<FragmentSettingsListBinding, SettingsListViewModel>() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override var layoutId = R.layout.fragment_settings_list
    override val viewModelClass = SettingsListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAppLanguage()
        initViews()
    }

    fun initViews() {
        binding?.apply {
            settingsFeedback.isVisible = firebaseAuth.isAuthorisedUser()
            container.getViewsFromLayout(TextView::class.java).forEach {
                it.setSafeOnClickListener {
                    if (it.id == settingsTalkToAi.id) {
                        launchOtherApp()
                        return@setSafeOnClickListener
                    }
                    val direction = when (it.id) {
                        settingsAccount.id -> SettingsListFragmentDirections.startSettingsAccountFragment()
                        settingsLanguage.id -> SettingsListFragmentDirections.startSettingsLanguageFragment()
                        settingsTheme.id -> SettingsListFragmentDirections.startSettingsThemeFragment()
                        settingsFeedback.id -> SettingsListFragmentDirections.startSettingsFeedbackDialog()
                        settingsPrivacy.id -> SettingsListFragmentDirections.startSettingsPrivacyFragment()
                        else -> SettingsListFragmentDirections.startSettingsBlockerFragment()
                    }
                    findNavController().navigate(direction)
                }
            }
            setFragmentResultListener(SETTINGS_FEEDBACK) { _, bundle ->
                viewModel.insertFeedback(Feedback(firebaseAuth.currentUser?.email.orEmpty(), bundle.getString(SETTINGS_FEEDBACK, String.EMPTY), Date().time))
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            appLanguageLiveData.safeSingleObserve(viewLifecycleOwner) { appLang ->
                binding?.settingsLanguage?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_settings_language, 0, appLang.flagDrawable(), 0)
            }
        }
        viewModel.successFeedbackLiveData.safeSingleObserve(viewLifecycleOwner) { feedback ->
            showMessage(String.format(getString(R.string.settings_feedback_send_success), feedback),
                false)
        }
    }

    private fun launchOtherApp() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://smartblocker.onelink.me/kUoF?af_xp=app&pid=Cross_sale&c=Talk%20To%20AI&af_dp=talktoai%3A%2F%2Fcom.vnstudio.talktoai"))
        startActivity(intent)
    }

}