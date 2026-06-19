package com.myobandar.hoshi.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

object HoshiDatabaseFactory {
    private const val DatabaseName = "hoshi.db"

    fun create(context: Context): HoshiDatabase =
        builder(context).build()

    fun builder(context: Context): RoomDatabase.Builder<HoshiDatabase> =
        Room.databaseBuilder(
            context.applicationContext,
            HoshiDatabase::class.java,
            DatabaseName
        ).createFromAsset(DatabaseName)
}
