package io.github.droidkaigi.confsched2019.session.ui.item

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.bindingadapter.setHighlightText
import io.github.droidkaigi.confsched2019.util.lazyWithParam
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.lang.IllegalArgumentException
import kotlin.math.max

class SpeechSessionItem @AssistedInject constructor(
    @Assisted override val session: SpeechSession,
    @Assisted val detailNavDirections: NavDirections,
    @Assisted val surveyNavDirections: NavDirections,
    @Assisted val hasStartPadding: Boolean,
    @Assisted val query: String?,
    val navController: NavController,
    val sessionContentsActionCreator: SessionContentsActionCreator
) : BindableItem<ItemSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem, EqualableContentsProvider {
    val speechSession get() = session

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: SpeechSession,
            detailNavDirections: NavDirections,
            surveyNavDirections: NavDirections,
            hasStartPadding: Boolean,
            query: String? = null
        ): SpeechSessionItem
    }

    val layoutInflater by lazyWithParam<Context, LayoutInflater> { context ->
        LayoutInflater.from(context)
    }

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        with(viewBinding) {
            root.setOnClickListener {
                try {
                    navController.navigate(detailNavDirections)
                } catch (e: IllegalArgumentException) {
                    // FIXME: When launching the app and click session multiple times, cause Exception
                    // see https://github.com/DroidKaigi/conference-app-2019/issues/664
                }
            }
            session = speechSession
            lang = defaultLang()
            hasStartPadding = this@SpeechSessionItem.hasStartPadding
            query = this@SpeechSessionItem.query
            favorite.setOnClickListener {
                // apply state immediately
                viewBinding.session = speechSession.copy(
                    isFavorited = !speechSession.isFavorited
                )
                sessionContentsActionCreator.toggleFavorite(speechSession)
            }

            val timeInMinutes: Int = speechSession.timeInMinutes
            timeAndRoom.text = root.context.getString(
                R.string.session_duration_room_format,
                timeInMinutes,
                speechSession.room.name
            )
            categoryChip.text = speechSession.category.name.getByLang(defaultLang())
            survey.setOnClickListener {
                navController.navigate(surveyNavDirections)
            }

            bindSpeaker()

            speechSession.message?.let { message ->
                this@with.message.text = message.getByLang(defaultLang())
            }
        }
    }

    private fun ItemSessionBinding.bindSpeaker() {
        (0 until max(
            speakers.size, speechSession.speakers.size
        )).forEach { index ->
            val existSpeakerView = speakers.getChildAt(index) as? ViewGroup
            val speaker: Speaker? = speechSession.speakers.getOrNull(index)
            if (existSpeakerView == null && speaker == null) {
                return@forEach
            }
            if (existSpeakerView != null && speaker == null) {
                // Cache for performance
                existSpeakerView.isVisible = false
                return@forEach
            }
            if (existSpeakerView == null && speaker != null) {
                val speakerView = layoutInflater.get(root.context).inflate(
                    R.layout.layout_speaker, speakers, false
                ) as ViewGroup
                val textView: TextView = speakerView.findViewById(R.id.speaker)
                bindSpeakerData(speaker, textView)

                speakers.addView(speakerView)
                return@forEach
            }
            if (existSpeakerView != null && speaker != null) {
                val textView: TextView = existSpeakerView.findViewById(R.id.speaker)
                textView.text = speaker.name
                bindSpeakerData(speaker, textView)
            }
        }
    }

    private fun bindSpeakerData(
        speaker: Speaker,
        textView: TextView
    ) {
        textView.text = speaker.name
        setHighlightText(textView, query)
        val imageUrl = speaker.imageUrl
        val context = textView.context
        val placeHolder = run {
            VectorDrawableCompat.create(
                context.resources,
                R.drawable.ic_person_outline_black_24dp,
                null
            )?.apply {
                setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.gray2
                    )
                )
            }
        }

        imageUrl ?: run {
            placeHolder?.let {
                textView.setLeftDrawable(it)
            }
        }

        Picasso
            .get()
            .load(imageUrl)
            .transform(CropCircleTransformation())
            .apply {
                if (placeHolder != null) {
                    placeholder(placeHolder)
                }
            }
            .into(object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    placeHolderDrawable?.let {
                        textView.setLeftDrawable(it)
                    }
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val res = textView.context.resources
                    val drawable = BitmapDrawable(res, bitmap)
                    textView.setLeftDrawable(drawable)
                }
            })
    }

    fun TextView.setLeftDrawable(drawable: Drawable) {
        val res = context.resources
        val widthDp = 16
        val heightDp = 16
        val widthPx = (widthDp * res.displayMetrics.density).toInt()
        val heightPx = (heightDp * res.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, widthPx, heightPx)
        setCompoundDrawables(drawable, null, null, null)
    }

    override fun getLayout(): Int = R.layout.item_session

    override fun providerEqualableContents(): Array<*> = arrayOf(session)

    override fun isTextHighlightNeedUpdate() = query?.let {
        when {
            session.title.getByLang(defaultLang()).toLowerCase()
                .contains(query.toLowerCase().toRegex()) -> true
            session.speakers.find {
                it.name.toLowerCase().contains(query.toLowerCase().toRegex())
            } != null -> true
            else -> false
        }
    } ?: false

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }
}
