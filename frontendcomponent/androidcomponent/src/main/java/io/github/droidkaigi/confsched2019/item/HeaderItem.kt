package io.github.droidkaigi.confsched2019.item

import android.widget.TextView
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.github.droidkaigi.confsched2019.widget.component.R

class HeaderItem(private val title: CharSequence) : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.section

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.section_title).apply {
            text = title
        }
    }
}
