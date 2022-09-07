package com.bogleo.musicapp.screens.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bogleo.musicapp.R
import com.bogleo.musicapp.adaptors.pager.TabsPageAdapter
import com.bogleo.musicapp.adaptors.recycler.TabSliderAdapter
import com.bogleo.musicapp.adaptors.recycler.other.TabSliderLayoutManager
import com.bogleo.musicapp.common.ScreenUtils
import com.bogleo.musicapp.databinding.FragmentHomeBinding
import com.bogleo.musicapp.screens.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val mainViewModel : MainViewModel by activityViewModels()

    @Inject
    lateinit var tabSliderAdapter: TabSliderAdapter

    @Inject
    lateinit var tabSliderLayoutManager: TabSliderLayoutManager

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val startingTabIndex = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screenCenter = ScreenUtils.getScreenWidth(requireContext()) / 2
        val tabs = listOf(
            resources.getString(R.string.favorites),
            resources.getString(R.string.playlists),
            resources.getString(R.string.tracks),
            resources.getString(R.string.albums),
            resources.getString(R.string.artists)
        )

        with(binding) {
            // Configure Sliding Tabs
            with(recyclerTabSlider) {
                adapter = tabSliderAdapter.apply {
                    setData(tabs)
                    setOnClickListener { position: Int ->
                        smoothScrollToPosition(position)
                    }
                }
                layoutManager = tabSliderLayoutManager.apply {
                    onItemSelectCallback = ::onTabSelectCallback
                }
                setPadding(screenCenter, 0, screenCenter, 0)
            }

            // Configure Pager
            with(viewPagerContent) {
                adapter = TabsPageAdapter(this@HomeFragment)
                registerOnPageChangeCallback(onPageChangeCallback)
            }

            // Scroll to Tracks tab
            if (savedInstanceState == null) {
                viewPagerContent.currentItem = startingTabIndex
                recyclerTabSlider.smoothScrollToPosition(startingTabIndex)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onTabSelectCallback(layoutPosition: Int) {
        if (binding.viewPagerContent.currentItem != layoutPosition) {
            binding.viewPagerContent.currentItem = layoutPosition
        }
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.recyclerTabSlider.smoothScrollToPosition(position)
        }
    }
}