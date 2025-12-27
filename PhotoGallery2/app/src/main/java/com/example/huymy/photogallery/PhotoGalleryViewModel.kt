package com.example.huymy.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {
    private val photoRepository = PhotoRepository()
    private val _query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val galleryItems: Flow<PagingData<GalleryItem>> = _query.flatMapLatest { query ->
        photoRepository.getPhotos(query)
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        _query.value = query
    }
}