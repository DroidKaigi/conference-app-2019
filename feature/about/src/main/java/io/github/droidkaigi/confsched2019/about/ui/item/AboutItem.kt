package io.github.droidkaigi.confsched2019.about.ui.item

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.ItemAboutBinding

class AboutItem(
    @StringRes private val name: Int,
    private val description: Int,
    private val summary: String? = null,
    private val isLektonCheckText: Boolean = false,
    private val clickListener: ((Context) -> Unit)? = null
) : BindableItem<ItemAboutBinding>() {

    override fun getLayout(): Int = R.layout.item_about

    override fun bind(binding: ItemAboutBinding, position: Int) {
        binding.name = name
        binding.description = description
        binding.summary = summary
        TextViewCompat.setTextAppearance(
            binding.checkText, if (isLektonCheckText) {
                R.style.TextAppearance_Lekton_Body1
            } else {
                // Because use Lekton only for version item
                0
            }
        )
        binding.checkText.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                if (clickListener == null) {
                    R.color.colorOnBackgroundSecondary
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
