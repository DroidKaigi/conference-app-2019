package io.github.droidkaigi.confsched2019.about.ui.item

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.ItemAboutBinding

class AboutItem(
    @StringRes private val name: Int,
    @StringRes private val description: Int,
    private val clickListener: ((Context) -> Unit)? = null
) : BindableItem<ItemAboutBinding>() {

    override fun createViewHolder(itemView: View): ViewHolder<ItemAboutBinding> {
        return ViewHolder(DataBindingUtil.bind(itemView)!!)
    }

    override fun getLayout(): Int = R.layout.item_about

    override fun bind(binding: ItemAboutBinding, position: Int) {
        binding.name = name
        binding.description = description
        binding.checkText.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                if (clickListener == null) {
                    R.color.gray2
                } else {
                    R.color.colorSecondary
                }
            )
        )
        clickListener?.let { clickListener ->
            binding.root.setOnClickListener { clickListener(it.context) }
        }
    }
}
