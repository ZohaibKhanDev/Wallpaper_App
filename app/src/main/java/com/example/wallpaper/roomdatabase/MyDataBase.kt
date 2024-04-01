package com.example.wallpaper.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [FavItem::class], version = 1)
abstract class MyDataBase:RoomDatabase() {
    abstract fun favDao():FavDao
}