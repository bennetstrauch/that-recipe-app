package com.plcoding.bookpedia.recipe.data.database

import androidx.room.RoomDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseFactory {

    fun getDatabase(): RecipeDatabase
}


