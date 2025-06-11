package com.plcoding.bookpedia.recipe.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
        val appContext = context.applicationContext

        return Room.databaseBuilder<RecipeDatabase>(
            context = appContext,
            // Best practice for Android is to provide just the filename.
            // Room handles creating the file in the correct location.
            name = RecipeDatabase.DB_NAME
        )
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                        scope.launch {
                            // Create a temporary instance of the DB for seeding
                            val tempDb = getDatabase(appContext)

                            // Use the DAO from the temporary instance to insert data
                            val dao = tempDb.recipeDao()
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

                            // It's good practice to close the temporary instance
                            tempDb.close()
                        }
                    }
                }
            )
    }
}

// This helper function now correctly receives the context it needs.
private fun getDatabase(context: Context): RecipeDatabase {
    return Room.databaseBuilder(
        context, // No need for applicationContext again, it's already done
        RecipeDatabase::class.java,
        RecipeDatabase.DB_NAME
    ).build()
}
