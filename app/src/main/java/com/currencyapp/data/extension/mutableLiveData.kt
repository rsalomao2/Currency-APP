package com.currencyapp.data.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.currencyapp.domain.model.Event

fun <T : Any> mutableLiveData(initialValue: T? = null): MutableLiveData<T> {
    return MutableLiveData<T>().apply { value = initialValue }
}

fun <T> LiveData<T>.observeNotNull(lifecycleOwner: LifecycleOwner, onChanged: (t: T) -> Unit) {
    this.observe(lifecycleOwner, Observer { if (it != null) onChanged(it) })
}

fun <T> LiveData<Event<T>>.observeEventNotHandled(owner: LifecycleOwner, observer: (T) -> Unit) {
    this.observe(owner, Observer<Event<T>> { event ->
        event?.getContentIfNotHandled()?.let { content ->
            observer(content)
        }
    })
}
