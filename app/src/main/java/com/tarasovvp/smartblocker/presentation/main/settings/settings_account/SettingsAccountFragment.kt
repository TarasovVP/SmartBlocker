package com.tarasovvp.smartblocker.presentation.main.settings.settings_account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.database.AppDatabase
import com.tarasovvp.smartblocker.databinding.FragmentSettingsAccountBinding
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CHANGE_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CURRENT_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DELETE_ACCOUNT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_OUT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NEW_PASSWORD
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsAccountFragment :
    BaseFragment<FragmentSettingsAccountBinding, SettingsAccountViewModel>() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override var layoutId = R.layout.fragment_settings_account
    override val viewModelClass = SettingsAccountViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.includeEmptyState?.setDescription(EmptyState.EMPTY_STATE_ACCOUNT.descriptionRes())
        initViews()
        setOnclickListeners()
        setFragmentResults()
    }

    private fun setFragmentResults() {
        setFragmentResultListener(LOG_OUT) { _, _ ->
           viewModel.signOut()
        }
        setFragmentResultListener(DELETE_ACCOUNT) { _, bundle ->
            if (firebaseAuth.isGoogleAuthUser()) {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            } else {
                val authCredential = EmailAuthProvider.getCredential(firebaseAuth.currentUser?.email.orEmpty(), bundle.getString(CURRENT_PASSWORD, String.EMPTY))
                viewModel.reAuthenticate(authCredential)
            }
        }
        setFragmentResultListener(CHANGE_PASSWORD) { _, bundle ->
            viewModel.changePassword(bundle.getString(CURRENT_PASSWORD, String.EMPTY),
                bundle.getString(NEW_PASSWORD, String.EMPTY))
        }
    }

    fun initViews() {
        binding?.apply {
            isLoggedInUser = firebaseAuth.isAuthorisedUser()
            settingsAccountName.text = if (isLoggedInUser.isTrue()) firebaseAuth.currentUser?.currentUserEmail() else getString(R.string.settings_account_unauthorised)
            when {
                firebaseAuth.isGoogleAuthUser() -> settingsAccountAvatar.setImageResource(R.drawable.ic_logo_google)
                firebaseAuth.isAuthorisedUser() -> settingsAccountAvatar.setImageResource(R.drawable.ic_email)
                else -> settingsAccountAvatar.setImageBitmap(context?.getInitialDrawable(firebaseAuth.currentUser?.currentUserEmail().nameInitial())?.toBitmap())
            }
            executePendingBindings()
            settingsAccountChangePassword.isVisible = firebaseAuth.isAuthorisedUser() && firebaseAuth.isGoogleAuthUser().not()
        }
    }

    private fun setOnclickListeners() {
        binding?.apply {
            settingsAccountLogOut.setSafeOnClickListener {
                (activity as? MainActivity)?.stopBlocker()
                findNavController().navigate(SettingsAccountFragmentDirections.startLogOutDialog(isAuthorised = firebaseAuth.isAuthorisedUser()))
            }
            settingsAccountDelete.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startDeleteAccountDialog(isGoogleAuth = firebaseAuth.isGoogleAuthUser()))
            }
            settingsAccountChangePassword.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startChangePasswordDialog())
            }
            settingsAccountSignUp.setSafeOnClickListener {
                findNavController().navigate(SettingsAccountFragmentDirections.startSettingsSignUpFragment())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            reAuthenticateLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.deleteUser()
            }
            successLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as? MainActivity)?.apply {
                    AppDatabase.getDatabase(this).clearAllTables()
                    stopBlocker()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            successChangePasswordLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(String.format(getString(R.string.settings_account_change_password_succeed)),
                    false)
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    viewModel.reAuthenticate(authCredential)
                }
            } catch (e: ApiException) {
                showMessage(CommonStatusCodes.getStatusCodeString(e.statusCode), true)
            }
        }
}