package com.example.wallpaper

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class WallpaperSetter(private val context: Context) : AsyncTask<String, Void, Bitmap?>() {
       @SuppressLint("StaticFieldLeak")
    override fun doInBackground(vararg params: String): Bitmap? {
        val wallpaperUrl = params[0]
        return try {
            val inputStream = URL(wallpaperUrl).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(bitmap: Bitmap?) {
        super.onPostExecute(bitmap)
        bitmap?.let {
            try {
                WallpaperManager.getInstance(context).setBitmap(bitmap)
                Toast.makeText(context, "WallPaper Set SuccessFully", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
suspend fun fetchBitmapFromUrl(urlString: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val url = URL(urlString)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            bitmap = BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        bitmap
    }
}
suspend fun setWallpaper(context: Context, bitmap: Bitmap) {
    val wallpaperManager = WallpaperManager.getInstance(context)
    wallpaperManager.setBitmap(bitmap)
    if (wallpaperManager.isSetWallpaperAllowed){
        Toast.makeText(context, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show()
    }
}
