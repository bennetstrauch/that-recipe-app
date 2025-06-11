package com.plcoding.bookpedia.recipe.data.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<RecipeDatabase>
}