package com.bogleo.musicapp.core.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bogleo.musicapp.screens.albums.AlbumsFragment
import com.bogleo.musicapp.screens.artists.ArtistsFragment
import com.bogleo.musicapp.screens.favorites.FavoritesFragment
import com.bogleo.musicapp.screens.playlists.PlaylistsFragment
import com.bogleo.musicapp.screens.tracks.TracksFragment
import java.lang.IllegalArgumentException

class TabsPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FavoritesFragment()
            1 -> PlaylistsFragment()
            2 -> TracksFragment()
            3 -> AlbumsFragment()
            4 -> ArtistsFragment()
            else -> throw IllegalArgumentException("Pager index out of bounds: $position")
        }
    }
}