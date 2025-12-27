package com.example.huymy.photogallery

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.huymy.photogallery.api.FlickrApi

private const val TAG = "PhotoPagingSource"

class PhotoPagingSource(
    private val flickrApi: FlickrApi,
    private val query: String
) : PagingSource<Int, GalleryItem>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val page = params.key ?: 1
        return try {
            val response = if (query.isBlank()) {
                flickrApi.fetchPhotos(page)
            } else {
                flickrApi.searchPhotos(mapOf("text" to query, "page" to page.toString()))
            }
            val photos = response.photos.galleryItems
            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e(TAG, "failed to load page $page: $e")
            LoadResult.Error(e)
        }
    }
}