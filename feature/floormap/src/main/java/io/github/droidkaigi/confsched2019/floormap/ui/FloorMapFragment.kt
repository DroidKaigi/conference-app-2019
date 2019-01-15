package io.github.droidkaigi.confsched2019.floormap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.floormap.R
import io.github.droidkaigi.confsched2019.floormap.databinding.FragmentFloorMapBinding
import io.github.droidkaigi.confsched2019.floormap.databinding.ItemFloorMapBinding
import io.github.droidkaigi.confsched2019.floormap.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.model.FloorMap

class FloorMapFragment : DaggerFragment() {

    private lateinit var binding: FragmentFloorMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_floor_map,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFloorMapPager()
    }

    private fun setupFloorMapPager() {
        binding.floorMapTabLayout.setupWithViewPager(binding.floorMapViewPager)
        binding.floorMapViewPager.adapter = object : PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val itemBinding = DataBindingUtil.inflate<ItemFloorMapBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_floor_map,
                    container,
                    true
                )
                val resId = when (FloorMap.values()[position]) {
                    FloorMap.FLOOR_1 -> R.drawable.ic_floor1
                    FloorMap.FLOOR_5 -> R.drawable.ic_floor2
                    else -> null
                }
                resId?.let { itemBinding.floorMapImage.setImageResource(resId) }
                itemBinding.floorTitle = getPageTitle(position)
                return itemBinding.root
            }

            override fun getPageTitle(position: Int) = FloorMap.values()[position].title

            override fun getCount() = FloorMap.values().size

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }
        }
    }
}

@Module
abstract class FloorMapFragmentModule {
    @Module
    companion object {
        @PageScope @JvmStatic @Provides fun providesLifecycle(
            floorMapFragment: FloorMapFragment
        ): Lifecycle {
            return floorMapFragment.viewLifecycleOwner.lifecycle
        }
    }
}
