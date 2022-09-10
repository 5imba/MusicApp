package com.bogleo.musicapp.screens.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bogleo.musicapp.R
import com.bogleo.musicapp.adaptors.pager.TabsPageAdapter
import com.bogleo.musicapp.adaptors.recycler.TabSliderAdapter
import com.bogleo.musicapp.adaptors.recycler.managers.TabSliderLayoutManager
import com.bogleo.musicapp.common.extensions.*
import com.bogleo.musicapp.common.utils.ScreenUtils
import com.bogleo.musicapp.databinding.FragmentHomeBinding
import com.bogleo.musicapp.screens.viewmodels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

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
        // Get screen center for Sliding Tabs centering
        val screenCenter = ScreenUtils.getScreenWidth(requireContext()) / 2
        // Create tabs list, also set at [TabsPageAdapter]
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

            // Configure Player Bottom Sheet
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetPlayer)
            bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
            bottomSheetPlayer.setOnClickListener { expandPlayer() }
            player.imgBtnCollapse.setOnClickListener { collapsePlayer() }

            // Start song title marquee
            player.txtPlayerTitleSmall.isSelected = true
            player.txtPlayerArtistSmall.isSelected = true
            player.txtPlayerTitleBig.isSelected = true
            player.txtPlayerArtistBig.isSelected = true
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

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            onPlayerStateChange(newState = newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            animatePlayerSlide(slideOffset = slideOffset)
        }
    }

    private fun expandPlayer() {
        if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun collapsePlayer() {
        if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun onPlayerStateChange(newState: Int) {
        with(binding.player) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    playerContainerSmall.visibility = View.VISIBLE
                    playerContainerBig.visibility = View.GONE
                    imgPlayerAlbumCoverSmall.visibility = View.VISIBLE
                    imgPlayerAlbumCoverTransit.visibility = View.GONE
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    playerContainerSmall.visibility = View.GONE
                    playerContainerBig.visibility = View.VISIBLE
                    imgPlayerAlbumCoverBig.visibility = View.VISIBLE
                    imgPlayerAlbumCoverTransit.visibility = View.GONE

                }
                BottomSheetBehavior.STATE_DRAGGING -> {
                    playerContainerSmall.visibility = View.VISIBLE
                    playerContainerBig.visibility = View.VISIBLE
                }
                BottomSheetBehavior.STATE_HIDDEN -> {

                }
            }
        }
    }

    private fun animatePlayerSlide(slideOffset: Float) {
        val invertedSlideOffset = 1f - slideOffset
        with(binding.player) {
            // Content alpha animation
            binding.contentContainer.alpha = invertedSlideOffset
            playerContainerSmall.alpha = invertedSlideOffset
            playerContainerBig.alpha = slideOffset
            imgPlayerAlbumCoverSmall.visibility = View.INVISIBLE
            imgPlayerAlbumCoverBig.visibility = View.INVISIBLE

            // Album cover slide animation
            imgPlayerAlbumCoverTransit.apply {
                visibility = View.VISIBLE
                lerpWidth(
                    imgPlayerAlbumCoverSmall.width,
                    imgPlayerAlbumCoverBig.width,
                    slideOffset
                )
                lerpHeight(
                    imgPlayerAlbumCoverSmall.height,
                    imgPlayerAlbumCoverBig.height,
                    slideOffset
                )
                lerpX(
                    imgPlayerAlbumCoverSmall.x,
                    imgPlayerAlbumCoverBig.x,
                    slideOffset
                )
                lerpY(
                    imgPlayerAlbumCoverSmall.y,
                    imgPlayerAlbumCoverBig.y,
                    slideOffset
                )
            }
        }
    }
}