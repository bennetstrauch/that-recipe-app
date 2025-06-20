// In desktopMain/kotlin/com/plcoding/bookpedia/recipe/data/database/DatabaseFactory.desktop.kt

package com.plcoding.bookpedia.recipe.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseFactory {

    @Volatile
    private var instance: RecipeDatabase? = null

    actual fun getDatabase(): RecipeDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase().also {
                instance = it
                // Seed the database after it's created
                val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                scope.launch {
                    seedData(it)
                }
            }
        }
    }

    private fun buildDatabase(): RecipeDatabase {
        val dbFile = getDbFile()
        return Room.databaseBuilder<RecipeDatabase>(
            name = dbFile.absolutePath,
        ).build()
    }

    private suspend fun seedData(database: RecipeDatabase) {
        val dao = database.recipeDao
        // Insert your initial data here
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

    private fun getDbFile(): File {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "thatRecipe") // Use your app's name
            os.contains("mac") -> File(userHome, "Library/Application Support/thatRecipe")
            else -> File(userHome, ".local/share/thatRecipe")
        }
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        return File(appDataDir, RecipeDatabase.DB_NAME)
    }
}