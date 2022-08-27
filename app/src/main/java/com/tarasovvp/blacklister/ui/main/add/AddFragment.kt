package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentAddBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment

class AddFragment : BaseBindingFragment<FragmentAddBinding>() {

    override fun getViewBinding() = FragmentAddBinding.inflate(layoutInflater)
    private val args: AddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).toolbar?.title = if (args.filter?.isBlackFilter.isTrue()) getString(R.string.black_list) else getString(R.string.white_list)
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
            when(checkedId) {
                R.id.add_full_number -> binding?.addViewPager?.setCurrentItem(0, false)
                R.id.add_filter -> binding?.addViewPager?.setCurrentItem(1, false)
            }
        }
    }

}