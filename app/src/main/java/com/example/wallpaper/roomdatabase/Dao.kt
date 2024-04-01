package com.example.wallpaper.roomdatabase

import android.provider.ContactsContract.Intents.Insert
import androidx.room.Dao
import androidx.room.Query

@Dao
interface FavDao {
    @Query("SELECT * FROM FavItem")
    fun getAllFav():List<FavItem>
    @androidx.room.Insert
    fun Insert(favItem: FavItem)

}