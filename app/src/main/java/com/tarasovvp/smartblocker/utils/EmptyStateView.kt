package com.tarasovvp.smartblocker.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ViewEmptyStateBinding
import com.tarasovvp.smartblocker.utils.extensions.EMPTY

class EmptyStateView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) :
    ConstraintLayout(context, attrs, defStyle) {
        private var binding: ViewEmptyStateBinding? = null

        init {
            binding = ViewEmptyStateBinding.inflate(LayoutInflater.from(context), this, true)
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView)
            val descriptionRes = a.getResourceId(R.styleable.EmptyStateView_description, 0)
            binding?.emptyStateDescription?.text =
                if (descriptionRes > 0) context.getString(descriptionRes) else String.EMPTY
            a.recycle()
            visibility = GONE
        }

        fun setDescription(descriptionRes: Int) {
            binding?.emptyStateDescription?.text =
                if (descriptionRes > 0) context.getString(descriptionRes) else String.EMPTY
        }
    }
