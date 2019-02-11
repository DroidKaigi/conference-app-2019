package io.github.droidkaigi.confsched2019.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import io.github.droidkaigi.confsched2019.widget.PageViewModel

interface PageComponent

inline fun <reified T : PageComponent> getPageComponent(
    fragment: Fragment,
    noinline pageComponentCreator: (pageLifecycleOwner: LifecycleOwner) -> T
): T {
    val viewModel = ViewModelProviders
        .of(fragment, PageViewModel.Factory(pageComponentCreator))
        .get(PageViewModel::class.java)
    return viewModel.pageComponent as T
}
