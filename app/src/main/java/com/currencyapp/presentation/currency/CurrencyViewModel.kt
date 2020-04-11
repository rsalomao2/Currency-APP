package com.currencyapp.presentation.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyapp.data.remote.repository.CurrencyRepository
import com.currencyapp.domain.model.Currency
import com.currencyapp.domain.model.Event
import com.currencyapp.domain.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val UPDATE_DELAY = 1000L

class CurrencyViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _defaultCurrency =
        Currency("EUR", "Germany", 1.0, "file:///android_asset/flags/eu.png")

    private var _currencyBase: Currency? = _defaultCurrency
    val errorNetwork = MutableLiveData<Event<Boolean>>()
    val hideKeyBoard = MutableLiveData<Event<Boolean>>()
    val currencyList = MutableLiveData<List<Currency>>()
    val errorMessage = MutableLiveData<Event<String>>()
    var scrollToTopFlag = false
    val onItemClickListener: (Currency) -> (Unit) = { clickedCurrency ->
        if (_currencyBase != clickedCurrency) {
            _currencyBase = clickedCurrency.copy(rate = _currencyBase?.rate?: 1.0)
            hideKeyBoard.value = Event(true)
            scrollToTopFlag = _currencyBase?.code == clickedCurrency.code
            loadLatestCurrency()
        }
    }
    private var _job: Job? = null

    val onTextListener: (String) -> (Unit) = { newRate ->
        if (newRate.isNotBlank() && _currencyBase?.rate != newRate.toDoubleOrNull()) {
            _currencyBase?.rate = newRate.toDouble()
            loadLatestCurrency()
        }
    }

    init {
        viewModelScope.launch {
//            autoUpdateCurrencyListJob()
            loadLatestCurrency()
        }
    }

    fun loadLatestCurrency() {
        val code = _currencyBase?.code ?: _defaultCurrency.code
        _job = viewModelScope.launch {
            when (val result = repository.loadLatestCurrency(code)) {
                is Status.Success -> {
                    currencyList.value = getCurrencyList(result.response)
                }
                is Status.Error -> {
                    _job?.cancel()
                    errorMessage.value = Event(result.responseError)
                }
                is Status.NetworkError -> {
                    _job?.cancel()
                    errorNetwork.value = Event(true)
                }
            }
        }
    }

    private fun getCurrencyList(result: List<Currency>): List<Currency> {
        return mutableListOf<Currency>().apply {
            val baseCurrency = _currencyBase ?: _defaultCurrency
            add(baseCurrency)
            result.map {
                add(Currency(it.code, it.country, it.rate * baseCurrency.rate, it.iconUrl))
            }
        }
    }

    private suspend fun autoUpdateCurrencyListJob() {
        _job?.cancel()
        _job = viewModelScope.launch {
            while (true) {
                loadLatestCurrency()
                delay(UPDATE_DELAY)
            }
        }
    }
}
