package com.currencyapp.presentation.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyapp.data.remote.repository.CurrencyRepository
import com.currencyapp.data.extension.mutableLiveData
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
    val currencyReference = mutableLiveData(Event(_defaultCurrency))
    val errorNetwork = MutableLiveData<Event<Boolean>>()
    val currencyList = MutableLiveData<List<Currency>>()
    val errorMessage = MutableLiveData<Event<String>>()
    var scrollToTopFlag = false
    val onItemClickListener: (Currency) -> (Unit) = { clickedCurrency ->
        if (_currencyReference != clickedCurrency) {
            val currencyReferenceRate =
                _currencyReference?.rate ?: _defaultCurrency.rate
            val updatedReferenceCurrency = clickedCurrency.copy(rate = currencyReferenceRate)
            currencyReference.value = Event(updatedReferenceCurrency)
            scrollToTopFlag = _currencyReference?.code == clickedCurrency.code
        }
    }
    private var _job: Job? = null
    private val _currencyReference: Currency? get() = currencyReference.value?.peekContent()

    val onTextListener: (String) -> (Unit) = { newRate ->
        if (newRate.isNotBlank() && _currencyReference?.rate != newRate.toDoubleOrNull()){
            val currencyReferenceWithRateUpdated =
                _currencyReference?.copy(rate = newRate.toDoubleOrNull() ?: 1.0)
                    ?: _defaultCurrency.copy(rate = newRate.toDoubleOrNull() ?: 1.0)
            currencyReference.value = Event(currencyReferenceWithRateUpdated)
        }
    }

    init {
        viewModelScope.launch {
            autoUpdateCurrencyListJob()
        }
    }

    fun loadLatestCurrency(code: String?) {
        _job = viewModelScope.launch {
            when (val result = repository.loadLatestCurrency(code ?: _defaultCurrency.code)) {
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
            val baseCurrency = _currencyReference ?: _defaultCurrency
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
                val referenceCode =
                    _currencyReference?.code ?: _defaultCurrency.code
                loadLatestCurrency(referenceCode)
                delay(UPDATE_DELAY)
            }
        }
    }
}
