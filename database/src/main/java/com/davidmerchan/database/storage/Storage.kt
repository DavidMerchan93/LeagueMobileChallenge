package com.davidmerchan.database.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Storage
    @Inject
    constructor(
        private val security: Security,
        private val context: Context,
    ) {
        private val Context.securePrefsDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "league_secure_prefs",
        )

        suspend fun saveSecureString(
            key: String,
            value: String,
        ) {
            val encrypted = security.encrypt(value)
            context.securePrefsDataStore.edit { prefs ->
                prefs[stringPreferencesKey(key)] = encrypted
            }
        }

        fun readSecureString(key: String): Flow<String?> =
            context.securePrefsDataStore.data.map { prefs ->
                prefs[stringPreferencesKey(key)]?.let { security.decrypt(it) }
            }
    }
