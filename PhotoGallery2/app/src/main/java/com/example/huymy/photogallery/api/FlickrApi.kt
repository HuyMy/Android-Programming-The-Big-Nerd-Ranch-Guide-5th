package com.example.huymy.photogallery.api

import retrofit2.http.GET

private const val API_KEY = "9f01f1bec5a03a575a68d0340568af61"

interface FlickrApi {
    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=$API_KEY" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    suspend fun fetchPhotos(): FlickrResponse
}