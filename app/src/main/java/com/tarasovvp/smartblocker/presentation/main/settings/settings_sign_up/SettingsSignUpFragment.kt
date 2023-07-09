package com.tarasovvp.smartblocker.presentation.main.settings.settings_sign_up

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.database.AppDatabase
import com.tarasovvp.smartblocker.databinding.FragmentSettingsSignUpBinding
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsSignUpFragment : BaseFragment<FragmentSettingsSignUpBinding, SettingSignUpViewModel>() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override var layoutId = R.layout.fragment_settings_sign_up
    override val viewModelClass = SettingSignUpViewModel::class.java

    private val currentUser: CurrentUser by lazy { CurrentUser() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
        getCurrentUserData()
        setContinueButton(binding?.container?.getViewsFromLayout(EditText::class.java))
        setOnClickListeners()
        setTransferDataSwitch()
    }

    private fun getCurrentUserData() {
        viewModel.getAllFilters()
        viewModel.getAllFilteredCalls()
        viewModel.getBlockHidden()
    }

    private fun setContinueButton(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            isInactive = editTextList?.any { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    isInactive = editTextList.any { it.text.isNullOrEmpty() }.isTrue()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding?.apply {
            settingsSignUpGoogleAuth.setSafeOnClickListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
            settingsSignUpContinue.setSafeOnClickListener {
                root.hideKeyboard()
                viewModel.fetchSignInMethodsForEmail(settingsSignUpEmail.inputText())
            }
        }
    }

    private fun setTransferDataSwitch() {
        binding?.settingsTransferDataSwitch?.setOnCheckedChangeListener { _, isChecked ->
            binding?.settingsTransferDataDescribe?.text = getString( if (isChecked) R.string.settings_account_transfer_data_turn_on else R.string.settings_account_transfer_data_turn_off)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            filtersLiveData.safeSingleObserve(viewLifecycleOwner) { filters ->
                currentUser.filterList.addAll(filters)
                Log.e("blockHiddenTAG", "SettingsSignUpFragment observeLiveData filtersLiveData currentUser $currentUser")
            }
            filteredCallsLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCalls ->
                currentUser.filteredCallList.addAll(filteredCalls)
                Log.e("blockHiddenTAG", "SettingsSignUpFragment observeLiveData filteredCallsLiveData currentUser $currentUser")
            }
            blockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { isBlockHidden ->
                currentUser.isBlockHidden = isBlockHidden
                Log.e("blockHiddenTAG", "SettingsSignUpFragment observeLiveData blockHiddenLiveData currentUser $currentUser")
            }
            successSignUpLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.createCurrentUser( if (binding?.settingsTransferDataSwitch?.isChecked.isTrue()) currentUser else CurrentUser())
            }
            createEmailAccountLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.createUserWithEmailAndPassword(binding?.settingsSignUpEmail.inputText(), binding?.settingsSignUpPassword.inputText())
            }
            createGoogleAccountLiveData.safeSingleObserve(viewLifecycleOwner) { idToken ->
                if (idToken.isEmpty()) {
                    googleSignInClient.signOut()
                } else {
                    viewModel.createUserWithGoogle(idToken)
                }
            }
            successSignUpLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.createCurrentUser(if (binding?.settingsTransferDataSwitch?.isChecked.isTrue()) currentUser else CurrentUser())
            }
            createCurrentUserLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as? MainActivity)?.apply {
                    AppDatabase.getDatabase(this).clearAllTables()
                    getAllData(true)
                    startBlocker()
                }
                findNavController().navigate(R.id.listBlockerFragment)
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.email?.let { viewModel.fetchSignInMethodsForEmail(it, account.idToken) }
            } catch (e: ApiException) {
                showMessage(CommonStatusCodes.getStatusCodeString(e.statusCode), true)
            }
        }
}