package com.tarasovvp.smartblocker.presentation.main.number.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemContactBinding
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class CreateFilterAdapter(
    var contactWithFilterUIModels: ArrayList<ContactWithFilterUIModel>? = null,
    private val contactWithFilterClick: (ContactWithFilterUIModel) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false),
        )
    }

    override fun getItemViewType(position: Int): Int {
        return contactWithFilterUIModels?.get(position)
            ?.let { it::class.java.simpleName.hashCode() }.orZero()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        (holder as ContactViewHolder).bindData(contactWithFilterUIModels?.get(position))
    }

    internal inner class ContactViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemContactBinding? = DataBindingUtil.bind(itemView)

        fun bindData(contactWithFilter: ContactWithFilterUIModel?) {
            binding?.apply {
                this.contactWithFilter = contactWithFilter
                this.contactWithFilter?.highlightedSpanned = contactWithFilter?.highlightedSpanned
                    ?: this.contactWithFilter?.highlightedSpanned(
                        filterWithFilteredNumberUIModel,
                        ContextCompat.getColor(itemView.context, R.color.text_color_black),
                    )
                root.setSafeOnClickListener {
                    contactWithFilter?.let { it1 ->
                        contactWithFilterClick.invoke(
                            it1.apply {
                                highlightedSpanned =
                                    number.highlightedSpanned(
                                        String.EMPTY,
                                        null,
                                        ContextCompat.getColor(itemView.context, R.color.text_color_black),
                                    )
                            },
                        )
                    }
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemCount() = contactWithFilterUIModels?.size.orZero()
}
