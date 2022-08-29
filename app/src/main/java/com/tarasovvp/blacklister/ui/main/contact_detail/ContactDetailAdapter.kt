package com.tarasovvp.blacklister.ui.main.contact_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.databinding.DataBindingUtil
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemFilterBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Filter

class ContactDetailAdapter(
    var titleList: ArrayList<String>,
    var filterListMap: HashMap<String, List<Filter>>,
) :
    BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = titleList.size

    override fun getChildrenCount(groupPosition: Int): Int =
        filterListMap[titleList[groupPosition]]?.size.orZero()

    override fun getGroup(groupPosition: Int): String {
        return titleList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Filter? {
        return filterListMap[titleList[groupPosition]]?.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean = true

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
            DataBindingUtil.inflate<ItemHeaderBinding>(LayoutInflater.from(parent?.context),
                R.layout.item_header,
                parent,
                false).apply {
                itemHeaderText.text = titleList[groupPosition]
                root.isEnabled = filterListMap[titleList[groupPosition]].orEmpty().isEmpty().not()
                if (filterListMap[titleList[groupPosition]].orEmpty().isEmpty().not()) {
                    itemHeaderText.setCompoundDrawablesWithIntrinsicBounds(0,
                        0,
                        if (isExpanded) R.drawable.ic_drop_up else R.drawable.ic_drop_down,
                        0)
                }
                return root
            }
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        DataBindingUtil.inflate<ItemFilterBinding>(LayoutInflater.from(parent?.context),
            R.layout.item_filter, parent, false).apply {
            filterListMap[titleList[groupPosition]]?.get(childPosition)?.let { filter ->
                this.filter = filter
            }
            return root
        }
    }
}