package io.notable.shared.session

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private const val APP_DATASTORE = "app"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

@Singleton
class AppDataStoreManager
@Inject
constructor(
    val context: Application
) : AppDataStore {

    override suspend fun setValue(
        key: String,
        value: String
    ) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun readValue(
        key: String,
    ): String? {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
    }
}