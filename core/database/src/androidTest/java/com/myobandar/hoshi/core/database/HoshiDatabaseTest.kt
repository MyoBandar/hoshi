package com.myobandar.hoshi.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.myobandar.hoshi.core.database.model.KanjiEntity
import com.myobandar.hoshi.core.database.model.KanjiRadicalEntity
import com.myobandar.hoshi.core.database.model.RadicalEntity
import com.myobandar.hoshi.core.database.model.UserKanjiEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HoshiDatabaseTest {
    private lateinit var database: HoshiDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HoshiDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun opensEmptyDatabase() = runTest {
        assertNull(database.kanjiDao().get("休"))
        assertNull(database.radicalDao().get("亻"))
        assertNull(database.userKanjiDao().get("休"))
        assertEquals(emptyList<KanjiRadicalEntity>(), database.kanjiRadicalDao().getForKanji("休"))
    }

    @Test
    fun storesKanjiWithListFields() = runTest {
        val entity = KanjiEntity(
            character = "休",
            meanings = listOf("rest", "day off"),
            onyomi = listOf("キュウ"),
            kunyomi = listOf("やす.む"),
            strokeCount = 6,
            jlptLevel = 4
        )

        database.kanjiDao().upsert(entity)

        assertEquals(entity, database.kanjiDao().get("休"))
    }

    @Test
    fun storesRadicalAndKanjiRadicalLinks() = runTest {
        database.kanjiDao().upsert(
            KanjiEntity(
                character = "休",
                meanings = listOf("rest"),
                onyomi = emptyList(),
                kunyomi = emptyList(),
                strokeCount = 6,
                jlptLevel = null
            )
        )
        database.radicalDao().upsert(
            RadicalEntity(
                character = "亻",
                meaning = "person",
                strokeCount = 2
            )
        )
        database.kanjiRadicalDao().upsert(
            KanjiRadicalEntity(
                kanji = "休",
                radical = "亻",
                relationType = "COMPONENT"
            )
        )
        database.kanjiRadicalDao().upsert(
            KanjiRadicalEntity(
                kanji = "休",
                radical = "亻",
                relationType = "OFFICIAL_RADICAL"
            )
        )

        assertEquals(
            listOf(KanjiRadicalEntity("休", "亻", "OFFICIAL_RADICAL")),
            database.kanjiRadicalDao().getForKanji("休")
        )
    }

    @Test
    fun storesUserKanjiState() = runTest {
        database.kanjiDao().upsert(
            KanjiEntity(
                character = "体",
                meanings = listOf("body"),
                onyomi = listOf("タイ"),
                kunyomi = listOf("からだ"),
                strokeCount = 7,
                jlptLevel = 4
            )
        )
        val state = UserKanjiEntity(
            kanji = "体",
            discoveredAt = 1_000L,
            lastSeenAt = 2_000L,
            discoveryCount = 3,
            masteryLevel = 1
        )

        database.userKanjiDao().upsert(state)

        assertEquals(state, database.userKanjiDao().get("体"))
    }
}
