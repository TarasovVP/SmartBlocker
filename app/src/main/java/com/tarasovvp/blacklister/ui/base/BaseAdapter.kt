package com.tarasovvp.blacklister.ui.base

import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.model.HeaderDataItem

abstract class BaseAdapter<D : BaseAdapter.NumberData> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mData: ArrayList<NumberData>? = null

    override fun getItemViewType(position: Int): Int {
        return if (mData?.get(position) is HeaderData) {
            (mData?.get(position) as HeaderData).headerType
        } else 0
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is BaseAdapter<*>.HeaderViewHolder) {
            holder.bindData(
                position
            )
        }
    }

    internal inner class HeaderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            Log.e("adapterTAG", "BaseAdapter bindData position $position itemView $itemView")
            DataBindingUtil.bind<ItemHeaderBinding>(itemView)?.headerDataItem =
                getHeaderDataInPosition(position)
        }

    }

    override fun getItemCount(): Int {
        return if (mData.isNull()) { 0 } else {
            mData?.size ?: 0
        }
    }

    fun setHeaderAndData(dataList: List<D>, header: HeaderData) {
        mData = mData ?: ArrayList()
        mData?.add(header)
        mData?.addAll(dataList as Collection<NumberData>)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getDataInPosition(position: Int): D {
        return mData?.get(position) as D
    }

    protected fun getHeaderDataInPosition(position: Int): HeaderDataItem {
        return mData?.get(position) as HeaderDataItem
    }

    fun clearData() {
        mData?.clear()
    }

    interface HeaderData : NumberData {
        val headerType: Int
    }

    interface NumberData
}
