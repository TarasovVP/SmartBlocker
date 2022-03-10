package com.example.blacklister.ui.numberlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.blacklister.databinding.ItemBlackNumberBinding
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseViewHolder

class BlackNumberAdapter(private val listener: BlackNumberClickListener) :
    BaseAdapter<BlackNumber, BaseViewHolder<BlackNumber>>(ArrayList()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackNumberViewHolder {
        val binding =
            ItemBlackNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlackNumberViewHolder(binding, listener)
    }
}