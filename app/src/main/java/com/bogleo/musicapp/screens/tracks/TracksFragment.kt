package com.bogleo.musicapp.screens.tracks

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bogleo.musicapp.R
import com.bogleo.musicapp.core.recycler.ListItemAdapter
import com.bogleo.musicapp.core.recycler.listitems.SongListItem
import com.bogleo.musicapp.common.Resource
import com.bogleo.musicapp.common.Status
import com.bogleo.musicapp.common.extensions.toPx
import com.bogleo.musicapp.core.recycler.SwipeToPlayNext
import com.bogleo.musicapp.data.model.Song
import com.bogleo.musicapp.databinding.FragmentTracksBinding
import com.bogleo.musicapp.screens.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TracksFragment"

@AndroidEntryPoint
class TracksFragment : Fragment() {

    // TODO Inject it
    private lateinit var listItemAdapter: ListItemAdapter

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listItemAdapter = ListItemAdapter(getListItems())

        with(binding.recyclerTracks) {
            adapter = listItemAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )

            val decoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_hor_line)?.let {

                val insetDivider = InsetDrawable(it, 74.toPx, 0f, 0f, 0f)
                decoration.setDrawable(insetDivider)
            }

            addItemDecoration(decoration)

            val swipeToPlayNextCallback = SwipeToPlayNext(onSwipe = ::addSongToQueue)
            ItemTouchHelper(swipeToPlayNextCallback).attachToRecyclerView(this)
        }

        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result: Resource<List<Song>> ->
            when(result.status) {
                is Status.Loading -> setLoadingVisibility(isLoading = true)
                is Status.Success -> {
                    listItemAdapter.submitList(result.data)
                    setLoadingVisibility(isLoading = false)
                }
                is Status.Error<*> -> Log.e(TAG, result.status.message.toString())
            }
        }
    }

    private fun setLoadingVisibility(isLoading: Boolean) {
        with(binding) {
            if(isLoading) {
                progressBarTracks.visibility = View.VISIBLE
                recyclerTracks.visibility = View.GONE
            } else {
                progressBarTracks.visibility = View.GONE
                recyclerTracks.visibility = View.VISIBLE
            }
        }
    }

    private fun getListItems() = listOf(
        SongListItem(
            onClickListener = ::playSong,
            onLongClickListener = ::showSongMenu
        )
    )

    private fun playSong(song: Song) {
        mainViewModel.playOrToggleSong(mediaItem = song)
    }

    private fun showSongMenu(song: Song) {
        // TODO create song popup menu
        Snackbar.make(
            binding.root,
            "Long Press",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun addSongToQueue(song: Song) {
        Snackbar.make(
            binding.root,
            "Swiped",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}