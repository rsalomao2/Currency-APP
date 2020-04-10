package com.currencyapp.data.remote.repository

import android.annotation.SuppressLint
import android.content.Context
import com.currencyapp.data.provider.CoroutineContextProvider
import com.currencyapp.data.remote.dto.CountryDto
import com.currencyapp.data.remote.dto.CurrencyResponseDto
import com.currencyapp.data.remote.service.CurrencyApiService
import com.currencyapp.domain.model.Currency
import com.currencyapp.domain.model.Status
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class CurrencyRepositoryImpl(
    private val context: Context,
    private val contextProvider: CoroutineContextProvider,
    private val service: CurrencyApiService
) :
    CurrencyRepository {
    override suspend fun loadLatestCurrency(code: String): Status<List<Currency>> =
        withContext(contextProvider.IO) {
            try {
                val response = service.getApiLatestCurrency(code)
                Status.Success(extractCurrencyList(response))
            } catch (e: HttpException) {
                Status.NetworkError
            } catch (e: Exception) {
                Status.Error(e.message ?: "Unknown error")
            }
        }

    private fun extractCurrencyList(response: CurrencyResponseDto) =
        mutableListOf<Currency>().apply {
            response.rates.map {
                add(
                    Currency(
                        it.key,
                        getCountryName(it.key),
                        it.value,
                        getUrlFromCurrencyCode(it.key)
                    )
                )
            }
        }

    private fun getCountryName(code: String): String {
        val file = context.assets.open("countries.json").bufferedReader().use { it.readText() }

        val countries = Gson().fromJson(file, Array<CountryDto>::class.java)
        val country = countries.first {
            it.currency_code == code
        }
        return country.country
    }

    @SuppressLint("DefaultLocale")
    private fun getUrlFromCurrencyCode(code: String): String {
        val codeName = code.toLowerCase().subSequence(0, 2)
        return "file:///android_asset/flags/$codeName.png"
    }
}
