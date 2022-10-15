package com.tarasovvp.blacklister.utils.mask

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.AppCompatEditText
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.orZero

class MaskedEditText : AppCompatEditText, TextWatcher {
    private val onEditorActionListener =
        OnEditorActionListener { _: TextView?, _: Int, _: KeyEvent? -> true }
    var mask = String.EMPTY
    private var charRepresentation = 0.toChar()
    private var keepHint = false
    private var rawToMask: IntArray? = null
    var rawText: RawText? = null
    private var editingBefore = false
    private var editingOnChanged = false
    private var editingAfter = false
    private var maskToRaw: IntArray? = null
    private var selection1 = 0
    private var initialized = false
    private var ignore = false
    protected var maxRawLength = 0
    private var lastValidMaskPosition = 0
    private var selectionChanged = false
    private var focusChangeListener: OnFocusChangeListener? = null
    private var allowedChars: String? = null
    private var deniedChars: String? = null
    var isKeepingText = false
        private set

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MaskedEditText)
        mask = attributes.getString(R.styleable.MaskedEditText_mask).orEmpty()
        allowedChars = attributes.getString(R.styleable.MaskedEditText_allowed_chars)
        deniedChars = attributes.getString(R.styleable.MaskedEditText_denied_chars)
        val enableImeAction =
            attributes.getBoolean(R.styleable.MaskedEditText_enable_ime_action, false)
        val representation = attributes.getString(R.styleable.MaskedEditText_char_representation)
        charRepresentation = if (representation == null) {
            '#'
        } else {
            representation[0]
        }
        keepHint = attributes.getBoolean(R.styleable.MaskedEditText_keep_hint, false)
        cleanUp()
        if (!enableImeAction) {
            setOnEditorActionListener(onEditorActionListener)
        } else {
            setOnEditorActionListener(null)
        }
        attributes.recycle()
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, type)
    }

    override fun setOnFocusChangeListener(listener: OnFocusChangeListener) {
        focusChangeListener = listener
    }

    private fun cleanUp() {
        initialized = false
        if (mask.isEmpty()) {
            return
        }
        generatePositionArrays()
        if (isKeepingText.not() || rawText.isNull()) {
            rawText = RawText()
            selection1 = rawToMask?.get(0).orZero()
        }
        editingBefore = true
        editingOnChanged = true
        editingAfter = true
        if (hasHint() && rawText?.length() == 0) {
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
                if (maskToRaw?.get(i) != -1) return i
            }
        }
        throw RuntimeException("Mask must contain at least one representation char")
    }

    private fun hasHint(): Boolean {
        return hint != null
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }


    fun getRawText(): String {
        return String.format("%s%s", String.EMPTY, rawText!!.text)
    }

    fun getFormattedPhone(): String {
        return String.format("%s%s", "0", rawText!!.text)
    }

    private fun generatePositionArrays() {
        val aux = IntArray(mask.length)
        maskToRaw = IntArray(mask.length)
        var charsInMaskAux = String.EMPTY
        var charIndex = 0
        for (i in mask.indices) {
            val currentChar = mask[i]
            if (currentChar == charRepresentation) {
                aux[charIndex] = i
                maskToRaw!![i] = charIndex++
            } else {
                val charAsString = currentChar.toString()
                if (!charsInMaskAux.contains(charAsString)) {
                    charsInMaskAux += charAsString
                }
                maskToRaw!![i] = -1
            }
        }
        if (charsInMaskAux.indexOf(' ') < 0) {
            charsInMaskAux += SPACE
        }
        rawToMask = IntArray(charIndex)
        System.arraycopy(aux, 0, rawToMask, 0, charIndex)
    }

    private fun init() {
        addTextChangedListener(this)
    }

    override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int,
        after: Int,
    ) {
        if (!editingBefore) {
            editingBefore = true
            if (start > lastValidMaskPosition) {
                ignore = true
            }
            var rangeStart = start
            if (after == 0) {
                rangeStart = erasingStart(start)
            }
            val range = calculateRange(rangeStart, start + count)
            if (range.start != -1) {
                rawText!!.subtractFromString(range)
            }
            if (count > 0) {
                selection1 = previousValidPosition(start)
            }
        }
    }

    private fun erasingStart(start1: Int): Int {
        var start = start1
        while (start > 0 && maskToRaw!![start] == -1) {
            start--
        }
        return start
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count1: Int) {
        var count = count1
        if (!editingOnChanged && editingBefore) {
            editingOnChanged = true
            if (ignore) {
                return
            }
            if (count > 0) {
                val startingPosition = maskToRaw!![nextValidPosition(start)]
                val addedString = s.subSequence(start, start + count).toString()
                count = rawText!!.addToString(clear(addedString), startingPosition, maxRawLength)
                if (initialized) {
                    val currentPosition: Int =
                        if (startingPosition + count < rawToMask!!.size) rawToMask!![startingPosition + count] else lastValidMaskPosition + 1
                    selection1 = nextValidPosition(currentPosition)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        if (!editingAfter && editingBefore && editingOnChanged) {
            editingAfter = true
            if (hasHint() && (keepHint || rawText!!.length() == 0)) {
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

    private fun isKeepHint(): Boolean {
        return keepHint
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
                if (selStart > rawText!!.length() - 1) {
                    val start = fixSelection(selStart)
                    val end = fixSelection(selEnd)
                    if (start >= 0 && end < text!!.length) {
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
        while (currentPosition < lastValidMaskPosition && maskToRaw!![currentPosition] == -1) {
            currentPosition++
        }
        return if (currentPosition > lastValidMaskPosition) lastValidMaskPosition + 1 else currentPosition
    }

    private fun previousValidPosition(currentPosition1: Int): Int {
        var currentPosition = currentPosition1
        while (currentPosition >= 0 && maskToRaw!![currentPosition] == -1) {
            currentPosition--
            if (currentPosition < 0) {
                return nextValidPosition(0)
            }
        }
        return currentPosition
    }

    private fun lastValidPosition(): Int {
        return if (rawText!!.length() == maxRawLength) {
            rawToMask!![rawText!!.length() - 1] + 1
        } else nextValidPosition(rawToMask!![rawText!!.length()])
    }

    private fun makeMaskedText(): String {
        val maskedTextLength: Int = if (rawText!!.length() < rawToMask!!.size) {
            rawToMask!![rawText!!.length()]
        } else {
            mask.length
        }
        val maskedText = CharArray(maskedTextLength)
        for (i in maskedText.indices) {
            val rawIndex = maskToRaw?.get(i).orZero()
            if (rawIndex == -1) {
                maskedText[i] = mask[i]
            } else {
                maskedText[i] = rawText!!.charAt(rawIndex)
            }
        }
        return String(maskedText)
    }

    private fun makeMaskedTextWithHint(): CharSequence {
        val ssb = SpannableStringBuilder()
        var mtrv: Int
        val maskFirstChunkEnd = rawToMask?.get(0).orZero()
        for (i in mask.indices) {
            mtrv = maskToRaw!![i]
            if (mtrv != -1) {
                if (mtrv < rawText!!.length()) {
                    ssb.append(rawText!!.charAt(mtrv))
                } else {
                    ssb.append(hint[maskToRaw!![i]])
                }
            } else {
                ssb.append(mask[i])
            }
            if (keepHint && rawText!!.length() < rawToMask!!.size && i >= rawToMask!![rawText!!.length()]
                || !keepHint && i >= maskFirstChunkEnd
            ) {
                ssb.setSpan(ForegroundColorSpan(currentHintTextColor), i, i + 1, 0)
            }
        }
        return ssb
    }

    private fun calculateRange(start: Int, end: Int): Range {
        val range = Range()
        var i = start
        while (i <= end && i < mask.length) {
            if (maskToRaw!![i] != -1) {
                if (range.start == -1) {
                    range.start = maskToRaw!![i]
                }
                range.end = maskToRaw!![i]
            }
            i++
        }
        if (end == mask.length) {
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
        if (deniedChars != null) {
            for (c in deniedChars.orEmpty().toCharArray()) {
                string = string.replace(c.toString(), String.EMPTY)
            }
        }
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

    companion object {
        const val SPACE = " "
    }
}