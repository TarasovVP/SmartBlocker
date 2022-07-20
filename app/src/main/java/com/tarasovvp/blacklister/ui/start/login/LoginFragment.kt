package com.tarasovvp.blacklister.ui.start.login

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.FORGOT_PASSWORD
import com.tarasovvp.blacklister.constants.Constants.SERVER_CLIENT_ID
import com.tarasovvp.blacklister.databinding.FragmentLoginBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)
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
        activity?.actionBar?.hide()
        setOnButtonsClick()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            FORGOT_PASSWORD)?.safeSingleObserve(viewLifecycleOwner) { email ->
            if (email.isNotEmpty()) {
                viewModel.sendPasswordResetEmail(email)
            } else {
                showMessage(getString(R.string.enter_your_email), true)
            }
        }
    }

    private fun setOnButtonsClick() {
        binding?.continueButton?.setSafeOnClickListener {
            if (binding?.email?.text.isNullOrEmpty() || binding?.password?.text.isNullOrEmpty()) {
                showMessage(getString(R.string.enter_login_password), true)
            } else {
                viewModel.signInWithEmailAndPassword(binding?.email?.text.toString(),
                    binding?.password?.text.toString())
            }
        }
        binding?.continueWithoutAccButton?.setSafeOnClickListener {
            (activity as MainActivity).apply {
                getAllData()
            }
            findNavController().navigate(R.id.callLogListFragment)
        }
        binding?.register?.setSafeOnClickListener {
            findNavController().navigate(R.id.startSignUpFragment)
        }
        binding?.buttonForgotPassword?.setSafeOnClickListener {
            findNavController().navigate(LoginFragmentDirections.startForgotPasswordDialog(email = binding?.email?.text.toString()))
        }
        binding?.googleAuth?.setSafeOnClickListener {
            googleSignInLauncher.launch(googleSignInClient?.signInIntent)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).apply {
                    getAllData()
                }
                findNavController().navigate(R.id.callLogListFragment)
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