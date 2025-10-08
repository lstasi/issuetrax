package com.issuetrax.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.issuetrax.app.domain.repository.RepositoryContextRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "repository_context")

class RepositoryContextRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RepositoryContextRepository {
    
    companion object {
        private val OWNER_KEY = stringPreferencesKey("selected_repo_owner")
        private val REPO_KEY = stringPreferencesKey("selected_repo_name")
    }
    
    override suspend fun saveSelectedRepository(owner: String, repo: String) {
        context.dataStore.edit { preferences ->
            preferences[OWNER_KEY] = owner
            preferences[REPO_KEY] = repo
        }
    }
    
    override fun getSelectedRepository(): Flow<Pair<String, String>?> {
        return context.dataStore.data.map { preferences ->
            val owner = preferences[OWNER_KEY]
            val repo = preferences[REPO_KEY]
            if (owner != null && repo != null) {
                Pair(owner, repo)
            } else {
                null
            }
        }
    }
    
    override suspend fun clearSelectedRepository() {
        context.dataStore.edit { preferences ->
            preferences.remove(OWNER_KEY)
            preferences.remove(REPO_KEY)
        }
    }
}
