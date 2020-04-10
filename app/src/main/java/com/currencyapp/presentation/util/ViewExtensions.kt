package com.currencyapp.presentation.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat

internal fun EditText.afterTextChanged(textResult: (String) -> Unit): TextWatcher {
    var current = ""
    var cursorPosition = 0
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            cursorPosition = start + after
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                removeTextChangedListener(this)
                current = s.toString().formatToCurrency()
                setText(current)
                if (hasFocus()) {
                    setCursorPosition()
                    textResult.invoke(current.replace(",", ""))
                }
                addTextChangedListener(this)
            }
        }

        private fun setCursorPosition() {
            if (cursorPosition <= current.length) {
                setSelection(cursorPosition)
            } else
                setSelection(cursorPosition)
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

private val currencyFormatter = NumberFormat.getCurrencyInstance()
private val symbol = currencyFormatter.currency.symbol
private const val CURRENCY_PATTERN = "%.2f"
fun String.formatToCurrency(): String {
    val force2DecimalDigits = CURRENCY_PATTERN.format(this.replace(",","").toDouble())
    val cleanString = force2DecimalDigits.replace(".", "").replace(",", "")
    val parsed = cleanString.toDoubleOrNull() ?: 0.0
    val formatted = currencyFormatter.format((parsed / 100))
    return formatted.replace(symbol, "")
}
