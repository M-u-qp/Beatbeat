package com.muqp.core_ui.common

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.widget.SearchView
import androidx.core.content.res.ResourcesCompat


object CustomSpanHintSearchView {
    private class CustomTypefaceSpan(private val typeface: Typeface?) : TypefaceSpan("") {

        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeface(ds, typeface)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeface(paint, typeface)
        }

        private fun applyCustomTypeface(paint: Paint, tf: Typeface?) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0

            val fake = oldStyle and tf!!.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }

            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }

            paint.typeface = tf
        }
    }

    fun getTypefaceSearchView(
        hintTextId: Int,
        fontId: Int,
        context: Context,
        searchView: SearchView
    ) {
        val searchHintText = context.getString(hintTextId)
        val spannableString = SpannableString(searchHintText)
        val typeface = ResourcesCompat.getFont(context, fontId)
        spannableString.setSpan(
            CustomTypefaceSpan(typeface),
            0,
            searchHintText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        searchView.queryHint = spannableString
    }
}
