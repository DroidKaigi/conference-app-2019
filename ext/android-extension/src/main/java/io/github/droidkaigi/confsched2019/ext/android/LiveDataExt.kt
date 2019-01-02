package io.github.droidkaigi.confsched2019.ext.android

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shopify.livedataktx.Removable
import com.shopify.livedataktx.SupportMediatorLiveData
import com.shopify.livedataktx.distinct
import com.shopify.livedataktx.map
import com.shopify.livedataktx.nonNull
import com.shopify.livedataktx.observe

inline fun <T> LiveData<T>.changed(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    return nonNull().distinct().observe(owner, onChanged)
}

inline fun <T> LiveData<T?>.changedNonNull(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    @Suppress("UNCHECKED_CAST")
    return (nonNull().distinct() as SupportMediatorLiveData<T>).observe(owner, onChanged)
}

inline fun <T : Any> LiveData<T>.changedForever(
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    return nonNull().distinct().observe(onChanged)
}

fun <T, R> LiveData<T>.mapNotNull(mapper: (T?) -> R?): LiveData<R> = map(mapper).nonNull()

fun <T : Any> LiveData<T>.requireValue(): T = requireNotNull(value)
