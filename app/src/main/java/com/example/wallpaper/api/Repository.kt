package com.example.wallpaper.api

import androidx.room.RoomDatabase
import com.example.wallpaper.roomdatabase.FavItem
import com.example.wallpaper.roomdatabase.MyDataBase

class Repository(private val database: MyDataBase):WallpaperApi {

    suspend fun getAllFav():List<FavItem>{
        return database.favDao().getAllFav()
    }

    fun Insert(favItem: FavItem){
        return database.favDao().Insert(favItem)
    }

    override suspend fun getAllWallpaper(per_page: Int): Wallpaper {
        return WallpaperClientApi.getAllWallpaper(per_page)
    }

    override suspend fun searchWallPaper(per_page: Int, query: String): Wallpaper {
        return WallpaperClientApi.searchWallPaper(per_page,query)
    }

}