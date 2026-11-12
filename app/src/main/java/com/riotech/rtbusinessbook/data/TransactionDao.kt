package com.riotech.rtbusinessbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY dateMillis DESC, id DESC")
    fun observeActive(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun observeDeleted(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(t: TransactionEntity): Long

    @Update
    suspend fun update(t: TransactionEntity)

    @Query("UPDATE transactions SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("UPDATE transactions SET isDeleted = 0, updatedAt = :now WHERE id = :id")
    suspend fun restore(id: Long, now: Long)

    @Query("DELETE FROM transactions WHERE id = :id AND isDeleted = 1")
    suspend fun purge(id: Long)
}
