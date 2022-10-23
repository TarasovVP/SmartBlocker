package com.tarasovvp.blacklister.ui.main.call_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemExpandableHeaderBinding
import com.tarasovvp.blacklister.databinding.ItemFilterSmallBinding
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Filter

class CallDetailAdapter(
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
        val rootView = convertView
            ?: DataBindingUtil.inflate<ItemExpandableHeaderBinding>(LayoutInflater.from(parent?.context),
                R.layout.item_expandable_header,
                parent,
                false).root
        rootView.findViewById<TextView>(R.id.item_expandable_header_text).apply {
            text = titleList[groupPosition]
            if (filterListMap[text].orEmpty().isNotEmpty()) {
                setCompoundDrawablesWithIntrinsicBounds(0,
                    0,
                    if (isExpanded) R.drawable.ic_drop_up else R.drawable.ic_drop_down,
                    0)
            }
        }
        return rootView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        return convertView
            ?: DataBindingUtil.inflate<ItemFilterSmallBinding>(LayoutInflater.from(parent?.context),
                R.layout.item_filter_small,
                parent,
                false).apply {
                filterListMap[titleList[groupPosition]]?.get(childPosition)?.let { filter ->
                    this.filter = filter
                }
            }.root
    }
}