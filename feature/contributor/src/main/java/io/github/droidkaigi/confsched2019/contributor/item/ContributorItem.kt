package io.github.droidkaigi.confsched2019.contributor.item

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
        binding.index = (index + 1).toString()

        binding.root.setOnClickListener {
            contributor.profileUrl?.let {
                activityActionCreator.openUrl(it)
            }

        }

        Picasso
            .get()
            .load(contributor.iconUrl)
            .transform(CropCircleTransformation())
            .into(binding.contributorIconImageView)
    }
}
