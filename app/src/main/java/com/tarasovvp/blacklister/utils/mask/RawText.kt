package com.tarasovvp.blacklister.utils.mask

import com.tarasovvp.blacklister.extensions.EMPTY

class RawText {
    var text = String.EMPTY

    fun subtractFromString(range: Range) {
        text = String.format("%s%s",
            if (range.start > 0 && range.start <= text.length)
                text.substring(0, range.start) else String.EMPTY,
            if (range.end >= 0 && range.end < text.length) text.substring(range.end) else String.EMPTY)
    }

    fun addToString(newString1: String?, start: Int, maxLength: Int): Int {
        if (newString1 == null || newString1 == "") {
            return 0
        } else require(start >= 0) { "Start position must be non-negative" }
        require(start <= text.length) { "Start position must be less than the actual text length" }
        val firstPart = if (start > 0) text.substring(0, start) else String.EMPTY
        val lastPart = if (start < text.length) text.substring(start) else String.EMPTY
        var count = newString1.length
        val newString = if (text.length + newString1.length > maxLength) {
            count = maxLength - text.length
            newString1.substring(0, count)
        } else newString1
        text = firstPart + newString + lastPart
        return count
    }

    fun length(): Int {
        return text.length
    }

    fun charAt(position: Int): Char {
        return text[position]
    }
}