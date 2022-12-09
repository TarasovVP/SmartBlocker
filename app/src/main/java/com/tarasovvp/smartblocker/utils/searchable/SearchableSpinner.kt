package com.tarasovvp.smartblocker.utils.searchable

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ArrayAdapter
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.models.CountryCode

class SearchableSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : androidx.appcompat.widget.AppCompatSpinner(context, attrs, defStyleAttr) {
    var items: List<CountryCode> = mutableListOf()
        set(value) {
            val selectedIndex = this.selectedItem?.let {
                value.indexOfFirst { v -> v.countryEmoji() == it.countryEmoji() }
            } ?: -1
            field = value
            adapter =
                if (nothingSelectedText.isNullOrBlank() || selectedIndex >= 0)
                    ArrayAdapter(context, R.layout.item_layout, items.map { it.countryEmoji() })
                else {
                    val i = arrayListOf(nothingSelectedText)
                    i.addAll(items.map { it.countryEmoji() })
                    ArrayAdapter<String>(context, R.layout.item_layout, i
                    )
                }
            if (selectedIndex >= 0) {
                this.setSelection(selectedIndex)
            }
        }

    var nothingSelectedText: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (isSpinnerDialogOpen.not()) {
                isSpinnerDialogOpen = true
                if (event.action == MotionEvent.ACTION_UP) {
                    SearchableDialog(
                        context, items
                    ) { item, _ ->
                        adapter = ArrayAdapter(context, R.layout.item_layout, items.map { it.countryEmoji() })
                        setSelection(items.indexOf(item))
                    }.show()
                }
                return true
            }
            isSpinnerDialogOpen = false
        }
        Handler().postDelayed({ isSpinnerDialogOpen = false }, 500)
        return true
    }

    override fun getSelectedItem(): CountryCode? {
        if (this.selectedItemPosition < 0 || items.lastIndex < this.selectedItemPosition || items.lastIndex < 0 ||
            (adapter.isEmpty.not() && adapter.getItem(this.selectedItemPosition) == nothingSelectedText)
        ) {
            return null
        }
        return items[this.selectedItemPosition]
    }

    override fun getSelectedItemId(): Long {
        if (this.selectedItemPosition < 0 || items.lastIndex < this.selectedItemPosition || items.lastIndex < 0 ||
            (adapter.isEmpty.not() && adapter.getItem(this.selectedItemPosition) == nothingSelectedText)) {
            return -1L
        }
        return items[this.selectedItemPosition].hashCode().toLong()
    }

    override fun setSelection(position: Int) {
        if (position >= 0 && nothingSelectedText == adapter.getItem(0)) {
            this.adapter = ArrayAdapter(context, R.layout.item_layout, items.map { it.countryEmoji() })
        } else if (position < 0 && nothingSelectedText?.isNotBlank() == true) {
            val i = arrayListOf(nothingSelectedText)
            i.addAll(items.map { it.countryEmoji() })
            this.adapter = ArrayAdapter<String>(context, R.layout.item_layout, i)
        }
        super.setSelection(position)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        if (position >= 0 && nothingSelectedText == adapter.getItem(0)) {
            this.adapter = ArrayAdapter(context, R.layout.item_layout, items.map { it.countryEmoji() })
        } else if (position < 0 && nothingSelectedText?.isNotBlank() == true) {
            val i = arrayListOf(nothingSelectedText)
            i.addAll(items.map { it.countryEmoji() })
            this.adapter = ArrayAdapter<String>(context, R.layout.item_layout, i)
        }
        super.setSelection(position, animate)
    }

    companion object {
        var isSpinnerDialogOpen = false
    }

}