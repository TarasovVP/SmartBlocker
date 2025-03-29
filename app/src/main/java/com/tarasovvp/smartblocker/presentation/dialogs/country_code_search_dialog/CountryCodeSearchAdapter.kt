package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemCountryCodeBinding
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class CountryCodeSearchAdapter(
    var countryCodeList: List<CountryCodeUIModel>? = null,
    private val countryCodeClick: (CountryCodeUIModel) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return CountryCodeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_country_code, parent, false),
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val countryCode = countryCodeList?.get(position)
        (holder as CountryCodeViewHolder).bindData(countryCode)
    }

    override fun getItemCount() = countryCodeList?.size.orZero()

    internal inner class CountryCodeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemCountryCodeBinding? = DataBindingUtil.bind(itemView)

        fun bindData(countryCode: CountryCodeUIModel?) {
            binding?.countryCode = countryCode
            binding?.root?.setSafeOnClickListener {
                countryCode?.let { it1 -> countryCodeClick.invoke(it1) }
            }
        }
    }
}
