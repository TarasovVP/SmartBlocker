package com.tarasovvp.smartblocker.utils.mask

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.MASK_CHAR
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SPACE
import com.tarasovvp.smartblocker.utils.extensions.*

class MaskedEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatEditText(context, attrs), TextWatcher {

    private var focusChangeListener: OnFocusChangeListener? = null

    private var mask: String? = String.EMPTY
    private var rawToMask: IntArray? = null
    private var rawText: RawText? = null
    private var editingBefore = false
    private var editingOnChanged = false
    private var editingAfter = false
    private var maskToRaw: IntArray? = null
    private var selection1 = 0
    private var initialized = false
    private var ignore = false
    private var maxRawLength = 0
    private var lastValidMaskPosition = 0
    private var selectionChanged = false
    private var allowedChars: String? = null

    init {
        setOnEditorActionListener(OnEditorActionListener { _: TextView?, action: Int, _: KeyEvent? ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                this.hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })
        background = ContextCompat.getDrawable(context, R.drawable.bg_rounded)
        allowedChars = "1234567890"
        addTextChangedListener(this)
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, type)
    }

    override fun setOnFocusChangeListener(listener: OnFocusChangeListener) {
        focusChangeListener = listener
    }

    fun setNumberMask(mask: String) {
        this.mask = mask
        if (mask.isNotBlank()) hint = mask
        cleanUp()
    }

    fun getRawText(): String {
        return rawText?.text.orEmpty()
    }

    private fun cleanUp() {
        initialized = false
        if (mask.isNullOrEmpty()) {
            return
        }
        generatePositionArrays()
        if (rawText.isNull()) {
            rawText = RawText()
            selection1 = rawToMask?.get(0).orZero()
        }
        editingBefore = true
        editingOnChanged = true
        editingAfter = true
        if (hint.isNotNull() && rawText?.length() == 0) {
            this.setText(makeMaskedTextWithHint())
        } else {
            this.setText(makeMaskedText())
        }
        editingBefore = false
        editingOnChanged = false
        editingAfter = false
        maxRawLength = maskToRaw?.get(previousValidPosition(mask?.length.orZero() - 1)).orZero() + 1
        lastValidMaskPosition = findLastValidMaskPosition()
        initialized = true
        super.setOnFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (focusChangeListener.isNotNull()) {
                focusChangeListener?.onFocusChange(v, hasFocus)
            }
            if (hasFocus()) {
                selectionChanged = false
                this@MaskedEditText.setSelection(lastValidPosition())
            }
        }
    }

    private fun findLastValidMaskPosition(): Int {
        maskToRaw?.apply {
            for (i in indices.reversed()) {
                if (maskToRaw?.get(i) notEquals -1) return i
            }
        }
        throw RuntimeException("Mask must contain at least one representation char")
    }

    private fun generatePositionArrays() {
        val aux = IntArray(mask?.length.orZero())
        maskToRaw = IntArray(mask?.length.orZero())
        var charsInMaskAux = String.EMPTY
        var charIndex = 0
        for (i in mask.orEmpty().indices) {
            val currentChar = mask?.get(i)
            if (currentChar == MASK_CHAR) {
                aux[charIndex] = i
                maskToRaw?.let {
                    it[i] = charIndex++
                }
            } else {
                val charAsString = currentChar.toString()
                if (charsInMaskAux.contains(charAsString).not()) {
                    charsInMaskAux += charAsString
                }
                maskToRaw?.let {
                    it[i] = -1
                }
            }
        }
        if (charsInMaskAux.indexOf(' ') < 0) {
            charsInMaskAux += SPACE
        }
        rawToMask = IntArray(charIndex)
        rawToMask?.let { System.arraycopy(aux, 0, it, 0, charIndex) }
    }

    private fun erasingStart(start1: Int): Int {
        var start = start1
        while (start > 0 && maskToRaw?.get(start) == -1) {
            start--
        }
        return start
    }

    override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int,
        after: Int,
    ) {
        if (mask.isNullOrEmpty().not()) {
            if (editingBefore.not()) {
                editingBefore = true
                if (start > lastValidMaskPosition) {
                    ignore = true
                }
                var rangeStart = start
                if (after == 0) {
                    rangeStart = erasingStart(start)
                }
                val range = calculateRange(rangeStart, start + count)
                if (range.start notEquals -1) {
                    rawText?.subtractFromString(range)
                }
                if (count > 0) {
                    selection1 = previousValidPosition(start)
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count1: Int) {
        if (mask.isNullOrEmpty().not()) {
            var count = count1
            if (editingOnChanged.not() && editingBefore) {
                editingOnChanged = true
                if (ignore) {
                    return
                }
                if (count > 0) {
                    val startingPosition = maskToRaw?.get(nextValidPosition(start)).orZero()
                    val addedString = s.subSequence(start, start + count).toString()
                    count = rawText?.addToString(clear(addedString), startingPosition, maxRawLength).orZero()
                    if (initialized) {
                        val currentPosition: Int =
                            if (startingPosition + count < rawToMask?.size.orZero()) rawToMask?.get(
                                startingPosition + count).orZero() else lastValidMaskPosition + 1
                        selection1 = nextValidPosition(currentPosition)
                    }
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        if (mask.isNullOrEmpty().not()) {
            if (editingAfter.not() && editingBefore && editingOnChanged) {
                editingAfter = true
                if (hint.isNotNull()) {
                    setText(makeMaskedTextWithHint())
                } else {
                    setText(makeMaskedText())
                }
                selectionChanged = false
                setSelection(selection1)
                editingBefore = false
                editingOnChanged = false
                editingAfter = false
                ignore = false
            }
        }
    }

    override fun onSelectionChanged(selStart1: Int, selEnd1: Int) {
        var selStart = selStart1
        var selEnd = selEnd1
        if (initialized) {
            if (selectionChanged.not()) {
                selStart = fixSelection(selStart)
                selEnd = fixSelection(selEnd)
                if (selStart > text.toString().length) selStart = text.toString().length
                if (selStart < 0) selStart = 0
                if (selEnd > text.toString().length) selEnd = text.toString().length
                if (selEnd < 0) selEnd = 0
                setSelection(selStart, selEnd)
                selectionChanged = true
            } else {
                if (selStart > rawText?.length().orZero() - 1) {
                    val start = fixSelection(selStart)
                    val end = fixSelection(selEnd)
                    if (start >= 0 && end < text?.length.orZero()) {
                        setSelection(start, end)
                    }
                }
            }
        }
        super.onSelectionChanged(selStart, selEnd)
    }

    private fun fixSelection(selection: Int): Int {
        return if (selection > lastValidPosition()) {
            lastValidPosition()
        } else {
            nextValidPosition(selection)
        }
    }

    private fun nextValidPosition(currentPosition1: Int): Int {
        var currentPosition = currentPosition1
        while (currentPosition < lastValidMaskPosition && maskToRaw?.get(currentPosition) == -1) {
            currentPosition++
        }
        return if (currentPosition > lastValidMaskPosition) lastValidMaskPosition + 1 else currentPosition
    }

    private fun previousValidPosition(currentPosition1: Int): Int {
        var currentPosition = currentPosition1
        while (currentPosition >= 0 && maskToRaw?.get(currentPosition) == -1) {
            currentPosition--
            if (currentPosition < 0) {
                return nextValidPosition(0)
            }
        }
        return currentPosition
    }

    private fun lastValidPosition(): Int {
        return if (rawText?.length() == maxRawLength) {
            rawToMask?.get(rawText?.length().orZero() - 1).orZero() + 1
        } else nextValidPosition(rawToMask?.get(rawText?.length().orZero()).orZero())
    }

    private fun makeMaskedText(): String {
        val maskedTextLength: Int = if (rawText?.length().orZero() < rawToMask?.size.orZero()) {
            rawToMask?.get(rawText?.length().orZero()).orZero()
        } else {
            mask?.length.orZero()
        }
        val maskedText = CharArray(maskedTextLength)
        for (i in maskedText.indices) {
            val rawIndex = maskToRaw?.get(i).orZero()
            if (rawIndex == -1) {
                mask?.get(i)?.let { maskedText[i] = it }
            } else {
                rawText?.charAt(rawIndex)?.let {
                    maskedText[i] = it
                }
            }
        }
        return String(maskedText)
    }

    private fun makeMaskedTextWithHint(): CharSequence {
        val ssb = SpannableStringBuilder()
        var position: Int
        mask?.forEachIndexed { index, char ->
            position = maskToRaw?.get(index).orZero()
            if (position notEquals -1) {
                if (position < rawText?.length().orZero()) {
                    rawText?.charAt(position)?.let { ssb.append(it) }
                } else {
                    hint?.get(index)?.let { ssb.append(it) }
                }
            } else {
                ssb.append(char)
            }
            if (rawText?.length()
                    .orZero() < rawToMask?.size.orZero() && index >= rawToMask?.get(rawText?.length()
                    .orZero()).orZero()
            ) {
                ssb.setSpan(ForegroundColorSpan(currentHintTextColor), index, index + 1, 0)
            }
        }
        return ssb
    }

    private fun calculateRange(start: Int, end: Int): Range {
        val range = Range()
        var i = start
        while (i <= end && i < mask?.length.orZero()) {
            if (maskToRaw?.get(i) notEquals -1) {
                if (range.start == -1) {
                    range.start = maskToRaw?.get(i).orZero()
                }
                range.end = maskToRaw?.get(i).orZero()
            }
            i++
        }
        if (end == mask?.length.orZero()) {
            range.end = rawText?.length().orZero()
        }
        if (range.start == range.end && start < end) {
            val newStart = previousValidPosition(range.start - 1)
            if (newStart < range.start) {
                range.start = newStart
            }
        }
        return range
    }

    private fun clear(string1: String): String {
        var string = string1
        if (allowedChars.isNotNull()) {
            val builder = StringBuilder(string.length)
            for (c in string.toCharArray()) {
                if (allowedChars.orEmpty().contains(c.toString())) {
                    builder.append(c)
                }
            }
            string = builder.toString()
        }
        return string
    }
}