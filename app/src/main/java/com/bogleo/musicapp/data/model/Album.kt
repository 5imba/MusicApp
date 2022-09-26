package com.bogleo.musicapp.data.model

data class Album(
    val title: String,
    val artist: String,
    val songs: List<Song>
)