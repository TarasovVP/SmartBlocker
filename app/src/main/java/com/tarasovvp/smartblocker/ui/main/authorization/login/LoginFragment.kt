package com.tarasovvp.smartblocker.ui.main.authorization.login

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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.EMAIL
import com.tarasovvp.smartblocker.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.smartblocker.databinding.FragmentLoginBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override var layoutId = R.layout.fragment_login
    override val viewModelClass = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setLoginButton(binding?.container?.getViewsFromLayout(EditText::class.java))
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
        setFragmentResultListener(FORGOT_PASSWORD) { _, bundle ->
            val email = bundle.getString(EMAIL, String.EMPTY)
            if (email.isNotEmpty()) {
                viewModel.sendPasswordResetEmail(email)
            } else {
                showMessage(getString(R.string.authorization_enter_email), true)
            }
        }
    }

    private fun setOnClickListeners() {
        binding?.apply {
            loginContinueWithoutAcc.setSafeOnClickListener {
                (activity as MainActivity).apply {
                    getAllData()
                    if (SharedPreferencesUtil.smartBlockerTurnOff.not() && isBlockerLaunched().not()) startBlocker()
                }
                findNavController().navigate(LoginFragmentDirections.startListBlockerFragment())
            }
            loginSignUp.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startSignUpFragment())
            }
            loginForgotPassword.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startForgotPasswordDialog(email = loginEmailInput.inputText()))
            }
            loginGoogleAuth.setSafeOnClickListener {
                googleSignInLauncher.launch(SmartBlockerApp.instance?.googleSignInClient?.signInIntent)
            }
        }
    }

    private fun setLoginButton(editTextList: ArrayList<EditText>?) {
        binding?.apply {
            loginContinue.isEnabled = editTextList?.none { it.text.isNullOrEmpty() }.isTrue()
            editTextList?.onEach { editText ->
                editText.doAfterTextChanged {
                    loginContinue.isEnabled = editTextList.none { it.text.isNullOrEmpty() }.isTrue()
                }
            }
            loginContinue.setSafeOnClickListener {
                viewModel.signInWithEmailAndPassword(loginEmailInput.inputText(),
                    loginPasswordInput.inputText())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).apply {
                    getAllData()
                    if (SharedPreferencesUtil.smartBlockerTurnOff.not() && isBlockerLaunched().not()) startBlocker()
                }
                findNavController().navigate(R.id.listBlockerFragment)
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
                account.idToken?.let { viewModel.firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                showMessage(CommonStatusCodes.getStatusCodeString(e.statusCode), true)
            }
        }

}