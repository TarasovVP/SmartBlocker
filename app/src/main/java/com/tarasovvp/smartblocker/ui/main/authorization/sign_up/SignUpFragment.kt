package com.tarasovvp.smartblocker.ui.main.authorization.sign_up

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSignUpBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

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
        binding?.signUpEntrance?.setSafeOnClickListener {
            findNavController().navigateUp()
        }
        binding?.signUpContinue?.setSafeOnClickListener {
            viewModel.createUserWithEmailAndPassword(binding?.signUpEmail.inputText(),
                binding?.signUpPassword.inputText())
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
        }
    }

}