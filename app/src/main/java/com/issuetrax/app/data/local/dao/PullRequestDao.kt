package com.issuetrax.app.data.local.dao

import androidx.room.*
import com.issuetrax.app.data.local.entity.PullRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PullRequestDao {
    
    @Query("SELECT * FROM pull_requests WHERE repositoryFullName = :repositoryFullName AND state = :state ORDER BY updatedAt DESC")
    fun getPullRequestsByRepository(repositoryFullName: String, state: String): Flow<List<PullRequestEntity>>
    
    @Query("SELECT * FROM pull_requests WHERE id = :id")
    suspend fun getPullRequestById(id: Long): PullRequestEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPullRequests(pullRequests: List<PullRequestEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPullRequest(pullRequest: PullRequestEntity)
    
    @Delete
    suspend fun deletePullRequest(pullRequest: PullRequestEntity)
    
    @Query("DELETE FROM pull_requests WHERE repositoryFullName = :repositoryFullName")
    suspend fun deleteAllPullRequestsForRepository(repositoryFullName: String)
    
    @Query("DELETE FROM pull_requests WHERE lastSyncAt < :cutoffDate")
    suspend fun deleteOldPullRequests(cutoffDate: java.time.LocalDateTime)
}