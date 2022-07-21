package com.tarasovvp.blacklister.ui.main.numberadd

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentNumberAddBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Number
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
        binding?.numberAddInput?.setText(args.number?.number.orEmpty())
        initViewsWithData(args.number, false)
        setExistNumberChecking()
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
            viewModel.insertNumber(Number(number = binding?.numberAddInput?.text.toString()).apply {
                start = binding?.numberAddStart?.isChecked.isTrue()
                contain = binding?.numberAddContain?.isChecked.isTrue()
                end = binding?.numberAddEnd?.isChecked.isTrue()
                isBlackNumber = args.number?.isBlackNumber.isTrue()
            })
        }
        setFragmentResultListener(DELETE_NUMBER) { _, _ ->
            args.number?.let {
                viewModel.deleteNumber(it)
            }
        }
    }

    private fun setExistNumberChecking() {
        viewModel.checkNumberExist(args.number?.number.orEmpty(),
            args.number?.isBlackNumber.isTrue())
        binding?.numberAddInput?.doAfterTextChanged {
            viewModel.checkNumberExist(it.toString(), args.number?.isBlackNumber.isTrue())
        }
    }

    private fun initViewsWithData(number: Number?, isFromDb: Boolean) {
        binding?.apply {
            numberAddTitle.text =
                if (numberAddInput.text.isEmpty()) getString(R.string.add_filter_message) else String.format(
                    if (isFromDb && number.isNotNull()) getString(R.string.edit_filter_with_number_message) else getString(
                        R.string.add_filter_with_number_message),
                    binding?.numberAddInput?.text)
            numberDeleteSubmit.isVisible = isFromDb && number.isNotNull()
            numberAddSubmit.text =
                if (isFromDb && number.isNotNull()) getString(R.string.edit) else getString(R.string.add)
            numberAddSubmit.isVisible = numberAddInput.text.isNotEmpty()
            numberAddStart.isChecked = number?.start.isTrue()
            numberAddContain.isChecked = number?.contain.isTrue()
            numberAddEnd.isChecked = number?.end.isTrue()
            numberAddTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                if (args.number?.isBlackNumber.isTrue()) R.drawable.ic_black_number else R.drawable.ic_white_number,
                0,
                0)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            existBlackNumberLiveData.observe(viewLifecycleOwner) { blackNumber ->
                initViewsWithData(blackNumber, true)
            }
            existWhiteNumberLiveData.observe(viewLifecycleOwner) { whiteNumber ->
                initViewsWithData(whiteNumber, true)
            }
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
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
        }
        findNavController().popBackStack()
    }

}