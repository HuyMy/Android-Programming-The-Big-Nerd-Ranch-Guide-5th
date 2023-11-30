package com.huymee.android.criminalintent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "CrimeListViewModel"
class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    val crimes = mutableListOf<Crime>()

    init {
        Log.i(TAG, "init starting")
        viewModelScope.launch {
            Log.i(TAG, "coroutine launched")
            crimes += loadCrimes()
            Log.i(TAG, "Loading crimes finished")
        }

    }

    suspend fun loadCrimes(): List<Crime> {
        return crimeRepository.getCrimes()
    }
}