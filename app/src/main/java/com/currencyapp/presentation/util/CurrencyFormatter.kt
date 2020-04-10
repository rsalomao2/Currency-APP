package com.currencyapp.presentation.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*

/**
 * Created by novemio on 2/25/20.
 */
class CurrencyFormatter {
    private val symbols = DecimalFormatSymbols.getInstance(Locale.UK)
    private val df: DecimalFormat = DecimalFormat("#,###.##", symbols)
    private val df0: DecimalFormat = DecimalFormat("#,##0.0;#,###.0#", symbols)
    private val df00: DecimalFormat = DecimalFormat("#,##0.00;#,###.00", symbols)
    private val dfnd: DecimalFormat = DecimalFormat("#,###", symbols)
    private var hasFractionalPart = false
    private var hasFraction0 = false
    private var hasFRaction00 = false


    fun isDecimalSeparatorAlwaysShown(value: Boolean) {
        df.isDecimalSeparatorAlwaysShown = value
    }

    fun setMaxInegerDigits(max: Int) {
        df.maximumIntegerDigits = max
        df0.maximumIntegerDigits = max
        df00.maximumIntegerDigits = max
        dfnd.maximumIntegerDigits = max
    }

    fun checkDecimalSymbol(value: String) {
        val decimalSeparator = df.decimalFormatSymbols.decimalSeparator.toString()
        hasFractionalPart = value.contains(decimalSeparator)
        hasFraction0 = value.endsWith("${decimalSeparator}0")
        hasFRaction00 = value.endsWith("${decimalSeparator}00")
    }


    fun format(value: String): String? {
        return try {
            value.replace(df.decimalFormatSymbols.groupingSeparator.toString(), "")
            val number = df.parse(value)
            when {
                hasFRaction00 -> df00.format(number)
                hasFraction0 -> df0.format(number)
                hasFractionalPart -> df.format(number)
                else -> dfnd.format(number)
            }
        } catch (e: ParseException) {
            ""
        } catch (e: IllegalArgumentException) {
            ""
        }
    }
}
//TODO: Verificar se precisa disso mesmo