package com.tarasovvp.smartblocker.utils

import android.text.method.LinkMovementMethod
import android.widget.TextView
import android.text.Spannable
import android.view.MotionEvent
import android.text.style.URLSpan
import com.tarasovvp.smartblocker.utils.extensions.notEquals

abstract class TextViewLinkHandler : LinkMovementMethod() {
    override fun onTouchEvent(textView: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action notEquals MotionEvent.ACTION_UP) return super.onTouchEvent(textView, buffer, event)
        var x = event.x.toInt()
        var y = event.y.toInt()
        x -= textView.totalPaddingLeft
        y -= textView.totalPaddingTop
        x += textView.scrollX
        y += textView.scrollY
        val layout = textView.layout
        val line = layout.getLineForVertical(y)
        val off = layout.getOffsetForHorizontal(line, x.toFloat())
        val link = buffer.getSpans(off, off, URLSpan::class.java)
        if (link.isNotEmpty()) {
            onLinkClick(link[0].url)
        }
        return true
    }

    abstract fun onLinkClick(url: String?)
}