package com.tarasovvp.blacklister.ui.main.number_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.ADD_TO_LIST
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.number_add.NumberAddFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDetailFragment : BaseFragment<FragmentNumberDetailBinding, NumberDetailViewModel>() {

    override fun getViewBinding() = FragmentNumberDetailBinding.inflate(layoutInflater)

    override val viewModelClass = NumberDetailViewModel::class.java

    private val args: NumberDetailFragmentArgs by navArgs()

    private var expandableNumberAdapter: NumberDetailAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPriority()
        args.number?.let { number ->
            if (number.isEmpty()) {
                viewModel.numberDetailLiveData.postValue(Contact(name = getString(R.string.hidden)))
            } else {
                viewModel.getContact(number.filter { it.isDigit() || it == PLUS_CHAR })
            }
            viewModel.getBlackNumberList(number.filter { it.isDigit() || it == PLUS_CHAR })
            viewModel.getWhiteNumberList(number.filter { it.isDigit() || it == PLUS_CHAR })
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
        binding?.numberDetailPriority?.setCompoundDrawablesWithIntrinsicBounds(0,
            0,
            if (SharedPreferencesUtil.isWhiteListPriority) R.drawable.ic_white_number else R.drawable.ic_black_number,
            0)
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
                number = Number(number = contact.trimmedPhone).apply {
                    isBlackNumber = bundle.getBoolean(WHITE_LIST).not()
                }))
        }
    }

    private fun setNumberList(numberList: List<Number>, isBlackList: Boolean) {
        val title =
            "Найдено ${numberList.size} фильтров из ${if (isBlackList) "черного списка" else "белого списка"}"
        if (expandableNumberAdapter.isNotNull()) {
            expandableNumberAdapter?.titleList?.add(title)
            expandableNumberAdapter?.numberListMap?.put(title, numberList)
        } else {
            expandableNumberAdapter =
                NumberDetailAdapter(arrayListOf(title), hashMapOf(title to numberList))
            binding?.numberDetailNumberList?.setAdapter(expandableNumberAdapter)
            binding?.numberDetailNumberList?.setOnChildClickListener { _, _, _, childPosition, _ ->
                findNavController().navigate(NumberDetailFragmentDirections.startNumberAddFragment(
                    number = numberList[childPosition]))
                return@setOnChildClickListener true
            }
        }
    }
}