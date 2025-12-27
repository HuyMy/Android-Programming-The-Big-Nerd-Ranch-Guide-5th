package com.example.huymy.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(@Query("page") page: Int): FlickrResponse

    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchPhotos(@QueryMap options: Map<String, String>): FlickrResponse
}