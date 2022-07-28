package com.tarasovvp.blacklister.ui.main.numberdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.core.view.isVisible
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.databinding.ItemNumberBinding
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.model.Number

class NumberDetailAdapter(
    var titleList: ArrayList<String>,
    var numberListMap: HashMap<String, List<Number>>,
) :
    BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = titleList.size

    override fun getChildrenCount(groupPosition: Int): Int =
        numberListMap[titleList[groupPosition]]?.size.orZero()

    override fun getGroup(groupPosition: Int): String {
        return titleList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Number? {
        return numberListMap[titleList[groupPosition]]?.get(childPosition)
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
        binding.root.isEnabled = numberListMap[titleList[groupPosition]].orEmpty().isEmpty().not()
        if (numberListMap[titleList[groupPosition]].orEmpty().isEmpty().not()) {
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
        ItemNumberBinding.inflate(LayoutInflater.from(parent?.context)).apply {
            numberListMap[titleList[groupPosition]]?.get(childPosition)?.let { number ->
                itemNumberAvatar.setImageResource(if (number.isBlackNumber) R.drawable.ic_black_number else R.drawable.ic_white_number)
                itemNumberValue.text = number.number
                itemNumberStart.isVisible = number.start
                itemNumberContain.isVisible = number.contain
                itemNumberEnd.isVisible = number.end
                number.number = number.number.filter { it.isDigit() || it == Constants.PLUS_CHAR }
            }
            return root
        }
    }
}