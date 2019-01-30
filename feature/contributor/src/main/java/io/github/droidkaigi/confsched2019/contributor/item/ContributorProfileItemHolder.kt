package io.github.droidkaigi.confsched2019.contributor.item

import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.contributor.R
import io.github.droidkaigi.confsched2019.contributor.bindingmodel.ContributorBindingModel
import io.github.droidkaigi.confsched2019.contributor.databinding.ItemContributorProfileBinding
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ContributorProfileItemHolder(
    private val bindingModel: ContributorBindingModel,
    private val onProfileClicked: (profileUrl: String) -> Unit
) : BindableItem<ItemContributorProfileBinding>() {

    override fun getLayout(): Int = R.layout.item_contributor_profile

    override fun bind(
        binding: ItemContributorProfileBinding,
        index: Int
    ) {
        binding.bindingModel = bindingModel

        binding.index = (bindingModel.index + 1).toString()

        binding.root.setOnClickListener {
            bindingModel.profileUrl?.let {
                onProfileClicked(bindingModel.profileUrl)
            }
        }

        Picasso
            .get()
            .load(bindingModel.iconUrl)
            .transform(CropCircleTransformation())
            .into(binding.contributorIconImageView)
    }
}
