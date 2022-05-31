package com.tarasovvp.blacklister.ui.main.blacknumberladd

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberAddBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class BlackNumberAddFragment :
    BaseFragment<FragmentBlackNumberAddBinding, BlackNumberAddViewModel>() {

    override fun getViewBinding() = FragmentBlackNumberAddBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberAddViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.blackNumberAddSearch?.doAfterTextChanged {
            binding?.blackNumberAddSubmit?.isEnabled = it?.isNotEmpty().isTrue()
        }
        binding?.blackNumberAddSubmit?.setSafeOnClickListener {
            viewModel.insertBlackNumber(BlackNumber(blackNumber = binding?.blackNumberAddSearch?.text.toString(),
                isStart = binding?.blackNumberAddStart?.isChecked.isTrue(),
                isContain = binding?.blackNumberAddContain?.isChecked.isTrue(),
                isEnd = binding?.blackNumberAddEnd?.isChecked.isTrue()))
        }
    }

    override fun observeLiveData() {
        viewModel.blackNumberLiveData.safeSingleObserve(viewLifecycleOwner, {
            showMessage("Number ${it.blackNumber} is added", false)
            findNavController().popBackStack()
        })
    }

}