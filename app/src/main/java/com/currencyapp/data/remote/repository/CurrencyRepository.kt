package com.currencyapp.data.remote.repository

import com.currencyapp.domain.model.Currency
import com.currencyapp.domain.model.Status

interface CurrencyRepository {
    suspend fun loadLatestCurrency(code: String): Status<List<Currency>>
}
