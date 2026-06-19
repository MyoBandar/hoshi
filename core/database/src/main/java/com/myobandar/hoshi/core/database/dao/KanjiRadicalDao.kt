package com.myobandar.hoshi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.myobandar.hoshi.core.database.model.KanjiRadicalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KanjiRadicalDao {
    @Upsert
    suspend fun upsert(link: KanjiRadicalEntity)

    @Upsert
    suspend fun upsertAll(links: List<KanjiRadicalEntity>)

    @Query("SELECT * FROM kanji_radicals WHERE kanji = :kanji ORDER BY radical")
    suspend fun getForKanji(kanji: String): List<KanjiRadicalEntity>

    @Query("SELECT * FROM kanji_radicals WHERE radical = :radical ORDER BY kanji")
    fun observeForRadical(radical: String): Flow<List<KanjiRadicalEntity>>
}
