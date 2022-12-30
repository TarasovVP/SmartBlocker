package com.tarasovvp.smartblocker.ui.start.sign_up

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSignUpBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class SignUpViewModelFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override var layoutId = R.layout.fragment_sign_up
    override val viewModelClass = SignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
        initContinueButton(binding?.container?.getViewsFromLayout(EditText::class.java))

    }

    private fun initContinueButton(editTextList: ArrayList<EditText>?) {
        binding?.signUpContinue?.isEnabled = editTextList?.none { it.text.isNullOrEmpty() }.isTrue()
        editTextList?.onEach { editText ->
            editText.doAfterTextChanged {
                binding?.signUpContinue?.isEnabled =
                    editTextList.none { it.text.isNullOrEmpty() }.isTrue()
            }
        }
        binding?.signUpContinue?.setSafeOnClickListener {
            if (binding?.signUpPassword.inputText() == binding?.signUpRepeatPassword.inputText()) {
                viewModel.createUserWithEmailAndPassword(binding?.signUpEmail.inputText(),
                    binding?.signUpRepeatPassword.inputText())
            } else {
                showMessage(getString(R.string.passwords_different), true)
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner) {
                showMessage(getString(R.string.success_sign_up), false)
                findNavController().popBackStack()
            }
        }
    }

}