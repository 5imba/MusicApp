package com.bogleo.musicapp.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bogleo.musicapp.common.Resource
import com.bogleo.musicapp.data.model.Song

sealed class States {
    object Default : States()
    object Initializing : States()
    object Initialized : States()
    object Error : States()
}

class MusicProvider {

    private val _songList: MutableList<Song> = mutableListOf()
    private val _byName: MutableList<Int> = mutableListOf()
    private val _byDateAdded: MutableList<Int> = mutableListOf()
    private val _byArtists: MutableList<Int> = mutableListOf()
    private val _byAlbums: MutableList<Int> = mutableListOf()
    private val _byPlaylists: MutableList<Int> = mutableListOf()
    private val _byFavorites: MutableList<Int> = mutableListOf()

    fun byName(): List<Song> = _byName.map { index: Int ->
        _songList[index]
    }

    
}