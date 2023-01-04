package com.tarasovvp.smartblocker.ui.settings.settings_review

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.databinding.FragmentSettingsReviewBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.extensions.hideKeyboard
import com.tarasovvp.smartblocker.extensions.isNotTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.Review
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

class SettingsReviewFragment :
    BaseFragment<FragmentSettingsReviewBinding, SettingsReviewViewModel>() {

    override var layoutId = R.layout.fragment_settings_review
    override val viewModelClass = SettingsReviewViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setClickListeners()
    }

    private fun initViews() {
        binding?.apply {
            includeEmptyState.emptyState = EmptyState.EMPTY_STATE_ACCOUNT
            includeEmptyState.root.isVisible =
                SmartBlockerApp.instance?.isLoggedInUser().isNotTrue()
            settingsReviewLogin.isVisible =
                SmartBlockerApp.instance?.isLoggedInUser().isNotTrue()
            settingsReviewInput.doAfterTextChanged {
                binding?.settingsReviewSend?.isEnabled = it.toString().isNotBlank()
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            settingsReviewSend.setSafeOnClickListener {
                viewModel.insertReview(Review(user = SmartBlockerApp.instance?.auth?.currentUser?.email.orEmpty(),
                    message = settingsReviewInput.text.toString(),
                    time = Date().time))
                settingsReviewInput.hideKeyboard()
            }
            settingsReviewLogin.setSafeOnClickListener {
                findNavController().navigate(SettingsReviewFragmentDirections.startLoginFragment())
            }
        }
    }


    override fun observeLiveData() {
        viewModel.successReviewLiveData.safeSingleObserve(viewLifecycleOwner) { review ->
            showMessage(String.format(getString(R.string.settings_review_send_success), review),
                false)
            binding?.settingsReviewInput?.text?.clear()
        }
    }
}