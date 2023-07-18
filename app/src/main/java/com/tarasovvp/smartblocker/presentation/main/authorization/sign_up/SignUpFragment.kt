package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

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
import com.tarasovvp.smartblocker.databinding.FragmentSignUpBinding
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.EXIST_ACCOUNT
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override var layoutId = R.layout.fragment_sign_up
    override val viewModelClass = SignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
        setContinueButton(binding?.container?.getViewsFromLayout(EditText::class.java))
        setOnClickListeners()
        setFragmentResultListeners()
    }

    private fun setOnClickListeners() {
        binding?.apply {
            signUpGoogleAuth.setSafeOnClickListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
            signUpContinue.setSafeOnClickListener {
                root.hideKeyboard()
                viewModel.fetchSignInMethodsForEmail(signUpEmail.inputText())
            }
            signUpEntrance.setSafeOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(EXIST_ACCOUNT) { _, _ ->
            googleSignInClient.signOut()
            findNavController().navigateUp()
        }
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

    override fun observeLiveData() {
        with(viewModel) {
            createEmailAccountLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.createUserWithEmailAndPassword(binding?.signUpEmail.inputText(), binding?.signUpPassword.inputText())
            }
            createGoogleAccountLiveData.safeSingleObserve(viewLifecycleOwner) { idToken ->
                viewModel.createUserWithGoogle(idToken)
            }
            accountExistLiveData.safeSingleObserve(viewLifecycleOwner) {
                findNavController().navigate(SignUpFragmentDirections.startExistAccountDialog(description = getString(R.string.authorization_account_exist)))
            }
            successSignUpLiveData.safeSingleObserve(viewLifecycleOwner) {
                viewModel.createCurrentUser(CurrentUser())
            }
            createCurrentUserLiveData.safeSingleObserve(viewLifecycleOwner) {
                findNavController().navigate(R.id.listBlockerFragment)
                (activity as? MainActivity)?.apply {
                    getAllData(true)
                    startBlocker()
                    setStartDestination(true)
                }
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