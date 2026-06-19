package com.myobandar.hoshi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myobandar.hoshi.core.database.model.RadicalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RadicalDao {
    @Upsert
    suspend fun upsert(radical: RadicalEntity)

    @Upsert
    suspend fun upsertAll(radicals: List<RadicalEntity>)

    @Query("SELECT * FROM radicals WHERE character = :character")
    suspend fun get(character: String): RadicalEntity?

    @Query("SELECT * FROM radicals ORDER BY character")
    fun observeAll(): Flow<List<RadicalEntity>>
}
