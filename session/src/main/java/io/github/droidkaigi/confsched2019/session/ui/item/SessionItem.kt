package io.github.droidkaigi.confsched2019.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2019.model.Session

data class SessionItem(
    val session: Session.SpeechSession,
    private val onFavoriteClickListener: (Session.SpeechSession) -> Unit,
    private val isShowDayNumber: Boolean = false,
    private val searchQuery: String = "",
    private val simplify: Boolean = false,
    private val userIdInDetail: String? = null
) : BindableItem<ItemSessionBinding>(
    session.id.toLong()
) {

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        viewBinding.session = session
        viewBinding.searchQuery = searchQuery
        viewBinding.favorite.setOnClickListener {
            onFavoriteClickListener(session)
        }
        viewBinding.isShowDayNumber = isShowDayNumber
        viewBinding.simplify = simplify

        session.message?.let { message ->
            viewBinding.message.text = if (true) {
                message.jaMessage
            } else {
                message.enMessage
            }
        }

//        when (session.level) {
//            is Level.Beginner -> R.drawable.ic_beginner_lightgreen_20dp
//            is Level.IntermediateOrExpert -> R.drawable.ic_intermediate_senior_bluegray_20dp
//            is Level.Niche -> R.drawable.ic_niche_cyan_20dp
//        }
    }

    override fun getLayout(): Int = R.layout.item_session
}
