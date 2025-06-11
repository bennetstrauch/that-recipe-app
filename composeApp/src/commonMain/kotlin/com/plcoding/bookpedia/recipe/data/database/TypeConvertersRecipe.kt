// In data/local/database/RecipeTypeConverters.kt

package com.plcoding.bookpedia.recipe.data.database

import androidx.room.TypeConverter
import com.plcoding.bookpedia.recipe.domain.MeasurementType

object RecipeTypeConverters {
    @TypeConverter
    fun fromMeasurementType(type: MeasurementType): String {
        return type.name
    }

    @TypeConverter
    fun toMeasurementType(name: String): MeasurementType {
        return try {
            MeasurementType.valueOf(name)
        } catch (e: IllegalArgumentException) {
            // Provide a safe fallback if the string from the DB is invalid
            MeasurementType.PIECE
        }
    }
}