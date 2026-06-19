package com.myobandar.hoshi.core.database

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrepackagedKanjiDatabaseTest {
    private lateinit var database: HoshiDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.deleteDatabase(DATABASE_NAME)
        database = HoshiDatabaseFactory.create(context)
        Log.d(TAG, "Opened prepackaged Room database from asset: $DATABASE_NAME")
    }

    @After
    fun tearDown() {
        database.close()
        ApplicationProvider.getApplicationContext<android.content.Context>()
            .deleteDatabase(DATABASE_NAME)
    }

    @Test
    fun loadsKanjiFromPrepackagedDatabase() = runTest {
        val kanji = checkNotNull(database.kanjiDao().get("休"))
        Log.d(TAG, "Loaded kanji=${kanji.character}, meanings=${kanji.meanings}, strokeCount=${kanji.strokeCount}")

        assertEquals("休", kanji.character)
        assertTrue(kanji.meanings.contains("rest"))
        assertEquals(6, kanji.strokeCount)
    }

    @Test
    fun loadsKanjiReadings() = runTest {
        val kanji = checkNotNull(database.kanjiDao().get("休"))
        Log.d(TAG, "Loaded readings for ${kanji.character}: onyomi=${kanji.onyomi}, kunyomi=${kanji.kunyomi}")

        assertTrue(kanji.onyomi.contains("キュウ"))
        assertTrue(kanji.kunyomi.any { it.startsWith("やす") })
    }

    @Test
    fun loadsKanjiRadicalLinks() = runTest {
        val links = database.kanjiRadicalDao().getForKanji("休")
        Log.d(TAG, "Loaded radical links for 休: $links")

        assertFalse(links.isEmpty())
        assertTrue(links.any { it.relationType == "COMPONENT" })
        assertTrue(links.any { it.radical == "人" && it.relationType == "OFFICIAL_RADICAL" })
        assertTrue(links.any { it.radical == "⺅" && it.relationType == "COMPONENT" })
        assertTrue(links.any { it.radical == "木" && it.relationType == "COMPONENT" })
    }

    @Test
    fun prepackagedDatabaseDoesNotSeedUserProgress() = runTest {
        val userKanji = database.userKanjiDao().get("休")
        Log.d(TAG, "Loaded user progress for 休: $userKanji")

        assertNull(userKanji)
    }

    private companion object {
        const val TAG = "PrepackagedKanjiDbTest"
        const val DATABASE_NAME = "hoshi.db"
    }
}
