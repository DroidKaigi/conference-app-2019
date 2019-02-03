package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularSpeakerBinding
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularSpeechSessionBinding
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class TabularSpeechSessionItem @AssistedInject constructor(
    @Assisted override val session: SpeechSession,
    @Assisted private val navDirections: NavDirections,
    private val navController: NavController
) : BindableItem<ItemTabularSpeechSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem, EqualableContentsProvider {

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: SpeechSession,
            navDirections: NavDirections
        ): TabularSpeechSessionItem
    }

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    init {
        groupAdapter.addAll(session.speakers.map { SpeakerIconItem(it) })
    }

    override fun bind(viewBinding: ItemTabularSpeechSessionBinding, position: Int) {
        val speechSession = session
        viewBinding.apply {
            root.setOnClickListener {
                navController.navigate(navDirections)
            }
            session = speechSession
            lang = defaultLang()

            speakerIconList.apply {
                layoutManager = LinearLayoutManager(root.context, RecyclerView.HORIZONTAL, false)
                adapter = groupAdapter
            }

            if (speechSession.isFavorited) {
                backgroundView.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.tabular_session_favoried_background
                    )
                )
                verticalLineView.setBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.red1)
                )
                sessionTitle.setTextColor(
                    ContextCompat.getColor(root.context, R.color.red1)
                )
            } else {
                backgroundView.setBackgroundResource(R.drawable.bg_item_tabular)
                verticalLineView.setBackgroundResource(R.drawable.bg_vertical_line)
                sessionTitle.setTextColor(
                    ContextCompat.getColorStateList(root.context, R.color.tabular_session_title)
                )
            }
            root.isActivated = !speechSession.isFinished
        }
    }

    override fun getLayout() = R.layout.item_tabular_speech_session

    override fun providerEqualableContents(): Array<*> = arrayOf(session)

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    private class SpeakerIconItem(
        private val speaker: Speaker
    ) : BindableItem<ItemTabularSpeakerBinding>(speaker.id.hashCode().toLong()) {

        override fun bind(viewBinding: ItemTabularSpeakerBinding, position: Int) {
            viewBinding.apply {
                val context = root.context
                val placeHolderDrawable = VectorDrawableCompat.create(
                    context.resources,
                    R.drawable.ic_person_outline_black_24dp,
                    null
                )?.apply {
                    setTint(
                        ContextCompat.getColor(context, R.color.gray2)
                    )
                }

                val imageUrl = speaker.imageUrl ?: run {
                    sessionSpeakerIconImage.setImageDrawable(placeHolderDrawable)
                    return
                }

                Picasso.get()
                    .load(imageUrl)
                    .transform(CropCircleTransformation())
                    .apply {
                        if (placeHolderDrawable != null) {
                            placeholder(placeHolderDrawable)
                        }
                    }
                    .into(sessionSpeakerIconImage)
            }
        }

        override fun getLayout(): Int = R.layout.item_tabular_speaker
    }
}
