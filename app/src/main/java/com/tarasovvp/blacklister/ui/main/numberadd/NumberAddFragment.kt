package com.tarasovvp.blacklister.ui.main.numberadd

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentNumberAddBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberAddFragment :
    BaseFragment<FragmentNumberAddBinding, NumberAddViewModel>() {

    override fun getViewBinding() = FragmentNumberAddBinding.inflate(layoutInflater)

    override val viewModelClass = NumberAddViewModel::class.java

    private val args: NumberAddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewsWithData()
        binding?.numberAddSearch?.doAfterTextChanged {
            binding?.numberAddSubmit?.isEnabled = it?.isNotEmpty().isTrue()
        }
        binding?.numberDeleteSubmit?.setSafeOnClickListener {
            args.number?.let {
                if (it.isBlackNumber) {
                    findNavController().navigate(NumberAddFragmentDirections.startDeleteNumberDialog(
                        blackNumber = it as BlackNumber))
                } else {
                    findNavController().navigate(NumberAddFragmentDirections.startDeleteNumberDialog(
                        whiteNumber = it as WhiteNumber))
                }
            }
        }
        binding?.numberAddSubmit?.setSafeOnClickListener {
            if (args.number?.isBlackNumber.isTrue()) {
                viewModel.insertBlackNumber(BlackNumber(number = binding?.numberAddSearch?.text.toString()).apply {
                    start = binding?.numberAddStart?.isChecked.isTrue()
                    contain = binding?.numberAddContain?.isChecked.isTrue()
                    end = binding?.numberAddEnd?.isChecked.isTrue()
                })
            } else {
                viewModel.insertWhiteNumber(WhiteNumber(number = binding?.numberAddSearch?.text.toString()).apply {
                    start = binding?.numberAddStart?.isChecked.isTrue()
                    contain = binding?.numberAddContain?.isChecked.isTrue()
                    end = binding?.numberAddEnd?.isChecked.isTrue()
                })
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            DELETE_NUMBER)?.safeSingleObserve(viewLifecycleOwner) {
            args.number?.let {
                if (it.isBlackNumber) {
                    viewModel.deleteBlackNumber(it as BlackNumber)
                } else {
                    viewModel.deleteWhiteNumber(it as WhiteNumber)
                }
            }
        }
    }

    private fun initViewsWithData() {
        binding?.apply {
            numberAddTitle.text = String.format(getString(R.string.fill_data_press_button),
                if (args.number?.number.isNullOrEmpty().not()
                ) getString(R.string.edit_number) else getString(R.string.add_number),
                if (args.number?.isBlackNumber.isTrue()) getString(R.string.black_list) else getString(
                    R.string.white_list))
            numberAddTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                if (args.number?.isBlackNumber.isTrue()) R.drawable.ic_black_number else R.drawable.ic_white_number, 0, 0)
            numberAddSearch.setText(args.number?.number.orEmpty())
            numberAddStart.isChecked = args.number?.start.isTrue()
            numberAddContain.isChecked = args.number?.contain.isTrue()
            numberAddEnd.isChecked = args.number?.end.isTrue()
            numberAddSubmit.isEnabled = args.number?.number.isNullOrEmpty().isTrue().not()
            numberDeleteSubmit.isVisible = args.number?.number.orEmpty().isNotEmpty()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            insertBlackNumberLiveData.safeSingleObserve(viewLifecycleOwner) { blackNumber ->
                handleSuccessNumberAction(String.format(getString(R.string.number_added),
                    blackNumber.number))
            }
            insertWhiteNumberLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumber ->
                handleSuccessNumberAction(String.format(getString(R.string.number_added),
                    whiteNumber.number))
            }
            deleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
                handleSuccessNumberAction(String.format(getString(R.string.delete_number_from_list),
                    args.number?.number.orEmpty()))
            }
        }
    }

    private fun handleSuccessNumberAction(message: String) {
        showMessage(message, false)
        (activity as MainActivity).apply {
            getAllData()
        }
        findNavController().popBackStack()
    }

}