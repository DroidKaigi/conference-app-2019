package io.github.droidkaigi.confsched2019.ext.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.shopify.livedataktx.Removable
import com.shopify.livedataktx.distinct
import com.shopify.livedataktx.nonNull
import com.shopify.livedataktx.observe

inline fun <T : Any> LiveData<T>.changed(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    return nonNull().distinct().observe(owner, onChanged)
}
inline fun <T : Any> LiveData<T>.changedForever(
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    return nonNull().distinct().observe(onChanged)
}
