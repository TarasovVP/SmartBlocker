package com.tarasovvp.blacklister.ui.start.login

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.EMAIL
import com.tarasovvp.blacklister.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.blacklister.constants.Constants.SERVER_CLIENT_ID
import com.tarasovvp.blacklister.databinding.FragmentLoginBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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
        setOnButtonsClick()
        setFragmentResultListener(FORGOT_PASSWORD) { _, bundle ->
            val email = bundle.getString(EMAIL, String.EMPTY)
            if (email.isNotEmpty()) {
                viewModel.sendPasswordResetEmail(email)
            } else {
                showMessage(getString(R.string.enter_your_email), true)
            }
        }
    }

    private fun setOnButtonsClick() {
        binding?.apply {
            loginContinue.setSafeOnClickListener {
                if (loginEmailInput.text.isEmpty() || loginPasswordInput.text.isEmpty()) {
                    showMessage(getString(R.string.enter_login_password), true)
                } else {
                    viewModel.signInWithEmailAndPassword(loginEmailInput.text.toString(),
                        loginPasswordInput.text.toString())
                }
            }
            loginContinueWithoutAcc.setSafeOnClickListener {
                (activity as MainActivity).apply {
                    getAllData()
                }
                findNavController().navigate(R.id.callListFragment)
            }
            loginSignUp.setSafeOnClickListener {
                findNavController().navigate(R.id.startSignUpFragment)
            }
            loginForgotPassword.setSafeOnClickListener {
                findNavController().navigate(LoginFragmentDirections.startForgotPasswordDialog(email = loginEmailInput.text.toString()))
            }
            loginGoogleAuth.setSafeOnClickListener {
                googleSignInLauncher.launch(googleSignInClient?.signInIntent)
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).getAllData()
                findNavController().navigate(R.id.blackFilterListFragment)
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
                showMessage(e.localizedMessage?.toString().toString(), false)
            }
        }

}