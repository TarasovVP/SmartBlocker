package com.tarasovvp.blacklister.ui.main.numberdetail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.databinding.ItemNumberBinding
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDetailFragment : BaseFragment<FragmentNumberDetailBinding, NumberDetailViewModel>() {

    override fun getViewBinding() = FragmentNumberDetailBinding.inflate(layoutInflater)

    override val viewModelClass = NumberDetailViewModel::class.java

    private val args: NumberDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.number?.let { number ->
            viewModel.getContact(number)
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
                setBlackNumberList(blackNumberList)
            }
            whiteNumberLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumberList ->
                setWhiteNumberList(whiteNumberList)
            }
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.numberDetailName?.text = contact.name
        binding?.numberDetailPhone?.text = contact.phone
        binding?.numberDetailAvatar?.loadCircleImage(contact.photoUrl)
        binding?.numberDetailType?.setImageResource(when {
            contact.isBlackList -> R.drawable.ic_block
            contact.isWhiteList -> R.drawable.ic_accepted
            else -> 0
        })
        binding?.numberDetailPriority?.text = String.format(getString(R.string.prioritness),
            if (SharedPreferencesUtil.isWhiteListPriority) getString(R.string.white_list) else getString(
                R.string.black_list))
    }

    private fun setBlackNumberList(blackNumberList: List<BlackNumber>) {
        binding?.numberDetailBlackListTitle?.isVisible = blackNumberList.isNullOrEmpty().not()
        blackNumberList.forEach { blackNumber ->
            val itemNumber = ItemNumberBinding.inflate(layoutInflater)
            itemNumber.itemNumberAvatar.setImageResource(R.drawable.ic_black_number)
            itemNumber.itemNumberValue.text = blackNumber.number
            itemNumber.itemNumberStart.isVisible = blackNumber.start
            itemNumber.itemNumberContain.isVisible = blackNumber.contain
            itemNumber.itemNumberEnd.isVisible = blackNumber.end
            itemNumber.root.setSafeOnClickListener {
                findNavController().navigate(NumberDetailFragmentDirections.startNumberAddFragment(
                    blackNumber = blackNumber))
            }
            binding?.numberDetailBlackNumberList?.addView(itemNumber.root)
        }
    }

    private fun setWhiteNumberList(whiteNumberList: List<WhiteNumber>) {
        binding?.numberDetailWhiteListTitle?.isVisible = whiteNumberList.isNullOrEmpty().not()
        whiteNumberList.forEach { whiteNumber ->
            val itemNumber = ItemNumberBinding.inflate(layoutInflater)
            itemNumber.itemNumberAvatar.setImageResource(R.drawable.ic_white_number)
            itemNumber.itemNumberValue.text = whiteNumber.number
            itemNumber.itemNumberStart.isVisible = whiteNumber.start
            itemNumber.itemNumberContain.isVisible = whiteNumber.contain
            itemNumber.itemNumberEnd.isVisible = whiteNumber.end
            itemNumber.root.setSafeOnClickListener {
                findNavController().navigate(NumberDetailFragmentDirections.startNumberAddFragment(
                    whiteNumber = whiteNumber))
            }
            binding?.numberDetailWhiteNumberList?.addView(itemNumber.root)
        }
    }
}