package com.tarasovvp.smartblocker.presentation.main.authorization.login

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentLoginBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EMAIL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EXIST_ACCOUNT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.UNAUTHORIZED_ENTER
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.getViewsFromLayout
import com.tarasovvp.smartblocker.utils.extensions.hideKeyboard
import com.tarasovvp.smartblocker.utils.extensions.hideKeyboardWithLayoutTouch
import com.tarasovvp.smartblocker.utils.extensions.inputText
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override var layoutId = R.layout.fragment_login
    override val viewModelClass = LoginViewModel::class.java

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setOnFragmentResultListeners()
        setLoginButton(binding?.container?.getViewsFromLayout(EditText::class.java))
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
    }

    private fun setOnClickListeners() {
        binding?.apply {
            loginGoogleAuth.setSafeOnClickListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
            loginContinueWithoutAcc.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startUnauthorizedEnterDialog())
            }
            loginSignUp.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startSignUpFragment())
            }
            loginForgotPassword.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startForgotPasswordDialog(email = loginEmailInput.inputText()))
            }
        }
    }

    private fun setOnFragmentResultListeners() {
        setFragmentResultListener(FORGOT_PASSWORD) { _, bundle ->
            val email = bundle.getString(EMAIL, String.EMPTY)
            if (email.isNotEmpty()) {
                viewModel.sendPasswordResetEmail(email)
            } else {
                showMessage(getString(R.string.authorization_enter_email), true)
            }
        }
        setFragmentResultListener(EXIST_ACCOUNT) { _, _ ->
            googleSignInClient.signOut()
            findNavController().navigate(LoginFragmentDirections.startSignUpFragment())
        }
        setFragmentResultListener(UNAUTHORIZED_ENTER) { _, _ ->
            viewModel.signInAnonymously()
        }
    }

    private fun setLoginButton(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            isInactive = editTextList?.any { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    isInactive = editTextList.any { it.text.isNullOrEmpty() }.isTrue()
                }
            }
            loginEnter.setSafeOnClickListener {
                root.hideKeyboard()
                viewModel.fetchSignInMethodsForEmail(loginEmailInput.inputText())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            isEmailAccountExistLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.signInWithEmailAndPassword(
                    binding?.loginEmailInput.inputText(),
                    binding?.loginPasswordInput.inputText(),
                )
            }
            isGoogleAccountExistLiveData.safeSingleObserve(viewLifecycleOwner) { idToken ->
                viewModel.signInAuthWithGoogle(idToken)
            }
            accountExistLiveData.safeSingleObserve(viewLifecycleOwner) {
                findNavController().navigate(
                    LoginFragmentDirections.startExistAccountDialog(
                        description = getString(R.string.authorization_account_not_exist),
                    ),
                )
            }
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner) {
                findNavController().navigate(LoginFragmentDirections.startListBlockerFragment())
                (activity as? MainActivity)?.apply {
                    getAllData(true)
                    startBlocker()
                    setStartDestination(true)
                }
            }
            successPasswordResetLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(getString(R.string.authorization_password_reset_success), false)
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
