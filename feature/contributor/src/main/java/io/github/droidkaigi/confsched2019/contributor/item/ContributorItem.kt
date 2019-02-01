package io.github.droidkaigi.confsched2019.contributor.item

import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.contributor.R
import io.github.droidkaigi.confsched2019.model.Contributor
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import javax.inject.Inject
import io.github.droidkaigi.confsched2019.contributor.databinding.ItemContributorBinding

class ContributorItem(
    private val contributor: Contributor
) : BindableItem<ItemContributorBinding>() {

    @Inject
    lateinit var activityActionCreator: ActivityActionCreator

    override fun getLayout(): Int = R.layout.item_contributor

    override fun bind(
        binding: ItemContributorBinding,
        index: Int
    ) {
        binding.contributor = contributor
        binding.rank = "${index + 1}"

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
}
