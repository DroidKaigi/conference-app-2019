package io.github.droidkaigi.confsched2019.item

import android.widget.TextView
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.github.droidkaigi.confsched2019.widget.component.R

class SearchSectionHeaderItem(
    private val title: CharSequence
) : Item<ViewHolder>(title.hashCode().toLong()),
    EqualableContentsProvider {
    override fun getLayout(): Int = R.layout.view_section_header

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.section_title).apply {
            text = title
        }
    }

    override fun providerEqualableContents(): Array<*> = arrayOf(title)

    override fun equals(other: Any?): Boolean = isSameContents(other)

    override fun hashCode(): Int = contentsHash()
}
