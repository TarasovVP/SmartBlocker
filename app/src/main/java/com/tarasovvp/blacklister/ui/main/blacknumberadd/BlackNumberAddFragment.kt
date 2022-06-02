package com.tarasovvp.blacklister.ui.main.blacknumberadd

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberAddBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.contactdetail.ContactDetailFragmentArgs
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class BlackNumberAddFragment :
    BaseFragment<FragmentBlackNumberAddBinding, BlackNumberAddViewModel>() {

    override fun getViewBinding() = FragmentBlackNumberAddBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberAddViewModel::class.java

    private val args: BlackNumberAddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.blackNumber?.apply {
            initViewsWithData(this)
        }
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

    private fun initViewsWithData(blackNumber: BlackNumber) {
        binding?.blackNumberAddSearch?.setText(blackNumber.blackNumber)
        binding?.blackNumberAddStart?.isChecked = blackNumber.isStart.isTrue()
        binding?.blackNumberAddContain?.isChecked = blackNumber.isContain.isTrue()
        binding?.blackNumberAddEnd?.isChecked = blackNumber.isEnd.isTrue()
        binding?.blackNumberAddSubmit?.isEnabled = blackNumber.blackNumber.isNotEmpty()
    }

    override fun observeLiveData() {
        viewModel.blackNumberLiveData.safeSingleObserve(viewLifecycleOwner, {
            showMessage("Number ${it.blackNumber} is added", false)
            findNavController().popBackStack()
        })
    }

}