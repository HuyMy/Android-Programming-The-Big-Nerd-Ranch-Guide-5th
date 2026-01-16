package com.example.huymy.photogallery

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

private const val TAG = "PollWorker"

class PollWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val preferencesRepository = PreferencesRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferencesRepository.storedQuery.first()
        val lastId = preferencesRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.i(TAG, "No saved query, finishing early.")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhotos(query)

            if (items.isNotEmpty()) {
                val newResultId = items.first().id
                if (newResultId == lastId) {
                    Log.i(TAG, "Still have the same result: $newResultId")
                } else {
                    Log.i(TAG, "Got a new result: $newResultId")
                    preferencesRepository.setLastResultId(newResultId)
                }
            }

            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }
}