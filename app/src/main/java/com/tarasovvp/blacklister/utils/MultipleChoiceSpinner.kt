package com.tarasovvp.blacklister.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.tarasovvp.blacklister.R

class MultipleChoiceSpinner(context: Context, attrs: AttributeSet?) : AppCompatSpinner(context, attrs),
    OnMultiChoiceClickListener,
    DialogInterface.OnCancelListener {
    private var items: List<String>? = null
    var selected: BooleanArray = booleanArrayOf()
    private var defaultText: String? = null
    private var listener: MultiSpinnerListener? = null

    override fun onClick(dialog: DialogInterface, position: Int, isChecked: Boolean) {
        selected[position] = isChecked
    }

    override fun onCancel(dialog: DialogInterface) {
        val spinnerBuffer = StringBuffer()
        items?.forEachIndexed { index, s ->
            if (selected[index]) {
                spinnerBuffer.append(s)
                spinnerBuffer.append(", ")
            }
        }

        var spinnerText: String?
        if (selected.contains(true)) {
            spinnerText = spinnerBuffer.toString()
            if (spinnerText.length > 2) spinnerText =
                spinnerText.substring(0, spinnerText.length - 2)
        } else {
            spinnerText = defaultText
        }
        val adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_item, arrayOf(spinnerText))
        setAdapter(adapter)
        listener?.onItemsSelected(selected)
    }

    override fun performClick(): Boolean {
        super.performClick()
        val builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(
            items?.toTypedArray<CharSequence>(), selected, this)
        builder.setPositiveButton(R.string.ok
        ) { dialog, _ -> dialog.cancel() }
        builder.setOnCancelListener(this)
        builder.show()
        return true
    }

    fun setItems(
        items: List<String>,
        defaultTitle: String,
        listener: MultiSpinnerListener,
    ) {
        this.items = items
        this.listener = listener
        defaultText = defaultTitle
        selected = BooleanArray(items.size)
        val adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_item, arrayOf(defaultTitle))
        setAdapter(adapter)
    }

    interface MultiSpinnerListener {
        fun onItemsSelected(selected: BooleanArray)
    }
}