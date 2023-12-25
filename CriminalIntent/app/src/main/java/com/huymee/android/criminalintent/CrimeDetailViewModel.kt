package com.huymee.android.criminalintent

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private const val PREF_KEY_IS_PERMISSION_DENIED = "PREF_KEY_IS_PERMISSION_DENIED"

class CrimeDetailViewModel(crimeId: UUID, application: Application) : ViewModel() {
    private val crimeRepository= CrimeRepository.get()

    private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?> = _crime.asStateFlow()
    private var appContext: Application
    var isPermissionDenied
        get() = Utils.getBooleanPreference(appContext, PREF_KEY_IS_PERMISSION_DENIED)
        set(value) = Utils.saveBooleanPreference(appContext, PREF_KEY_IS_PERMISSION_DENIED, value)


    init {
        appContext = application
        viewModelScope.launch {
            _crime.value = crimeRepository.getCrime(crimeId)
        }
    }

    fun updateCrime(onUpdate:(Crime) -> Crime) {
        _crime.update { oldCrime ->
            oldCrime?.let { onUpdate(it) }
        }
    }

    fun deleteCrime() {
        crime.value?.let { crimeRepository.deleteCrime(it) }
    }

    override fun onCleared() {
        super.onCleared()
        crime.value?.let { crimeRepository.updateCrime(it) }
    }

    fun getPhoneNumber(): String? {
        if (crime.value == null) {
            return null
        }
        val contactId = crime.value!!.contactId
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val queryFields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
        val selectionArgs = arrayOf(contactId)
        val queryCursor = appContext.contentResolver
            .query(uri, queryFields, selection, selectionArgs, null)
        queryCursor?.use {cursor ->
            return when {
                cursor.moveToFirst() -> cursor.getString(0)
                else -> null
            }
        }
        return null
    }
}

@Suppress("UNCHECKED_CAST")
class CrimeDetailViewModelFactory(
    private val crimeId: UUID,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailViewModel(crimeId, application) as T
    }
}