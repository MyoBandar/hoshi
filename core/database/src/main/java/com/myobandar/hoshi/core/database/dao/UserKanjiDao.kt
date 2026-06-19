package com.myobandar.hoshi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myobandar.hoshi.core.database.model.UserKanjiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserKanjiDao {
    @Upsert
    suspend fun upsert(userKanji: UserKanjiEntity)

    @Query("SELECT * FROM user_kanji WHERE kanji = :kanji")
    suspend fun get(kanji: String): UserKanjiEntity?

    @Query("SELECT * FROM user_kanji ORDER BY discovered_at DESC")
    fun observeDiscovered(): Flow<List<UserKanjiEntity>>
}
