package com.huymee.android.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {
    private val photoRepository = PhotoRepository()

    private val _galleryItems: MutableStateFlow<List<GalleryItem>> =
        MutableStateFlow(emptyList())
    val galleryItems: StateFlow<List<GalleryItem>>
        get() = _galleryItems.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val items = photoRepository.fetchPhotos()
                Log.i(TAG, "Items received: $items")
                _galleryItems.value = items
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch gallery items", e)
            }
        }
    }
}