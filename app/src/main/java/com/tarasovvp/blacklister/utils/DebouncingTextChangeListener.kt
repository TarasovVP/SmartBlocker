package com.tarasovvp.blacklister.utils

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.tarasovvp.blacklister.extensions.EMPTY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DebouncingTextChangeListener(
    lifecycle: Lifecycle,
    var mask: String = String.EMPTY,
    private val onDebouncingTextChange: (String?) -> Unit,
) : TextWatcher {
    private var debouncePeriod: Long = 500
    private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null

    override fun beforeTextChanged(
        charSequence: CharSequence?,
        start: Int,
        count: Int,
        after: Int,
    ) {
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            editable?.let {
                delay(debouncePeriod)
                onDebouncingTextChange(it.toString())
            }
        }
    }
}