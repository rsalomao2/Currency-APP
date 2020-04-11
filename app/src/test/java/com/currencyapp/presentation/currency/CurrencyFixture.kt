package com.currencyapp.presentation.currency

import com.currencyapp.domain.model.Currency


object CurrencyFixture {
    const val mockCode = "EUR"
    const val mockErrorMessage = "Error Message Mock"
    val mockCurrency = Currency(code="EUR", country="Germany", rate=1.0, iconUrl="file:///android_asset/flags/eu.png")
    val mockClickedItem = Currency(mockCode, "", 1.0, "")
    val mockFinalListOfCurrency = listOf(mockCurrency, mockClickedItem)
    val mockResponseList = listOf(mockClickedItem)
}