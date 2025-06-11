package com.plcoding.bookpedia.recipe.data

import com.plcoding.bookpedia.recipe.domain.*
import kotlinx.serialization.Serializable

// No DTO needed for simple types like Category, as they don't contain complex objects.
// We only need DTOs for the objects that will be part of a serialized list.

@Serializable
data class IngredientDto(
    val id: String,
    val name: String,
    val quantity: Double,
    val measureUnitId: String
)

@Serializable
data class InstructionStepDto(
    val id: String,
    val description: String,
    val timerInfo: TimerInfoDto? = null
)

@Serializable
data class TimerInfoDto(
    val durationSeconds: Long
)
