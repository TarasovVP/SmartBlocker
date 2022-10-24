package com.tarasovvp.blacklister.ui.main.filter_add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemContactFilterBinding
import com.tarasovvp.blacklister.enums.AddFilterState
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactFilterAdapter(
    var contactFilterList: ArrayList<BaseAdapter.MainData>? = null,
    private val contactClick: (BaseAdapter.MainData) -> Unit,
) :
    RecyclerView.Adapter<ContactFilterAdapter.ViewHolder>() {

    var searchQueryMap = Pair(String.EMPTY, String.EMPTY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate<ItemContactFilterBinding>(LayoutInflater.from(
            parent.context), R.layout.item_contact_filter, parent, false).root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val mainData = contactFilterList?.get(position)
        viewHolder.binding?.apply {
            if (mainData is Contact) {
                filter = null
                this.contact = mainData
                contact?.searchText = searchQueryMap.first
                root.setSafeOnClickListener {
                    contactClick.invoke(mainData)
                }
            } else if (mainData is Filter) {
                contact = null
                this.filter = mainData
                itemContactFilterContainer.setBackgroundColor(ContextCompat.getColor(
                    root.context, when {
                    filter?.addFilterState == AddFilterState.ADD_FILTER_CHANGE && position == 0 -> R.color.change_bg
                    filter?.addFilterState == AddFilterState.ADD_FILTER_DELETE && position == 0 -> R.color.delete_bg
                        else -> R.color.white
                    }))
                filter?.searchText = if (filter?.isTypeContain().isTrue())
                    searchQueryMap.first else String.format("%s%s",
                    searchQueryMap.second,
                    searchQueryMap.first)
                root.setSafeOnClickListener {
                    contactClick.invoke(mainData)
                }
            }
            executePendingBindings()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemContactFilterBinding? = null

        init {
            DataBindingUtil.bind<ItemContactFilterBinding>(itemView)?.let { binding = it }
        }
    }

    override fun getItemCount() = contactFilterList?.size.orZero()

}