package com.currencyapp.presentation.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.*

private const val DOUBLE_INTEGER_DIGITS = 309
private const val MAX_INT_DIGITS = 7
internal inline fun EditText.afterTextChanged(crossinline body: (text: CharSequence) -> Unit): TextWatcher {
    val current = ""
    val currencyFormatter = CurrencyFormatter()
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                if (!hasFocus()) return
                val maxIntDig = if (hasFocus()) MAX_INT_DIGITS else DOUBLE_INTEGER_DIGITS

                removeTextChangedListener(this)
                currencyFormatter.isDecimalSeparatorAlwaysShown(hasFocus())
                currencyFormatter.setMaxInegerDigits(maxIntDig)

                val formattedValue = currencyFormatter.format(s.toString())
                formatAndSelectCursor(formattedValue)

                if (hasFocus()) {
                    body(s.fromDecimalToDouble().toString())
                }
            }
        }

        private fun formatAndSelectCursor(value: String?) {
            val startLength: Int = text.length
            val cp = selectionStart
            setText(value)
            val endLength: Int = text.length
            val sel = cp + (endLength - startLength)
            if (sel > 0 && sel <= text.length) {
                setSelection(sel)
            } else { // place cursor at the end?
                setSelection(text.length)
            }
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

fun Editable?.fromDecimalToDouble(): Double =
    if (!isNullOrBlank()) {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply { maximumFractionDigits = 2 }
            .parse(toString())
            .toDouble()
    } else {
        0.0
    }
