package com.tarasovvp.blacklister.ui.start.signup

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentSignUpBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override fun getViewBinding() = FragmentSignUpBinding.inflate(layoutInflater)

    override val viewModelClass = SignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        binding?.continueButton?.setSafeOnClickListener {
            viewModel.createUserWithEmailAndPassword(binding?.email?.text.toString(),
                binding?.password?.text.toString(),
                binding?.name?.text.toString())
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                showMessage(getString(R.string.success_sign_up), false)
                findNavController().popBackStack()
            })
        }
    }

}