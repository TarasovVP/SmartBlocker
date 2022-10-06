package com.tarasovvp.blacklister.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
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
        var isAllFabsVisible = false
        binding?.apply {
            fabNew.setSafeOnClickListener {
                fabNew.setImageResource(if (isAllFabsVisible) R.drawable.ic_add else R.drawable.ic_close)
                isAllFabsVisible = if (isAllFabsVisible) {
                    fabContain.hide()
                    fabStart.hide()
                    fabFull.hide()
                    false
                } else {
                    fabContain.show()
                    fabStart.show()
                    fabFull.show()
                    true
                }
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