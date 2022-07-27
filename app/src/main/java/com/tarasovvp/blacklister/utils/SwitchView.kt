package com.tarasovvp.blacklister.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ViewSwitchBinding
import com.tarasovvp.blacklister.extensions.isTrue

class SwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewSwitchBinding? = null

    init {
        binding = ViewSwitchBinding.inflate(LayoutInflater.from(context), this, true)
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchView, 0, 0)
        binding?.viewSwitchTitle?.text = typedArray.getString(R.styleable.SwitchView_title)
        binding?.viewSwitchOffMode?.text = typedArray.getString(R.styleable.SwitchView_turnOffModeText)
        binding?.viewSwitchOffModeIcon?.setImageResource(typedArray.getResourceId(R.styleable.SwitchView_turnOffModeIcon, 0))
        binding?.viewSwitchOnMode?.text = typedArray.getString(R.styleable.SwitchView_turnOnModeText)
        binding?.viewSwitchOnModeIcon?.setImageResource(typedArray.getResourceId(R.styleable.SwitchView_turnOnModeIcon, 0))
        binding?.viewSwitchInfo?.text = typedArray.getString(R.styleable.SwitchView_info)
        typedArray.recycle()
    }

    fun setClickListener(switchClickListener: (Boolean) -> Unit) {
        binding?.root?.setSafeOnClickListener {
            switchClickListener.invoke(binding?.viewSwitchSwitcher?.isChecked.isTrue())
        }
    }

    fun setSwitchChange(isChecked: Boolean) {
        binding?.viewSwitchSwitcher?.isChecked = isChecked
        binding?.viewSwitchOffMode?.alpha = if (isChecked) 0.5f else 1f
        binding?.viewSwitchOffModeIcon?.alpha = if (isChecked) 0.5f else 1f
        binding?.viewSwitchOnMode?.alpha = if (isChecked) 1f else 0.5f
        binding?.viewSwitchOnModeIcon?.alpha = if (isChecked) 1f else 0.5f
    }
}