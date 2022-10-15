package com.tarasovvp.blacklister.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.tarasovvp.blacklister.extensions.EMPTY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


internal class DebouncingTextChangeListener(
    lifecycle: Lifecycle,
    var mask: String = String.EMPTY,
    private val onDebouncingTextChange: (String?) -> Unit
) : TextWatcher {
    private var debouncePeriod: Long = 500
    private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null
    private var isRunning = false
    private var isDeleting = false

    override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleting = count > after;
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            editable?.let {
                delay(debouncePeriod)
                Log.e("filterAddTAG",
                    "DebouncingTextChangeListener afterTextChanged isRunning $isRunning isDeleting $isDeleting mask $mask editable $editable")
                if (mask.isEmpty()) {
                    onDebouncingTextChange(it.toString())
                } else {
                    if (isRunning || isDeleting) {
                        return@launch
                    }
                    isRunning = true
                    val editableLength: Int = it.length
                    if (editableLength < mask.length) {
                        if (mask[editableLength] != '#') {
                            it.append(mask[editableLength])
                        } else if (mask[editableLength - 1] != '#') {
                            it.insert(editableLength - 1, mask, editableLength - 1, editableLength)
                        }
                    }
                    onDebouncingTextChange(it.toString())
                    isRunning = false
                }
            }
        }
    }
}