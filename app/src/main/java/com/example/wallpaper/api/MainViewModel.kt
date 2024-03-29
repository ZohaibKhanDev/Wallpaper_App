package com.example.wallpaper.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val repository: Repository):ViewModel() {
    private val _allWallPaper= MutableStateFlow<ResultState<Wallpaper>>(ResultState.Loading)
    val allWallpaper:StateFlow<ResultState<Wallpaper>> =_allWallPaper.asStateFlow()


     fun getAllWallpaper(){
      viewModelScope.launch {
          _allWallPaper.value=ResultState.Loading
          try {
              val response=repository.getAllWallpaper()
              _allWallPaper.value=ResultState.Success(response)
          }catch (e:Exception){
              _allWallPaper.value=ResultState.Error(e)
          }
      }
    }
}