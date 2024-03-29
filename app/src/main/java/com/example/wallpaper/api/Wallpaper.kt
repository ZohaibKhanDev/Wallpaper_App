package com.example.wallpaper.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    @SerialName("next_page")
    val nextPage: String?=null,
    @SerialName("page")
    val page: Int?=null,
    @SerialName("per_page")
    val perPage: Int?=null,
    @SerialName("photos")
    val photos: List<Photo>?=null,
    @SerialName("total_results")
    val totalResults: Int?=null
)