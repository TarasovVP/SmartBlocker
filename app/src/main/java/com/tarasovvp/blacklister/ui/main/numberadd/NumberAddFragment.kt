package com.tarasovvp.blacklister.ui.main.numberadd

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentNumberAddBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.WhiteNumber
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
            args.blackNumber?.let {
                findNavController().navigate(NumberAddFragmentDirections.startDeleteNumberDialog(blackNumber = it))
            }
            args.whiteNumber?.let {
                findNavController().navigate(NumberAddFragmentDirections.startDeleteNumberDialog(whiteNumber = it))
            }
        }
        binding?.numberAddSubmit?.setSafeOnClickListener {
            if (args.blackNumber.isNotNull()) {
                viewModel.insertBlackNumber(BlackNumber(number = binding?.numberAddSearch?.text.toString(),
                    start = binding?.numberAddStart?.isChecked.isTrue(),
                    contain = binding?.numberAddContain?.isChecked.isTrue(),
                    end = binding?.numberAddEnd?.isChecked.isTrue()))
            } else {
                viewModel.insertWhiteNumber(WhiteNumber(number = binding?.numberAddSearch?.text.toString(),
                    start = binding?.numberAddStart?.isChecked.isTrue(),
                    contain = binding?.numberAddContain?.isChecked.isTrue(),
                    end = binding?.numberAddEnd?.isChecked.isTrue()))
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(DELETE_NUMBER)?.safeSingleObserve(viewLifecycleOwner) { blackNumber ->
            args.blackNumber?.let {
                viewModel.deleteBlackNumber(it)
            }
            args.whiteNumber?.let {
                viewModel.deleteWhiteNumber(it)
            }
        }
    }

    private fun initViewsWithData() {
        val text = String.format(getString(R.string.fill_data_press_button),
            if (args.blackNumber?.number.isNullOrEmpty()
                    .not() || args.whiteNumber?.number.isNullOrEmpty().not()
            ) getString(R.string.edit_number) else getString(R.string.add_number),
            if (args.blackNumber.isNotNull()) getString(R.string.black_list) else getString(R.string.white_list))
        binding?.numberAddTitle?.text = text
        binding?.numberAddTitle?.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
            if (args.blackNumber.isNotNull()) R.drawable.ic_black_number else R.drawable.ic_white_number,
            0,
            0)
        binding?.numberAddSearch?.setText(if (args.blackNumber.isNotNull()) args.blackNumber?.number.orEmpty() else args.whiteNumber?.number.orEmpty())
        binding?.numberAddStart?.isChecked =
            if (args.blackNumber.isNotNull()) args.blackNumber?.start.isTrue() else args.whiteNumber?.start.isTrue()
        binding?.numberAddContain?.isChecked =
            if (args.blackNumber.isNotNull()) args.blackNumber?.contain.isTrue() else args.whiteNumber?.contain.isTrue()
        binding?.numberAddEnd?.isChecked =
            if (args.blackNumber.isNotNull()) args.blackNumber?.end.isTrue() else args.whiteNumber?.end.isTrue()
        binding?.numberAddSubmit?.isEnabled =
            if (args.blackNumber.isNotNull()) args.blackNumber?.number.isNullOrEmpty().isTrue()
                .not() else args.whiteNumber?.number.isNullOrEmpty().isTrue().not()
    }

    override fun observeLiveData() {
        with(viewModel) {
            insertBlackNumberLiveData.safeSingleObserve(viewLifecycleOwner, { blackNumber ->
                showMessage(String.format(getString(R.string.number_added),
                    blackNumber.number), false)
                findNavController().popBackStack()
            })
            insertWhiteNumberLiveData.safeSingleObserve(viewLifecycleOwner, { whiteNumber ->
                showMessage(String.format(getString(R.string.number_added),
                    whiteNumber.number), false)
                findNavController().popBackStack()
            })
        }
    }

}