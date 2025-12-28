package com.example.huymy.photogallery

import android.app.Application

class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.intialize(this)
    }
}