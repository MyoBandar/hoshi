package com.myobandar.hoshi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myobandar.hoshi.core.database.model.KanjiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KanjiDao {
    @Upsert
    suspend fun upsert(kanji: KanjiEntity)

    @Upsert
    suspend fun upsertAll(kanji: List<KanjiEntity>)

    @Query("SELECT * FROM kanji WHERE character = :character")
    suspend fun get(character: String): KanjiEntity?

    @Query("SELECT * FROM kanji WHERE character = :character")
    fun observe(character: String): Flow<KanjiEntity?>

    @Query("SELECT * FROM kanji ORDER BY character")
    fun observeAll(): Flow<List<KanjiEntity>>
}
