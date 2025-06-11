package com.plcoding.bookpedia.recipe.data.database

//import android.content.Context
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//actual class DatabaseFactory(
//    private val context: Context
//) {
//    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
//        val appContext = context.applicationContext
//        val dbFile = appContext.getDatabasePath(RecipeDatabase.DB_NAME)
//
//        return Room.databaseBuilder(
//            context = appContext,
//            name = dbFile.absolutePath
//        )
//    }
//}