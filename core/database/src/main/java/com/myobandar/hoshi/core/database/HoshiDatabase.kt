package com.myobandar.hoshi.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myobandar.hoshi.core.database.dao.KanjiDao
import com.myobandar.hoshi.core.database.dao.KanjiRadicalDao
import com.myobandar.hoshi.core.database.dao.RadicalDao
import com.myobandar.hoshi.core.database.dao.UserKanjiDao
import com.myobandar.hoshi.core.database.model.KanjiEntity
import com.myobandar.hoshi.core.database.model.KanjiRadicalEntity
import com.myobandar.hoshi.core.database.model.RadicalEntity
import com.myobandar.hoshi.core.database.model.UserKanjiEntity

@Database(
    entities = [
        KanjiEntity::class,
        RadicalEntity::class,
        KanjiRadicalEntity::class,
        UserKanjiEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class HoshiDatabase : RoomDatabase() {
    abstract fun kanjiDao(): KanjiDao
    abstract fun radicalDao(): RadicalDao
    abstract fun kanjiRadicalDao(): KanjiRadicalDao
    abstract fun userKanjiDao(): UserKanjiDao
}
