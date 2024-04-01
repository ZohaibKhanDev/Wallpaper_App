package com.example.wallpaper.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaper.roomdatabase.FavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val repository: Repository):ViewModel() {
    private val _allWallPaper= MutableStateFlow<ResultState<Wallpaper>>(ResultState.Loading)
    val allWallpaper:StateFlow<ResultState<Wallpaper>> =_allWallPaper.asStateFlow()

    private val _allFav= MutableStateFlow<ResultState<List<FavItem>>>(ResultState.Loading)
    val allFav:StateFlow<ResultState<List<FavItem>>> = _allFav.asStateFlow()

    private val _allInsert= MutableStateFlow<ResultState<Unit>>(ResultState.Loading)
    val allInsert:StateFlow<ResultState<Unit>> = _allInsert.asStateFlow()
    fun getAllFav(){
        viewModelScope.launch {
            _allFav.value=ResultState.Loading
            try {
                val response=repository.getAllFav()
                _allFav.value=ResultState.Success(response)
            }catch (e:Exception){
                _allFav.value=ResultState.Error(e)
            }
        }
    }
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

    fun Insert(favItem: FavItem){
        viewModelScope.launch {
            _allInsert.value=ResultState.Loading
            try {
                val response=repository.Insert(favItem)
                _allInsert.value=ResultState.Success(response)
            }catch (e:Exception){
                _allInsert.value=ResultState.Error(e)
            }
        }
    }
}