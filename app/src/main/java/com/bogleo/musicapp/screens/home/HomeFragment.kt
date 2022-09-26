package com.bogleo.musicapp.screens.home

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bogleo.musicapp.R
import com.bogleo.musicapp.core.pager.TabsPageAdapter
import com.bogleo.musicapp.core.recycler.TabSliderAdapter
import com.bogleo.musicapp.core.recycler.managers.TabSliderLayoutManager
import com.bogleo.musicapp.common.extensions.*
import com.bogleo.musicapp.common.utils.ScreenUtils
import com.bogleo.musicapp.data.model.Song
import com.bogleo.musicapp.databinding.FragmentHomeBinding
import com.bogleo.musicapp.player.isPlayEnabled
import com.bogleo.musicapp.player.isPlaying
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

        with(binding) {
            // Configure Sliding Tabs
            with(recyclerTabSlider) {
                // Create tabs list, also set at [TabsPageAdapter]
                val tabs = listOf(
                    resources.getString(R.string.favorites),
                    resources.getString(R.string.playlists),
                    resources.getString(R.string.tracks),
                    resources.getString(R.string.albums),
                    resources.getString(R.string.artists)
                )
                adapter = tabSliderAdapter.apply {
                    setData(tabs)
                    setOnClickListener { position: Int ->
                        smoothScrollToPosition(position)
                    }
                }
                layoutManager = tabSliderLayoutManager.apply {
                    onItemSelectCallback = ::onTabSelectCallback
                }
                // Get screen center for Sliding Tabs scroll space
                val screenCenter = ScreenUtils.getScreenWidth(requireContext()) / 2
                setPadding(screenCenter, 0, screenCenter, 0)
            }

            // Configure Pager
            with(viewPagerContent) {
                adapter = TabsPageAdapter(this@HomeFragment)
                registerOnPageChangeCallback(onPageChangeCallback)
                isUserInputEnabled = false
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

            with(player) {
                // Start song title marquee
                txtPlayerTitleSmall.isSelected = true
                txtPlayerArtistSmall.isSelected = true
                txtPlayerTitleBig.isSelected = true
                txtPlayerArtistBig.isSelected = true

                // Bind onClick listeners
                imgBtnCollapse.setOnClickListener { collapsePlayer() }
                imgBtnPlaySmall.setOnClickListener { mainViewModel.playSong() }
                imgBtnPlayBig.setOnClickListener { mainViewModel.playSong() }
                imgBtnPauseSmall.setOnClickListener { mainViewModel.pauseSong() }
                imgBtnPauseBig.setOnClickListener { mainViewModel.pauseSong() }
                imgBtnNextSmall.setOnClickListener { mainViewModel.skipToNextSong() }
                imgBtnNextBig.setOnClickListener { mainViewModel.skipToNextSong() }
                imgBtnPrevSmall.setOnClickListener { mainViewModel.skipToPreviousSong() }
                imgBtnPrevBig.setOnClickListener { mainViewModel.skipToPreviousSong() }
            }

            mainViewModel.playbackState.observe(viewLifecycleOwner) { state: PlaybackStateCompat? ->
                state?.let { playbackState: PlaybackStateCompat ->
                    switchPlayPause(isPlaying = playbackState.isPlaying)
                }
            }

            mainViewModel.currentlyPlayingSong.observe(viewLifecycleOwner) { mediaItem: MediaMetadataCompat? ->
                mediaItem?.let {
                    it.toSong()?.let { song: Song ->
                        setPlayerData(song = song)
                    }
                }
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
                    binding.contentContainer.setMargins(bottom = 0)
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
                lerpAnimate(amount = slideOffset)
                    .lerpWidth(
                        start = imgPlayerAlbumCoverSmall.width,
                        stop = imgPlayerAlbumCoverBig.width
                    )
                    .lerpHeight(
                        start = imgPlayerAlbumCoverSmall.height,
                        stop = imgPlayerAlbumCoverBig.height
                    )
                    .lerpX(
                        start = imgPlayerAlbumCoverSmall.x,
                        stop = imgPlayerAlbumCoverBig.x
                    )
                    .lerpY(
                        start = imgPlayerAlbumCoverSmall.y,
                        stop = imgPlayerAlbumCoverBig.y
                    )
                    .apply()
            }
        }
    }

    private fun switchPlayPause(isPlaying: Boolean) {
        val playVisibility: Int
        val pauseVisibility: Int
        if(isPlaying) {
            playVisibility = View.GONE
            pauseVisibility = View.VISIBLE
        } else {
            playVisibility = View.VISIBLE
            pauseVisibility = View.GONE
        }
        with(binding.player) {
            imgBtnPlaySmall.visibility = playVisibility
            imgBtnPlayBig.visibility = playVisibility
            imgBtnPauseSmall.visibility = pauseVisibility
            imgBtnPauseBig.visibility = pauseVisibility
        }
    }

    private fun setPlayerData(song: Song) {
        with(binding.player) {
            txtPlayerTitleSmall.text = song.title
            txtPlayerTitleBig.text = song.title
            txtPlayerArtistSmall.text = song.artist
            txtPlayerArtistBig.text = song.artist
            imgPlayerAlbumCoverSmall.setImageURI(song.artworkUri)
            imgPlayerAlbumCoverBig.setImageURI(song.artworkUri)
            imgPlayerAlbumCoverTransit.setImageURI(song.artworkUri)
        }
    }
}