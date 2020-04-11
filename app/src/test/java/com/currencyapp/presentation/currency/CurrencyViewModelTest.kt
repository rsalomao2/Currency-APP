package com.currencyapp.presentation.currency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.currencyapp.data.remote.repository.CurrencyRepository
import com.currencyapp.domain.model.Currency
import com.currencyapp.domain.model.Event
import com.currencyapp.domain.model.Status
import com.currencyapp.presentation.currency.CurrencyFixture.mockClickedItem
import com.currencyapp.presentation.currency.CurrencyFixture.mockCode
import com.currencyapp.presentation.currency.CurrencyFixture.mockCurrency
import com.currencyapp.presentation.currency.CurrencyFixture.mockErrorMessage
import com.currencyapp.presentation.currency.CurrencyFixture.mockFinalListOfCurrency
import com.currencyapp.presentation.currency.CurrencyFixture.mockResponseList
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.refEq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CurrencyViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CurrencyRepository

    @Mock
    private lateinit var lifecycleOwner: LifecycleOwner

    @Mock
    private lateinit var currencyListObserver: Observer<List<Currency>>

    @Mock
    private lateinit var currencyObserver: Observer<Event<Currency>>

    @Mock
    private lateinit var stringObserver: Observer<Event<String>>

    @Mock
    private lateinit var booleanObserver: Observer<Event<Boolean>>

    @Mock
    private lateinit var testCoroutineDispatcher: CoroutineDispatcher

    private lateinit var viewModel: CurrencyViewModel
    private lateinit var lifecycle: LifecycleRegistry


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testCoroutineDispatcher)

        lifecycle = LifecycleRegistry(lifecycleOwner)
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        viewModel = CurrencyViewModel(repository)
    }

    @Test
    fun `it should init variables properly`() {
        assertNull(viewModel.errorNetwork.value)
        assertNull(viewModel.currencyList.value)
        assertNull(viewModel.errorMessage.value)
        assertFalse(viewModel.scrollToTopFlag)
    }

    @Test
    fun `emmit changes on currency list if success`() = runBlocking {
        whenever(repository.loadLatestCurrency(mockCode)).thenReturn(Status.Success(mockResponseList))
        viewModel.currencyList.observe(lifecycleOwner, currencyListObserver)
        viewModel.startJobs()
        verify(currencyListObserver).onChanged(mockFinalListOfCurrency)
    }

    @Test
    fun `scroll top when click on item`() {
        viewModel.onItemClickListener.invoke(mockClickedItem)
        assertTrue(viewModel.scrollToTopFlag)
    }

    @Test
    fun `emmit error message when request fail`() = runBlocking {
        whenever(repository.loadLatestCurrency(mockCode)).thenReturn(Status.Error(mockErrorMessage))
        viewModel.errorMessage.observe(lifecycleOwner, stringObserver)
        viewModel.startJobs()
        verify(stringObserver).onChanged(refEq(Event(mockErrorMessage)))
    }

    @Test
    fun `emmit network error message when request connection lost`() = runBlocking {
        whenever(repository.loadLatestCurrency(mockCode)).thenReturn(Status.NetworkError)
        viewModel.errorNetwork.observe(lifecycleOwner, booleanObserver)
        viewModel.startJobs()
        verify(booleanObserver).onChanged(refEq(Event(true)))
    }
}
