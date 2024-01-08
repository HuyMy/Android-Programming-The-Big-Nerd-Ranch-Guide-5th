package com.huymee.android.photogallery.api

import com.huymee.android.photogallery.GalleryItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoResponse(
    @Json(name = "photo")val galleryItems: List<GalleryItem>
)
