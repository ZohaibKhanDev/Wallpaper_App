package com.example.wallpaper.api

import androidx.room.RoomDatabase

class Repository() {
     suspend fun getAllWallpaper(): Wallpaper {
        return WallpaperClientApi.getAllWallpaper()
     }


}