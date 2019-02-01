package io.github.droidkaigi.confsched2019.contributor.item

import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.contributor.R
import io.github.droidkaigi.confsched2019.model.Contributor
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import io.github.droidkaigi.confsched2019.contributor.databinding.ItemContributorBinding

class ContributorItem @AssistedInject constructor(
    @Assisted val contributor: Contributor,
    val activityActionCreator: ActivityActionCreator
) : BindableItem<ItemContributorBinding>() {

    @AssistedInject.Factory
    interface Factory {
        fun create(
            contributor: Contributor
        ): ContributorItem
    }

    override fun getLayout(): Int = R.layout.item_contributor

    override fun bind(
        binding: ItemContributorBinding,
        index: Int
    ) {
        binding.contributor = contributor
        binding.offset = "${index + 1}"

        binding.root.setOnClickListener {
            activityActionCreator.openUrl(contributor.profileUrl)
        }

        val context = binding.root.context

        val placeHolderColor = ContextCompat.getColor(
            context,
            R.color.gray2
        )
        val placeHolder = VectorDrawableCompat.create(
            context.resources,
            R.drawable.ic_person_outline_black_24dp,
            null
        )
        placeHolder?.setTint(placeHolderColor)
        binding.contributorImage.clearColorFilter()
        Picasso
            .get()
            .load(contributor.iconUrl)
            .transform(CropCircleTransformation())
            .apply {
                placeHolder?.let {
                    placeholder(it)
                }
            }
            .into(binding.contributorImage)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContributorItem

        if (contributor != other.contributor) return false

        return true
    }

    override fun hashCode(): Int {
        return 32 * contributor.hashCode()
    }
}
