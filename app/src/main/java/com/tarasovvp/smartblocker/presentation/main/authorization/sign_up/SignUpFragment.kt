package com.tarasovvp.smartblocker.presentation.main.authorization.sign_up

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSignUpBinding
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override var layoutId = R.layout.fragment_sign_up
    override val viewModelClass = SignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide()
        (binding?.root as? ViewGroup)?.hideKeyboardWithLayoutTouch()
        setContinueButton(binding?.container?.getViewsFromLayout(EditText::class.java))
        binding?.signUpEntrance?.setSafeOnClickListener {
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
            binding?.signUpContinue?.setSafeOnClickListener {
                binding?.root?.hideKeyboard()
                viewModel.createUserWithEmailAndPassword(binding?.signUpEmail.inputText(),
                    binding?.signUpPassword.inputText())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner) { email ->
                SharedPrefs.accountEmail = email
                (activity as MainActivity).apply {
                    getAllData()
                    if (SharedPrefs.smartBlockerTurnOff.isNotTrue() && isBlockerLaunched().not()) startBlocker()
                }
                findNavController().navigate(R.id.listBlockerFragment)
            }
        }
    }
}