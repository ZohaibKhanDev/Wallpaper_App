package com.example.wallpaper.api

import android.accounts.AccountAuthenticatorResponse
import java.lang.Error

sealed class ResultState<out T>{
    object Loading:ResultState<Nothing>()
    data class Success<T>(val response: T):ResultState<T>()
    data class Error(val error: Throwable):ResultState<Nothing>()
}
