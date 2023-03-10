package com.tarasovvp.smartblocker.ui.main.number.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemCallBinding
import com.tarasovvp.smartblocker.databinding.ItemContactBinding
import com.tarasovvp.smartblocker.databinding.ItemFilterBinding
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.models.*

class NumberDataAdapter(
    var numberDataList: ArrayList<NumberData>? = null,
    private val numberDataClick: (NumberData) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isFilteredCallDetails: Boolean = false
    var isFilteredCallItemDisable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ContactWithFilter::class.java.simpleName.hashCode() -> ContactViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false)
            )
            LogCallWithFilter::class.java.simpleName.hashCode(),
            FilteredCallWithFilter::class.java.simpleName.hashCode(),
            CallWithFilter::class.java.simpleName.hashCode() -> CallViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_call, parent, false)
            )
            else -> FilterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_filter, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return numberDataList?.get(position)?.let { it::class.java.simpleName.hashCode() }.orZero()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val numberData = numberDataList?.get(position)
        when (holder) {
            is FilterViewHolder -> holder.bindData(numberData as? FilterWithCountryCode)
            is ContactViewHolder -> holder.bindData(numberData as? ContactWithFilter)
            is CallViewHolder -> holder.bindData(numberData as? CallWithFilter)
        }
    }

    internal inner class FilterViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemFilterBinding? = DataBindingUtil.bind(itemView)
        fun bindData(filterWithCountryCode: FilterWithCountryCode?) {
            binding?.apply {
                this.filterWithCountryCode = filterWithCountryCode?.apply {
                    highlightedSpanned =
                        highlightedSpanned ?: filterWithCountryCode.highlightedSpanned(filterWithCountryCode.filter, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                }
                itemFilterContainer.strokeColor = ContextCompat.getColor(
                    root.context,
                    if (adapterPosition == 0) filterWithCountryCode?.filter?.filterTypeTint() ?: R.color.transparent
                    else R.color.transparent)
                root.setSafeOnClickListener {
                    filterWithCountryCode?.let { it1 -> numberDataClick.invoke(it1) }
                }
                executePendingBindings()
            }
        }
    }

    internal inner class ContactViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemContactBinding? = DataBindingUtil.bind(itemView)
        fun bindData(contactWithFilter: ContactWithFilter?) {
            binding?.apply {
                this.contactWithFilter = contactWithFilter
                root.setSafeOnClickListener {
                    contactWithFilter?.let { it1 -> numberDataClick.invoke(it1.apply {
                        searchText = String.EMPTY
                        highlightedSpanned = contact?.number.highlightedSpanned(String.EMPTY, null, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                    }) }
                }
                executePendingBindings()
            }
        }
    }

    internal inner class CallViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemCallBinding? = DataBindingUtil.bind(itemView)
        fun bindData(callWithFilter: CallWithFilter?) {
            binding?.apply {
                root.isEnabled = isFilteredCallItemDisable.not()
                this.callWithFilter = callWithFilter
                this.callWithFilter?.call?.isExtract = isFilteredCallDetails.not()
                this.callWithFilter?.call?.isFilteredCallDetails = isFilteredCallDetails
                this.callWithFilter?.highlightedSpanned = this.callWithFilter?.highlightedSpanned
                    ?: this.callWithFilter?.call?.number.highlightedSpanned(String.EMPTY, null, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                root.setSafeOnClickListener {
                    this.callWithFilter?.apply {
                        searchText = String.EMPTY
                        highlightedSpanned = call?.number.highlightedSpanned(String.EMPTY, null, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                        numberDataClick.invoke(this)
                    }
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemCount() = numberDataList?.size.orZero()
}