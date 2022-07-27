package com.tarasovvp.blacklister.ui.main.numberadd

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_LIST_PREVIEW
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentNumberAddBinding
import com.tarasovvp.blacklister.extensions.getViewsFromLayout
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberAddFragment : BaseFragment<FragmentNumberAddBinding, NumberAddViewModel>() {

    override fun getViewBinding() = FragmentNumberAddBinding.inflate(layoutInflater)

    override val viewModelClass = NumberAddViewModel::class.java

    private val args: NumberAddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).toolbar?.title =
            if (args.number?.isBlackNumber.isTrue()) getString(R.string.black_list) else getString(R.string.white_list)
        binding?.numberAddInput?.setText(args.number?.number.orEmpty())
        binding?.numberAddIcon?.setImageResource(if (args.number?.isBlackNumber.isTrue()) R.drawable.ic_black_number else R.drawable.ic_white_number)
        setPriority()
        initViewsWithData(args.number, false)
        setExistNumberChecking()
        setClickListeners()
        setFragmentResultListener(DELETE_NUMBER) { _, _ ->
            args.number?.let {
                viewModel.deleteNumber(it)
            }
        }
        setFragmentResultListener(BLACK_LIST_PREVIEW) { _, _ ->
            viewModel.insertNumber(getNumber())
        }
    }

    private fun setPriority() {
        binding?.numberAddPriority?.setCompoundDrawablesWithIntrinsicBounds(0,
            0,
            if (SharedPreferencesUtil.isWhiteListPriority) R.drawable.ic_white_number else R.drawable.ic_black_number,
            0)
        binding?.numberAddPriority?.setSafeOnClickListener {
            findNavController().navigate(NumberAddFragmentDirections.startBlockSettingsFragment())
        }
    }

    private fun setExistNumberChecking() {
        viewModel.checkNumberExist(args.number?.number.orEmpty(),
            args.number?.isBlackNumber.isTrue())
        binding?.numberAddInput?.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
            viewModel.checkNumberExist(it.toString(), args.number?.isBlackNumber.isTrue())
        })
    }

    private fun initViewsWithData(number: Number?, fromDb: Boolean) {
        binding?.apply {
            numberAddStart.isChecked = number?.start.isTrue()
            numberAddContain.isChecked = number?.contain.isTrue()
            numberAddEnd.isChecked = number?.end.isTrue()
            numberAddTitle.text =
                if (numberAddInput.text.isEmpty()) getString(R.string.add_filter_message) else String.format(
                    if (fromDb && number.isNotNull()) getString(R.string.edit_filter_with_number_message) else getString(
                        R.string.add_filter_with_number_message),
                    numberAddInput.text)
            numberAddSubmit.isVisible =
                numberAddInput.text.isNotEmpty() && isNumberIdentical(fromDb, number).not()
            numberAddSubmit.text =
                if (fromDb && number.isNotNull()) getString(R.string.edit) else getString(R.string.add)
            numberDeleteSubmit.isVisible = numberAddInput.text.isNotEmpty() && number.isNotNull()
            numberAddInfo.isVisible =
                numberAddInput.text.isNotEmpty() && isNumberIdentical(fromDb, number).not()
            if (numberAddInput.text.isNotEmpty() && isNumberIdentical(fromDb, number).not()) {
                viewModel.checkContactListByNumber(getNumber())
            }
            setCheckChangeListeners(fromDb, number)
        }
    }

    private fun setCheckChangeListeners(fromDb: Boolean, number: Number?) {
        binding?.apply {
            val checkChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
                numberAddSubmit.isVisible =
                    numberAddInput.text.isNotEmpty() && isNumberIdentical(fromDb, number).not()
                if (numberAddInput.text.isNotEmpty() && isNumberIdentical(fromDb, number).not()) {
                    viewModel.checkContactListByNumber(getNumber())
                }
            }
            container.getViewsFromLayout(CheckBox::class.java).forEach { checkBox ->
                checkBox.setOnCheckedChangeListener(checkChangeListener)
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            numberDeleteSubmit.setSafeOnClickListener {
                args.number?.let {
                    findNavController().navigate(NumberAddFragmentDirections.startDeleteNumberDialog(
                        number = it))
                }
            }
            numberAddSubmit.setSafeOnClickListener {
                viewModel.insertNumber(getNumber())
            }
        }
    }

    private fun isNumberIdentical(fromDb: Boolean, number: Number?): Boolean {
        return fromDb && number.isNotNull() && binding?.numberAddStart?.isChecked == number?.start && binding?.numberAddContain?.isChecked == number?.contain && binding?.numberAddEnd?.isChecked == number?.end
    }

    private fun getNumber(): Number {
        val number = if (args.number?.isBlackNumber.isTrue()) {
            BlackNumber(number = binding?.numberAddInput?.text.toString())
        } else {
            WhiteNumber(number = binding?.numberAddInput?.text.toString())
        }
        number.apply {
            start = binding?.numberAddStart?.isChecked.isTrue()
            contain = binding?.numberAddContain?.isChecked.isTrue()
            end = binding?.numberAddEnd?.isChecked.isTrue()
            isBlackNumber = args.number?.isBlackNumber.isTrue()
        }
        return number
    }

    override fun observeLiveData() {
        with(viewModel) {
            existNumberLiveData.observe(viewLifecycleOwner) { number ->
                initViewsWithData(number, true)
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                binding?.numberAddInfo?.isVisible = contactList.isNotEmpty()
                var numberAddInfoText = ""
                contactList.filterNot {
                    if (args.number?.isBlackNumber.isTrue()) it.isWhiteList && SharedPreferencesUtil.isWhiteListPriority else it.isBlackList && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (this.isNotEmpty()) numberAddInfoText += String.format(getString(R.string.block_add_info),
                        if (args.number?.isBlackNumber.isTrue()) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                contactList.filter {
                    if (args.number?.isBlackNumber.isTrue()) it.isWhiteList && SharedPreferencesUtil.isWhiteListPriority else it.isBlackList && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (numberAddInfoText.isNotEmpty()) numberAddInfoText += "\n"
                    if (this.isNotEmpty()) numberAddInfoText += String.format(getString(R.string.not_block_add_info),
                        if (args.number?.isBlackNumber.isTrue()) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                binding?.numberAddInfo?.text = numberAddInfoText
                binding?.numberAddInfo?.setSafeOnClickListener {
                    findNavController().navigate(NumberAddFragmentDirections.startBlackListPreviewDialog(
                        number = args.number,
                        contactList = contactList.toTypedArray()))
                }
            }
            insertNumberLiveData.safeSingleObserve(viewLifecycleOwner) { number ->
                handleSuccessNumberAction(String.format(getString(R.string.number_added), number))
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