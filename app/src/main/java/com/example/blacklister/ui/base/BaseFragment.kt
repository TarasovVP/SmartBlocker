package com.example.blacklister.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.blacklister.R
import com.example.blacklister.ui.MainActivity

abstract class BaseFragment<VB : ViewBinding, T : ViewModel> : Fragment() {

    protected open var binding: VB? = null
    abstract fun getViewBinding(): VB

    abstract val viewModelClass: Class<T>

    abstract fun observeLiveData()

    protected open val viewModel: T by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[viewModelClass]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).apply {
            if (findNavController().currentDestination?.id != R.id.infoDialog) {
                bottomNavigationView?.isVisible = navigationScreens.contains(findNavController().currentDestination?.id)
                toolbar?.isVisible = navigationScreens.contains(findNavController().currentDestination?.id)
            }
            toolbar?.navigationIcon = ContextCompat.getDrawable(
                context,
                if (findNavController().currentDestination?.id == R.id.callLogListFragment) R.drawable.ic_arrow_transparent else R.drawable.ic_arrow_back
            )
            toolbar?.setNavigationOnClickListener {
                if (findNavController().currentDestination?.id == R.id.callLogListFragment) return@setNavigationOnClickListener
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        observeLiveData()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}