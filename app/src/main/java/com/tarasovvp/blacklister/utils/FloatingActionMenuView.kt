package com.tarasovvp.blacklister.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ViewFloatingActionMenuBinding
import com.tarasovvp.blacklister.enums.Condition

class FloatingActionMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewFloatingActionMenuBinding? = null

    init {
        binding = ViewFloatingActionMenuBinding.inflate(LayoutInflater.from(context), this, true)
        binding?.apply {
            fabNew.setSafeOnClickListener {
                fabNew.setImageResource(if (fabFull.isVisible) R.drawable.ic_add else R.drawable.ic_close)
                if (fabFull.isVisible) fabFull.hide() else fabFull.show()
                if (fabStart.isVisible) fabStart.hide() else fabStart.show()
                if (fabContain.isVisible) fabContain.hide() else fabContain.show()
            }
        }
    }

    fun setFabClickListener(fabClickListener: (Int) -> Unit) {
        binding?.apply {
            fabFull.setSafeOnClickListener {
                fabClickListener.invoke(Condition.CONDITION_TYPE_FULL.index)
            }
            fabStart.setSafeOnClickListener {
                fabClickListener.invoke(Condition.CONDITION_TYPE_START.index)
            }
            fabContain.setSafeOnClickListener {
                fabClickListener.invoke(Condition.CONDITION_TYPE_CONTAIN.index)
            }
        }
    }
}