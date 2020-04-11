package com.currencyapp.data.remote.dto

data class CurrencyResponseDto(val baseCurrency: String,val rates: Map<String, Double>)
