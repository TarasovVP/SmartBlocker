package com.tarasovvp.smartblocker.ui.base

import android.os.Parcelable
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.constants.Constants.NUMBER_DATA_TYPE
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.models.HeaderDataItem
import com.tarasovvp.smartblocker.models.NumberData
import kotlinx.parcelize.Parcelize

abstract class BaseAdapter<D : NumberData> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData: ArrayList<NumberData>? = null

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
            DataBindingUtil.bind<ItemHeaderBinding>(itemView)?.headerDataItem =
                getHeaderDataInPosition(position)
        }

    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    fun setHeaderAndData(dataList: List<D>, header: HeaderDataItem) {
        mData = mData ?: ArrayList()
        if (header.header.isNotEmpty()) mData?.add(header)
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

}
