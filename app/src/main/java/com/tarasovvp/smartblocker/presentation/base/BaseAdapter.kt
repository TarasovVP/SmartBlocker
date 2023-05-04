package com.tarasovvp.smartblocker.presentation.base

import android.os.Parcelable
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_DATA_TYPE
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.domain.models.HeaderData
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import kotlinx.parcelize.Parcelize

abstract class BaseAdapter<D : NumberDataUIModel> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData: ArrayList<NumberDataUIModel>? = null

    override fun getItemViewType(position: Int): Int {
        return if (mData?.get(position) is HeaderDataUIModel) HEADER_TYPE else NUMBER_DATA_TYPE
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

    fun setHeaderAndData(dataList: List<D>, header: HeaderData) {
        mData = mData ?: ArrayList()
        if (header.header.isNotEmpty()) mData?.add(header)
        mData?.addAll(dataList as Collection<NumberDataUIModel>)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getDataInPosition(position: Int): D {
        return mData?.get(position) as D
    }

    protected fun getHeaderDataInPosition(position: Int): HeaderData {
        return mData?.get(position) as HeaderData
    }

    fun clearData() {
        mData?.clear()
    }

    @Parcelize
    open class HeaderDataUIModel : NumberDataUIModel(), Parcelable

}
