package com.tarasovvp.blacklister.utils

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DebouncingTextChangeListener(lifecycle: Lifecycle, private val onDebouncingTextChange: (String?) -> Unit) :  TextWatcher{
    private var debouncePeriod: Long = 500
    private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            p0?.let {
                delay(debouncePeriod)
                onDebouncingTextChange(it.toString())
            }
        }
    }
}