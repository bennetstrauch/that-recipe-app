// In androidMain/com/plcoding/bookpedia/recipe/data/database/DatabaseFactory.kt

package com.plcoding.bookpedia.recipe.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// ##### take care of other implementations, make it claner
actual class DatabaseFactory(private val context: Context) {

    @Volatile
    private var instance: RecipeDatabase? = null

    actual fun getDatabase(): RecipeDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase().also { instance = it }
        }
    }

    private fun buildDatabase(): RecipeDatabase {
        val appContext = context.applicationContext
        return Room.databaseBuilder(
            appContext,
            RecipeDatabase::class.java,
            RecipeDatabase.DB_NAME
        )
            // DO NOT call .setDriver() here. Let Room use Android's default.
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                        scope.launch {
                            // Safely get the singleton instance to perform seeding
                            val dao = getDatabase().recipeDao

                            dao.upsertCategories(InitialData.categories)
                            dao.upsertMeasureUnits(InitialData.measureUnits)
                            dao.upsertStandardIngredients(InitialData.standardIngredients)

                            // Insert recipe 1
                            dao.upsertRecipeHeader(InitialData.applePieHeader)
                            dao.upsertRecipeVersion(InitialData.applePieVersion)
                            dao.upsertIngredients(InitialData.applePieIngredients)
                            dao.upsertInstructionSteps(InitialData.applePieDirections)

                            // Insert recipe 2
                            dao.upsertRecipeHeader(InitialData.chickenCurryHeader)
                            dao.upsertRecipeVersion(InitialData.chickenCurryVersion)
                            dao.upsertIngredients(InitialData.chickenCurryIngredients)
                            dao.upsertInstructionSteps(InitialData.chickenCurryDirections)
                        }



                    }
                }
            )
            .build()
    }
}