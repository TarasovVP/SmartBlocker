package com.tarasovvp.smartblocker.ui.start.login

import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.EMAIL
import com.tarasovvp.smartblocker.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.smartblocker.constants.Constants.SERVER_CLIENT_ID
import com.tarasovvp.smartblocker.databinding.FragmentLoginBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override var layoutId = R.layout.fragment_login
    override val viewModelClass = LoginViewModel::class.java

    private var googleSignInClient: GoogleSignInClient? = null

    override fun onStart() {
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

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
                showMessage(getString(R.string.enter_your_email), true)
            }
        }
    }

    private fun setOnClickListeners() {
        binding?.apply {
            loginContinueWithoutAcc.setSafeOnClickListener {
                (activity as MainActivity).apply {
                    getAllData()
                    if (SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()) startBlocker()
                }
                findNavController().navigate(LoginFragmentDirections.startBlackFilterListFragment())
            }
            loginSignUp.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startSignUpFragment())
            }
            loginForgotPassword.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startForgotPasswordDialog(email = loginEmailInput.inputText()))
            }
            loginGoogleAuth.setSafeOnClickListener {
                googleSignInLauncher.launch(googleSignInClient?.signInIntent)
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
                Log.e("authTAG",
                    "LoginFragment successSignInLiveData auth?.uid ${BlackListerApp.instance?.auth?.uid}")
                (activity as MainActivity).apply {
                    Log.e("blockerTAG",
                        "LoginFragment this $this SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not() ${SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()}")
                    getAllData()
                    if (SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()) startBlocker()
                }
                findNavController().navigate(R.id.blackFilterListFragment)
                Log.e("authTAG",
                    "LoginFragment successSignInLiveData navigate(R.id.blackFilterListFragment) auth?.uid ${BlackListerApp.instance?.auth?.uid}")
            }
            successPasswordResetLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(getString(R.string.password_reset_text), false)
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