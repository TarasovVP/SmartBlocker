package com.tarasovvp.blacklister.ui.number_data

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.ItemCallSmallBinding
import com.tarasovvp.blacklister.databinding.ItemContactBinding
import com.tarasovvp.blacklister.databinding.ItemFilterBinding
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDataAdapter(
    var numberDataList: ArrayList<NumberData>? = null,
    private val numberDataClick: (NumberData) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var searchQueryMap = Pair(String.EMPTY, String.EMPTY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.e("filterAddTAG",
            "NumberDataAdapter onCreateViewHolder viewType $viewType numberDataList?.size ${numberDataList?.size}")
        return when (viewType) {
            Contact::class.java.simpleName.hashCode() -> ContactViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false)
            )
            Filter::class.java.simpleName.hashCode() -> FilterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_filter, parent, false)
            )
            else -> CallViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_call_small, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return numberDataList?.get(position)?.let { it::class.java.simpleName.hashCode() }.orZero()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val numberData = numberDataList?.get(position)
        when (holder) {
            is FilterViewHolder -> holder.bindData(numberData as? Filter)
            is ContactViewHolder -> holder.bindData(numberData as? Contact)
            is CallViewHolder -> holder.bindData(numberData as? Call)
        }
    }

    internal inner class FilterViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemFilterBinding? = DataBindingUtil.bind(itemView)
        fun bindData(filter: Filter?) {
            binding?.apply {
                binding?.filter = filter
                this.filter?.searchText = searchQueryMap.first
                itemFilterContainer.setBackgroundColor(ContextCompat.getColor(
                    root.context, when {
                        filter?.filterAction == FilterAction.FILTER_ACTION_CHANGE && adapterPosition == 0 -> R.color.change_bg
                        filter?.filterAction == FilterAction.FILTER_ACTION_DELETE && adapterPosition == 0 -> R.color.delete_bg
                        filter?.filterType == BLACK_FILTER && adapterPosition == 0 -> R.color.delete_bg
                        filter?.filterType == WHITE_FILTER && adapterPosition == 0 -> R.color.white_bg
                        else -> R.color.white
                    }))
                filter?.searchText = if (filter?.isTypeContain().isTrue())
                    searchQueryMap.first else String.format("%s%s",
                    searchQueryMap.second,
                    searchQueryMap.first)
                root.setSafeOnClickListener {
                    filter?.let { it1 -> numberDataClick.invoke(it1) }
                }
                executePendingBindings()
            }
        }
    }

    internal inner class ContactViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemContactBinding? = DataBindingUtil.bind(itemView)
        fun bindData(contact: Contact?) {
            binding?.apply {
                this.contact = contact
                this.contact?.searchText = searchQueryMap.first
                root.setSafeOnClickListener {
                    contact?.let { it1 -> numberDataClick.invoke(it1) }
                }
                executePendingBindings()
            }
        }
    }

    internal inner class CallViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemCallSmallBinding? = DataBindingUtil.bind(itemView)
        fun bindData(call: Call?) {
            binding?.apply {
                this.call = call
                this.call?.searchText = searchQueryMap.first
                root.setSafeOnClickListener {
                    call?.let { it1 -> numberDataClick.invoke(it1) }
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemCount() = numberDataList?.size.orZero()
}