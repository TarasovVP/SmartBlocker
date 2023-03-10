package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemCountryCodeBinding
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.local.DataStorePrefsImpl
import com.tarasovvp.smartblocker.models.CountryCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CountryCodeSearchAdapter(
    var countryCodeList: List<CountryCode>? = null,
    private val countryCodeClick: (CountryCode) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CountryCodeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_country_code, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val countryCode = countryCodeList?.get(position)
        (holder as CountryCodeViewHolder).bindData(countryCode)
    }

    override fun getItemCount() = countryCodeList?.size.orZero()

    internal inner class CountryCodeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemCountryCodeBinding? = DataBindingUtil.bind(itemView)
        fun bindData(countryCode: CountryCode?) {
            binding?.countryCode = countryCode
            CoroutineScope(Dispatchers.IO).launch {
                val dataStorePrefsImpl = DataStorePrefsImpl(itemView.context)
                binding?.itemCountryCodeName?.text = countryCode?.countryNameEmoji(dataStorePrefsImpl.getAppLang().first().orEmpty())
            }
            binding?.root?.setSafeOnClickListener {
                countryCode?.let { it1 -> countryCodeClick.invoke(it1) }
            }
            binding?.executePendingBindings()
        }
    }
}