package com.tarasovvp.smartblocker.utils.searchable

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.models.CountryCode
import java.util.*

class SearchableDialog(
    var context: Context,
    var items: List<CountryCode>,
    var listener: (item: CountryCode, position: Int) -> Unit
) {
    private lateinit var alertDialog: AlertDialog
    private var position: Int = 0
    private var selected: CountryCode? = null


    lateinit var searchListAdapter: SearchableListAdapter
    lateinit var listView: ListView

    fun show() {
        val adb = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.searchable_dialog, null)
        listView = view.findViewById(R.id.list) as ListView

        val searchBox = view.findViewById(R.id.searchBox) as EditText
        searchListAdapter = SearchableListAdapter(context, items)
        listView.adapter = searchListAdapter
        adb.setView(view)
        alertDialog = adb.create()
        alertDialog.setCancelable(true)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, v, _, _ ->
            val t = v.findViewById<TextView>(R.id.item_call_number)
            for (j in items.indices) {
                if (t.text.toString().equals(items[j].countryEmoji(), ignoreCase = true)) {
                    position = j
                    selected = items[position]
                }
            }
            try {
                selected?.let { listener(it, position) }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            alertDialog.dismiss()
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val filteredValues = arrayListOf<CountryCode>()
                for (i in items.indices) {
                    val item = items[i]
                    if (item.countryEmoji().lowercase(Locale.getDefault()).trim { it <= ' ' }
                            .contains(searchBox.text.toString().lowercase(Locale.getDefault())
                                .trim { it <= ' ' })) {
                        filteredValues.add(item)
                    }
                }
                searchListAdapter = SearchableListAdapter(context, filteredValues)
                listView.adapter = searchListAdapter
            }
        })
        alertDialog.show()
    }
}
