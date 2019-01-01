package io.github.droidkaigi.confsched2019.about.ui.item

import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.HeaderAboutBinding
import io.github.droidkaigi.confsched2019.about.fixeddata.AboutThisApp

class AboutHeaderItem(
    private val headItem: AboutThisApp.HeadItem
) : BindableItem<HeaderAboutBinding>() {

    override fun createViewHolder(itemView: View): ViewHolder<HeaderAboutBinding> {
        return ViewHolder(DataBindingUtil.bind(itemView)!!)
    }

    override fun getLayout(): Int = R.layout.header_about

    // TODO: Set banner image/background
    // TODO: Set archives click listener
    override fun bind(binding: HeaderAboutBinding, position: Int) {
        binding.headItem = headItem
    }

    override fun isSameAs(other: Item<*>?): Boolean =
        other is AboutHeaderItem

    override fun equals(other: Any?): Boolean =
        headItem == (other as? AboutHeaderItem?)?.headItem

    override fun hashCode(): Int = headItem.hashCode()
}
