package io.github.droidkaigi.confsched2019.contributor.item

import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.contributor.R
import io.github.droidkaigi.confsched2019.contributor.databinding.ItemContributorProfileBinding
import io.github.droidkaigi.confsched2019.model.Contributor
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import javax.inject.Inject

class ContributorItem(
    private val contributor: Contributor
) : BindableItem<ItemContributorProfileBinding>() {

    @Inject
    lateinit var activityActionCreator: ActivityActionCreator

    override fun getLayout(): Int = R.layout.item_contributor_profile

    override fun bind(
        binding: ItemContributorProfileBinding,
        index: Int
    ) {
        binding.name = contributor.name
        binding.index = "${index + 1}"

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

        Picasso
            .get()
            .load(contributor.iconUrl)
            .transform(CropCircleTransformation())
            .apply {
                placeHolder?.let {
                    placeholder(it)
                }
            }
            .into(binding.contributorIconImageView)
    }
}
