package com.tarasovvp.blacklister.ui.main.filter_add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemContactBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Contact

class ContactByFilterAdapter(
    private var titleList: ArrayList<String>,
    private var contactListMap: HashMap<String, List<Contact>>,
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
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent?.context))
        binding.itemHeaderText.text = titleList[groupPosition]
        binding.root.isEnabled = contactListMap[titleList[groupPosition]].orEmpty().isEmpty().not()
        if (contactListMap[titleList[groupPosition]].orEmpty().isEmpty().not()) {
            binding.itemHeaderText.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                if (isExpanded) R.drawable.ic_drop_up else R.drawable.ic_drop_down,
                0)
        }
        return binding.root
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        ItemContactBinding.inflate(LayoutInflater.from(parent?.context)).apply {
            contactListMap[titleList[groupPosition]]?.get(childPosition)?.let { contact ->
                itemContactAvatar.loadCircleImage(contact.photoUrl)
                itemContactName.text = contact.name
                itemContactNumber.text = contact.phone
            }
            return root
        }
    }
}