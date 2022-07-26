package com.tarasovvp.blacklister.ui.main.numberdetail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.ADD_TO_LIST
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.databinding.ItemNumberBinding
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.numberadd.NumberAddFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDetailFragment : BaseFragment<FragmentNumberDetailBinding, NumberDetailViewModel>() {

    override fun getViewBinding() = FragmentNumberDetailBinding.inflate(layoutInflater)

    override val viewModelClass = NumberDetailViewModel::class.java

    private val args: NumberDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPriority()
        args.number?.let { number ->
            if (number.isEmpty()) {
                viewModel.numberDetailLiveData.postValue(Contact(name = getString(R.string.hidden)))
            } else {
                viewModel.getContact(number)
            }
            viewModel.getBlackNumberList(number)
            viewModel.getWhiteNumberList(number)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDetailLiveData.safeSingleObserve(viewLifecycleOwner) { contact ->
                setContactInfo(contact)
            }
            blackNumberLiveData.safeSingleObserve(viewLifecycleOwner) { blackNumberList ->
                setNumberList(blackNumberList, true)
            }
            whiteNumberLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumberList ->
                setNumberList(whiteNumberList, false)
            }
        }
    }

    private fun setPriority() {
        binding?.numberDetailPriority?.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (SharedPreferencesUtil.isWhiteListPriority) R.drawable.ic_white_number else R.drawable.ic_black_number, 0)
        binding?.numberDetailPriority?.setSafeOnClickListener {
            findNavController().navigate(NumberAddFragmentDirections.startBlockSettingsFragment())
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.apply {
            numberDetailName.text = contact.name
            numberDetailPhone.text = contact.phone
            numberDetailAvatar.loadCircleImage(contact.photoUrl)
            numberDetailType.setImageResource(when {
                contact.isBlackList -> R.drawable.ic_block
                contact.isWhiteList -> R.drawable.ic_accepted
                else -> 0
            })
            numberDetailPriority.text = String.format(getString(R.string.prioritness),
                if (SharedPreferencesUtil.isWhiteListPriority) getString(R.string.white_list) else getString(
                    R.string.black_list))
            numberDetailAddFilter.setSafeOnClickListener {
                findNavController().navigate(NumberDetailFragmentDirections.startAddToListDialog())
            }
        }
        setFragmentResultListener(ADD_TO_LIST) { _, bundle ->
            findNavController().navigate(NumberDetailFragmentDirections.startNumberAddFragment(
                number = Number(number = contact.phone.orEmpty()).apply {
                    isBlackNumber = bundle.getBoolean(WHITE_LIST).not()
                }))
        }
    }

    private fun setNumberList(numberList: List<Number>, isBlackList: Boolean) {
        if (isBlackList) {
            binding?.numberDetailBlackListEmpty?.isVisible = numberList.isEmpty()
        } else {
            binding?.numberDetailWhiteListEmpty?.isVisible = numberList.isEmpty()
        }
        numberList.forEach { number ->
            val itemNumber = ItemNumberBinding.inflate(layoutInflater)
            itemNumber.itemNumberAvatar.setImageResource(if (number.isBlackNumber) R.drawable.ic_black_number else R.drawable.ic_white_number)
            itemNumber.itemNumberValue.text = number.number
            itemNumber.itemNumberStart.isVisible = number.start
            itemNumber.itemNumberContain.isVisible = number.contain
            itemNumber.itemNumberEnd.isVisible = number.end
            itemNumber.root.setSafeOnClickListener {
                findNavController().navigate(NumberDetailFragmentDirections.startNumberAddFragment(
                    number = number))
            }
            if (number.isBlackNumber) {
                binding?.numberDetailBlackNumberList?.addView(itemNumber.root)
            } else {
                binding?.numberDetailWhiteNumberList?.addView(itemNumber.root)
            }
        }
    }
}