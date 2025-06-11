package com.plcoding.bookpedia.recipe.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeHeaderEntity(
    // Foreign key definition moved inside the annotation for clarity
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String?,
    val imageUrl: String?,
    val defaultPrepTimeMinutes: Int?,
    val isFavorite: Boolean
)
