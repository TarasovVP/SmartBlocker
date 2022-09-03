package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentAddBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment


class AddFragment : BaseBindingFragment<FragmentAddBinding>() {

    override var layoutId = R.layout.fragment_add
    private val args: AddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("toolbarTAG", "AddFragment onViewCreated")
        (activity as MainActivity).apply {
            toolbar?.apply {
                title = if (args.filter?.isBlackFilter.isTrue()) getString(R.string.black_list) else getString(
                    R.string.white_list)
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context, if (args.filter?.isBlackFilter.isTrue()) R.drawable.ic_black_filter else R.drawable.ic_white_filter)
                    setOnMenuItemClickListener {
                        args.filter?.isBlackFilter = args.filter?.isBlackFilter.isTrue().not()
                        title = if (args.filter?.isBlackFilter.isTrue()) getString(R.string.black_list) else getString(
                            R.string.white_list)
                        icon = ContextCompat.getDrawable(context, if (args.filter?.isBlackFilter.isTrue()) R.drawable.ic_black_filter else R.drawable.ic_white_filter)
                        initViewPager()
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
        initViewPager()
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

        binding?.addViewPager?.isUserInputEnabled = false
        binding?.addViewPager?.adapter = adapter
        binding?.addRadioGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.add_full_number -> binding?.addViewPager?.setCurrentItem(0, false)
                R.id.add_filter -> binding?.addViewPager?.setCurrentItem(1, false)
            }
        }
    }
}