package com.example.huymy.photogallery

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.huymy.photogallery.api.FlickrApi
import com.example.huymy.photogallery.api.PhotoInterceptor
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class PhotoRepository {
    private val flickrApi: FlickrApi

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        flickrApi = retrofit.create<FlickrApi>()
    }

    fun getPhotos(): Flow<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(flickrApi) }
        ).flow
    }
}