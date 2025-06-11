package com.plcoding.bookpedia.recipe.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeVersionEntity(
    @PrimaryKey val id: String,
    val recipeHeaderId: String, // This will be a foreign key
    val versionName: String,
    val versionCommentary: String?,
    val overridePrepTimeMinutes: Int?,
    val createdAt: Long
)