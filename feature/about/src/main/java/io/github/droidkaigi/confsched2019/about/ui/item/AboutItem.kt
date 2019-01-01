package io.github.droidkaigi.confsched2019.about.ui.item

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.ItemAboutBinding
import io.github.droidkaigi.confsched2019.model.About

class AboutItem(
    private val item: About.Item
) : BindableItem<ItemAboutBinding>() {

    override fun createViewHolder(itemView: View): ViewHolder<ItemAboutBinding> {
        return ViewHolder(DataBindingUtil.bind(itemView)!!)
    }

    override fun getLayout(): Int = R.layout.item_about

    override fun bind(binding: ItemAboutBinding, position: Int) {
        binding.item = item
        if (item.navigationUrl.isNullOrEmpty()) {
            binding.checkText.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.gray2)
            )
        }
    }

    override fun isSameAs(other: Item<*>?): Boolean =
        other is AboutItem

    override fun equals(other: Any?): Boolean =
        item == (other as? AboutItem?)?.item

    override fun hashCode(): Int = item.hashCode()
}
