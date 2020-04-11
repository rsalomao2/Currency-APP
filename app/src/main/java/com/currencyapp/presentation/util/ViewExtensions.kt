package com.currencyapp.presentation.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

internal fun EditText.afterTextChanged(action: (String) -> Unit): TextWatcher {
    var current = ""
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(editable: Editable?) {
            if (editable.toString() != current) {
                removeTextChangedListener(this)

                var cleanString = editable.toString().replace("[,.\\s]".toRegex(), "")

                if (cleanString.length >= 15) {
                    cleanString = cleanString.substring(0, cleanString.length - 1)
                }

                val parsed = if (cleanString.isEmpty()) 0.0 else cleanString.toDouble()

                val formatted = formatCurrency(parsed / 100)

                current = formatted

                val filters = editable?.filters
                editable?.filters = arrayOf()
                editable?.clear()
                editable?.append(current)
                editable?.filters = filters

                action.invoke((parsed / 100.0).toString().replace("$", ""))

                addTextChangedListener(this)
            }
        }
    }

    addTextChangedListener(watcher)
    return watcher
}

private fun getCurrencyIns(): NumberFormat {
    return DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.getDefault()))
}

fun formatCurrency(price: Double): String {
    return getCurrencyIns().format(price)
}

