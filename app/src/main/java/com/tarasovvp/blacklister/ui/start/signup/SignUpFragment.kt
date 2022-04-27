package com.tarasovvp.blacklister.ui.start.signup

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentSignUpBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.start.GoogleFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class SignUpFragment : GoogleFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override fun getViewBinding() = FragmentSignUpBinding.inflate(layoutInflater)

    override val viewModelClass = SignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.googleAuth?.setSafeOnClickListener {
            googleSignInLauncher.launch(googleSignInClient?.signInIntent)
        }
        binding?.continueButton?.setSafeOnClickListener {
            viewModel.createUserWithEmailAndPassword(binding?.email?.text.toString(),
                binding?.password?.text.toString(),
                binding?.name?.text.toString())
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                showMessage(getString(R.string.success_sign_up))
                findNavController().popBackStack()
            })
        }
    }

}