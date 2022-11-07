package com.tarasovvp.blacklister.ui.base

import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.number_data.NumberData
import kotlinx.android.parcel.Parcelize

abstract class BaseAdapter<D : NumberData> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mData: ArrayList<NumberData>? = null

    override fun getItemViewType(position: Int): Int {
        return if (mData?.get(position) is HeaderData) HEADER_TYPE else NUMBER_DATA_TYPE
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
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
        return mData?.size ?: 0
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

    @Parcelize
    open class HeaderData : NumberData(), Parcelable

    companion object {
        const val NUMBER_DATA_TYPE = 0
        const val HEADER_TYPE = 1
    }
}
