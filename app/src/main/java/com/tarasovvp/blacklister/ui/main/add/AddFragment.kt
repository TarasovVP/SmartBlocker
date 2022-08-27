package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tarasovvp.blacklister.databinding.FragmentAddContainerBinding
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment

class AddFragment : BaseBindingFragment<FragmentAddContainerBinding>() {

    override fun getViewBinding() = FragmentAddContainerBinding.inflate(layoutInflater)
    private val args: AddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        binding?.addViewPager?.adapter = adapter
        binding?.addViewPager?.let { viewPager ->
            binding?.addTabLayout?.let { tabLayout ->
                TabLayoutMediator(tabLayout, viewPager) { _, _ ->
                }.attach()
            }
            viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                }
            })
        }
    }

}