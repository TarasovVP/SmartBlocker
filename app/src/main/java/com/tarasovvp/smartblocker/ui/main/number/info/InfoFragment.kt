package com.tarasovvp.smartblocker.ui.main.number.info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentInfoBinding
import com.tarasovvp.smartblocker.extensions.initWebView
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class InfoFragment :
    BaseBindingFragment<FragmentInfoBinding>() {

    override var layoutId = R.layout.fragment_info

    private val args: InfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            toolbar?.title = args.info?.title
            setProgressVisibility(true)
            args.info?.description?.let {
                binding?.infoWebView?.initWebView(it) {
                    setProgressVisibility(false)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }
}