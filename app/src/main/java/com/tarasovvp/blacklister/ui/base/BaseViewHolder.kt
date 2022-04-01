package com.tarasovvp.blacklister.ui.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T : Any>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T)

    @CallSuper
    open fun bind(item: T, param: Any) {
        bind(item)
    }
}