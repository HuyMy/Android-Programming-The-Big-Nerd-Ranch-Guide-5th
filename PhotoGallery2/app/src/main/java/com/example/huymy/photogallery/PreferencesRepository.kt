package com.example.huymy.photogallery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {
    val storedQuery: Flow<String> = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }.distinctUntilChanged()

    suspend fun setLastResultId(lastResultId: String) {
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")

        private var INSTANCE: PreferencesRepository? = null

        fun intialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }

                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException(
                "PreferencesRepository must be initialized"
            )
        }
    }
}