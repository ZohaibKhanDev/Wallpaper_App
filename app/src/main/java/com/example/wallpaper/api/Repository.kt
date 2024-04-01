package com.example.wallpaper.api

import androidx.room.RoomDatabase
import com.example.wallpaper.roomdatabase.FavItem
import com.example.wallpaper.roomdatabase.MyDataBase

class Repository(private val database: MyDataBase) {
     suspend fun getAllWallpaper(): Wallpaper {
        return WallpaperClientApi.getAllWallpaper()
     }
    suspend fun getAllFav():List<FavItem>{
        return database.favDao().getAllFav()
    }

    fun Insert(favItem: FavItem){
        return database.favDao().Insert(favItem)
    }

}