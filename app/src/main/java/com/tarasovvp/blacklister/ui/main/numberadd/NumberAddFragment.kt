package com.tarasovvp.blacklister.ui.main.numberadd

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentNumberAddBinding
import com.tarasovvp.blacklister.enum.BlackNumberCategory
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.orZero
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
        args.blackNumber?.apply {
            initNumberAddCategory()
        }
        initViewsWithData()
        binding?.numberAddSearch?.doAfterTextChanged {
            binding?.numberAddSubmit?.isEnabled = it?.isNotEmpty().isTrue()
        }
        binding?.numberAddSubmit?.setSafeOnClickListener {
            if (args.blackNumber.isNotNull()) {
                viewModel.insertBlackNumber(BlackNumber(blackNumber = binding?.numberAddSearch?.text.toString(),
                    isStart = binding?.numberAddStart?.isChecked.isTrue(),
                    isContain = binding?.numberAddContain?.isChecked.isTrue(),
                    isEnd = binding?.numberAddEnd?.isChecked.isTrue(),
                    category = binding?.numberAddCategory?.selectedItemPosition.orZero()))
            } else {
                viewModel.insertWhiteNumber(WhiteNumber(whiteNumber = binding?.numberAddSearch?.text.toString(),
                    isStart = binding?.numberAddStart?.isChecked.isTrue(),
                    isContain = binding?.numberAddContain?.isChecked.isTrue(),
                    isEnd = binding?.numberAddEnd?.isChecked.isTrue()))
            }
        }
    }

    private fun initViewsWithData() {
        binding?.numberAddTitle?.text = "Для ${if (args.blackNumber?.blackNumber.isNullOrEmpty().not() || args.whiteNumber?.whiteNumber.isNullOrEmpty().not()) "редактирования номера" else "добавления номера в ${if (args.blackNumber.isNotNull()) "черный" else "белый"} список"} заполните поле ввода и нажмите кнопку"
        binding?.numberAddTitle?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, if (args.blackNumber.isNotNull()) R.drawable.ic_black_number else R.drawable.ic_white_number, 0, 0)
        binding?.numberAddSearch?.setText(if (args.blackNumber.isNotNull()) args.blackNumber?.blackNumber.orEmpty() else args.whiteNumber?.whiteNumber.orEmpty())
        binding?.numberAddStart?.isChecked =
            if (args.blackNumber.isNotNull()) args.blackNumber?.isStart.isTrue() else args.whiteNumber?.isStart.isTrue()
        binding?.numberAddContain?.isChecked =
            if (args.blackNumber.isNotNull()) args.blackNumber?.isContain.isTrue() else args.whiteNumber?.isContain.isTrue()
        binding?.numberAddEnd?.isChecked =
            if (args.blackNumber.isNotNull()) args.blackNumber?.isEnd.isTrue() else args.whiteNumber?.isEnd.isTrue()
        binding?.numberAddSubmit?.isEnabled =
            if (args.blackNumber.isNotNull()) args.blackNumber?.blackNumber.isNullOrEmpty().isTrue()
                .not() else args.whiteNumber?.whiteNumber.isNullOrEmpty().isTrue().not()
    }

    private fun initNumberAddCategory() {
        binding?.numberAddCategory?.isVisible = true
        val categoryList = BlackNumberCategory.values().map {
            getString(it.title)
        }
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, categoryList)
        binding?.numberAddCategory?.adapter = adapter
        binding?.numberAddCategory?.setSelection(args.blackNumber?.category.orZero())
    }

    override fun observeLiveData() {
        viewModel.blackNumberLiveData.safeSingleObserve(viewLifecycleOwner, {
            showMessage("Number ${it.blackNumber} is added", false)
            Log.e("blackNumberTAG",
                "BlackNumberAddFragment blackNumberLiveData.safeSingleObserve blackNumber ${
                    Gson().toJson(it)
                }")
            findNavController().popBackStack()
        })
        viewModel.whiteNumberLiveData.safeSingleObserve(viewLifecycleOwner, {
            showMessage("Number ${it.whiteNumber} is added", false)
            Log.e("blackNumberTAG",
                "BlackNumberAddFragment blackNumberLiveData.safeSingleObserve blackNumber ${
                    Gson().toJson(it)
                }")
            findNavController().popBackStack()
        })
    }

}