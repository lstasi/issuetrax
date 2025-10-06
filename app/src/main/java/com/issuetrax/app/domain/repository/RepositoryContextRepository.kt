package com.issuetrax.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface RepositoryContextRepository {
    suspend fun saveSelectedRepository(owner: String, repo: String)
    fun getSelectedRepository(): Flow<Pair<String, String>?>
    suspend fun clearSelectedRepository()
}
