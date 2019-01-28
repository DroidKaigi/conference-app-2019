package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularSpeakerBinding
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularSpeechSessionBinding
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class TabularSpeechSessionItem(
    val speechSession: SpeechSession,
    private val navDirections: NavDirections,
    private val navController: NavController
) : BindableItem<ItemTabularSpeechSessionBinding>(speechSession.id.hashCode().toLong()) {

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    init {
        groupAdapter.addAll(speechSession.speakers.map { SpeakerIconItem(it) })
    }

    override fun bind(viewBinding: ItemTabularSpeechSessionBinding, position: Int) {
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
            // FIXME pattern favorited
            root.isActivated = !speechSession.isFinished
        }
    }

    override fun getLayout() = R.layout.item_tabular_speech_session

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TabularSpeechSessionItem

        if (speechSession != other.speechSession) return false

        return true
    }

    override fun hashCode(): Int {
        return speechSession.hashCode()
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
