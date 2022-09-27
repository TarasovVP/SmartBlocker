package com.tarasovvp.blacklister.ui.main.filter_add

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemContactSmallBinding
import com.tarasovvp.blacklister.databinding.ItemExpandableHeaderBinding
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Contact

class ContactByFilterAdapter(
    var titleList: ArrayList<String>,
    var contactListMap: LinkedHashMap<String, List<Contact>>
) :
    BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = titleList.size

    override fun getChildrenCount(groupPosition: Int): Int =
        contactListMap[titleList[groupPosition]]?.size.orZero()

    override fun getGroup(groupPosition: Int): String {
        return titleList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Contact? {
        return contactListMap[titleList[groupPosition]]?.get(childPosition)
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
            if (groupPosition < titleList.size) text = titleList[groupPosition]
            setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                if (isExpanded) if (groupPosition > 0) R.drawable.ic_drop_up else R.drawable.ic_close else if (groupPosition > 0) R.drawable.ic_drop_down else R.drawable.ic_info,
                0)
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
            ?: DataBindingUtil.inflate<ItemContactSmallBinding>(LayoutInflater.from(parent?.context),
                R.layout.item_contact_small,
                parent,
                false).apply {
                Log.e("filterAddTAG",
                    "ContactByFilterAdapter getChildView groupPosition $groupPosition contactListMap.size ${contactListMap.size}")
                if (groupPosition < titleList.size) contactListMap[titleList[groupPosition]]?.get(
                    childPosition)?.let { contact ->
                    this.contact = contact
                }
            }.root
    }
}