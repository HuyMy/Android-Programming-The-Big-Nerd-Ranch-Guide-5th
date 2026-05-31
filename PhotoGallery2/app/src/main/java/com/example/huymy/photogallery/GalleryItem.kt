package com.example.huymy.photogallery

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.core.net.toUri

@JsonClass(generateAdapter = true)
data class GalleryItem(
    val title: String,
    val id: String,
    @Json(name = "url_s") val url: String,
    val owner: String
) {
    val photoPageUri: Uri
        get() = "https://www.flickr.com/photos".toUri()
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()
}
