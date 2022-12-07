package com.tarasovvp.smartblocker.ui.number_data

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
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class NumberDataAdapter(
    var numberDataList: ArrayList<NumberData>? = null,
    private val numberDataClick: (NumberData) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isFilteredCallDetails: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                    .inflate(R.layout.item_call, parent, false)
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
                this.filter = filter?.apply {
                    highlightedSpanned = highlightedSpanned ?: filter.filter.highlightedSpanned( String.EMPTY, null)
                }
                itemFilterContainer.strokeColor = ContextCompat.getColor(
                    root.context, when {
                        filter?.isChangeFilterAction().isTrue() && adapterPosition == 0 -> filter?.filterAction?.color ?: R.color.transparent
                        filter?.isDeleteFilterAction().isTrue() && adapterPosition == 0 -> filter?.filterAction?.color ?: R.color.transparent
                        else -> R.color.transparent
                    })
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
                root.setSafeOnClickListener {
                    contact?.let { it1 -> numberDataClick.invoke(it1.apply {
                        searchText = String.EMPTY
                        highlightedSpanned = number.highlightedSpanned( String.EMPTY, null)
                    }) }
                }
                executePendingBindings()
            }
        }
    }

    internal inner class CallViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemCallBinding? = DataBindingUtil.bind(itemView)
        fun bindData(call: Call?) {
            binding?.apply {
                this.call = call
                this.call?.isExtract = isFilteredCallDetails.not()
                this.call?.isFilteredCallDetails = isFilteredCallDetails
                this.call?.highlightedSpanned = this.call?.highlightedSpanned ?: this.call?.number.highlightedSpanned(String.EMPTY, null)
                root.setSafeOnClickListener {
                    call?.let { it1 -> numberDataClick.invoke(it1.apply {
                        searchText = String.EMPTY
                        highlightedSpanned = number.highlightedSpanned( String.EMPTY, null)
                    }) }
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemCount() = numberDataList?.size.orZero()
}