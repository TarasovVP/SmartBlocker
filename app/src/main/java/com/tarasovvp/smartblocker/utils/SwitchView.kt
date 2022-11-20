package com.tarasovvp.smartblocker.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ViewSwitchBinding
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.showPopUpWindow

class SwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewSwitchBinding? = null

    init {
        binding = ViewSwitchBinding.inflate(LayoutInflater.from(context), this, true)
        var infoText: String
        binding?.apply {
            with(context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchView, 0, 0)) {
                viewSwitchTitle.text = getString(R.styleable.SwitchView_title)
                viewSwitchOffMode.text = getString(R.styleable.SwitchView_turnOffModeText)
                viewSwitchOffModeIcon.setImageResource(getResourceId(R.styleable.SwitchView_turnOffModeIcon,
                    0))
                viewSwitchOnMode.text = getString(R.styleable.SwitchView_turnOnModeText)
                viewSwitchOnModeIcon.setImageResource(getResourceId(R.styleable.SwitchView_turnOnModeIcon,
                    0))
                infoText = getString(R.styleable.SwitchView_info).orEmpty()
                recycle()
            }
            //TODO complete
            viewSwitchInfo.setSafeOnClickListener {
                if (infoText.isNotEmpty() && binding?.root?.isEnabled.isTrue()) viewSwitchInfo.showPopUpWindow(
                    Info.INFO_SETTINGS)
            }
        }
    }

    fun setSwitchClickListener(switchClickListener: (Boolean) -> Unit) {
        binding?.root?.setSafeOnClickListener {
            switchClickListener.invoke(binding?.viewSwitchSwitcher?.isChecked.isTrue())
        }
    }

    fun setSwitchChange(isChecked: Boolean) {
        binding?.apply {
            viewSwitchSwitcher.isChecked = isChecked
            viewSwitchOffMode.alpha = if (isChecked) 0.5f else 1f
            viewSwitchOffModeIcon.alpha = if (isChecked) 0.5f else 1f
            viewSwitchOnMode.alpha = if (isChecked) 1f else 0.5f
            viewSwitchOnModeIcon.alpha = if (isChecked) 1f else 0.5f
        }
    }

    fun setEnableChange(isEnable: Boolean) {
        binding?.root?.isEnabled = isEnable
    }
}