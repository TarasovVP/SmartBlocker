package com.tarasovvp.smartblocker.utils.searchable

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.models.CountryCode
import java.util.*
import kotlin.collections.ArrayList

class SearchableListAdapter(context: Context, objects: List<CountryCode>) :
    ArrayAdapter<CountryCode>(context, R.layout.item_country_code) {
    var countryCodeList: MutableList<CountryCode> = objects as MutableList<CountryCode>
    var suggestions: MutableList<CountryCode> = ArrayList()
    private var filter = CustomFilter()

    override fun getCount(): Int {
        return countryCodeList.size
    }

    override fun getItem(i: Int): CountryCode {
        return countryCodeList[i]
    }

    override fun getItemId(i: Int): Long {
        return countryCodeList[i].hashCode().toLong()
    }

    override fun getView(i: Int, view: View?, parent: ViewGroup): View {
        var inflateview = view
        if (inflateview == null) {
            inflateview = LayoutInflater.from(context).inflate(R.layout.item_country_code, parent, false)
        }
        val tv = inflateview!!.findViewById<View>(R.id.item_call_number) as TextView
        tv.text = countryCodeList[i].countryEmoji()
        return inflateview
    }

    override fun getFilter(): Filter {
        return filter
    }

    inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            suggestions.clear()
            constraint.let {
                for (i in countryCodeList.indices) {
                    if (countryCodeList[i].countryEmoji().lowercase(Locale.ENGLISH).contains(constraint)) {
                        suggestions.add(countryCodeList[i])
                    }
                }
            }
            val results = FilterResults()
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

}