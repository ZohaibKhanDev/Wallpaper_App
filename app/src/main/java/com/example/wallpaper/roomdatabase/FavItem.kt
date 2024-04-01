package com.example.wallpaper.roomdatabase

import androidx.room.Entity

@Entity
data class FavItem(
    val id:Int?,
    val tittle:String,
    val des:String
)
