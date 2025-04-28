package com.example.wethepeople

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PartyApp : Application(), ImageLoaderFactory {
    
    override fun onCreate() {
        super.onCreate()
        // Any other initialization can go here
    }
    
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of app memory for image cache
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02) // Use 2% of free disk space
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false) // Ignore cache headers for drawable resources
            .build()
    }
} 