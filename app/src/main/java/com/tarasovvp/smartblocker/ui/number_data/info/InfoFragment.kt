package com.tarasovvp.smartblocker.ui.number_data.info

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentInfoBinding
import com.tarasovvp.smartblocker.extensions.initWebSettings
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class InfoFragment :
    BaseBindingFragment<FragmentInfoBinding>() {

    override var layoutId = R.layout.fragment_info

    private val args: InfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.infoWebView?.settings?.initWebSettings()
        binding?.infoWebView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        (activity as MainActivity).apply {
            toolbar?.title = args.info?.title
            binding?.infoWebView?.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    setProgressVisibility(true)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    setProgressVisibility(false)
                }
            }
        }
        args.info?.description?.let { binding?.infoWebView?.loadData(it, "text/html; charset=utf-8", "UTF-8") }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }
}