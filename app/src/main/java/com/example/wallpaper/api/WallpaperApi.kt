package com.example.wallpaper.api

import androidx.room.Query

interface WallpaperApi {
  suspend  fun getAllWallpaper(per_page: Int):Wallpaper

    suspend fun searchWallPaper(per_page: Int,query: String):Wallpaper
}