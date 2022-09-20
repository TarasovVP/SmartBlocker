package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentAddBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment

class ContainerAddFragment : BaseBindingFragment<FragmentAddBinding>() {

    override var layoutId = R.layout.fragment_add
    private val args: ContainerAddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("toolbarTAG", "AddFragment onViewCreated")
        setToolbar()
        initViewPager()
        setFragmentResultListener(Constants.CHANGE_FILTER) { _, _ ->
            args.filter?.isBlackFilter = args.filter?.isBlackFilter.isTrue().not()
            setToolbar()
            initViewPager()
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).apply {
            toolbar?.apply {
                title = if (args.filter?.isBlackFilter.isTrue()) getString(R.string.black_list) else getString(R.string.white_list)
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context, if (args.filter?.isBlackFilter.isTrue()) R.drawable.ic_white_filter else R.drawable.ic_black_filter)
                    setOnMenuItemClickListener {
                        findNavController().navigate(ContainerAddFragmentDirections.startChangeFilterDialog(args.filter))
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }

    private fun initViewPager() {
        val fragmentList = arrayListOf(
            FullNumberAddFragment(args.filter),
            FilterAddFragment(args.filter)
        )

        val adapter = activity?.supportFragmentManager?.let { fragmentManager ->
            AddAdapter(
                fragmentList,
                fragmentManager,
                lifecycle
            )
        }
        binding?.apply {
            addViewPager.isUserInputEnabled = false
            addViewPager.adapter = adapter
            addRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.add_full_number -> addViewPager.setCurrentItem(0, false)
                    R.id.add_filter -> addViewPager.setCurrentItem(1, false)
                }
            }
            Log.e("validTAG", "AddFragment getUserCountry ${args.filter?.filter.getPhoneNumber(context?.getUserCountry().orEmpty())} isValidNumber ${args.filter?.filter.isValidPhoneNumber(context?.getUserCountry().orEmpty())}")
            if (args.filter?.filter.isValidPhoneNumber(context?.getUserCountry().orEmpty()).not()) {
                addViewPager.setCurrentItem(R.id.add_filter, false)
                addRadioGroup.check(R.id.add_filter)
            }
        }
    }
}