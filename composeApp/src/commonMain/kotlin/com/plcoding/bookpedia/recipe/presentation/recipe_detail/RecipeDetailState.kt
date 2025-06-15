package com.plcoding.bookpedia.recipe.presentation.recipe_detail

import com.plcoding.bookpedia.core.presentation.UiText
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.domain.RecipeVersion

data class RecipeDetailState(
    val isLoading: Boolean = true,
    val isPictureVisible: Boolean = true,
    val isMetaInfoExpanded: Boolean = true,

    val recipeHeader: RecipeHeader? = null,
    val allVersions: List<RecipeVersion> = emptyList(),
    val selectedVersion: RecipeVersion? = null,
    val checkedIngredientIds: Set<String> = emptySet(),
    val checkedStepIds: Set<String> = emptySet(),
    // Map of <InstructionStep.id, RemainingSeconds>
    val runningTimers: Map<String, Long> = emptyMap(),
    val pausedTimers: Set<String> = emptySet(),
    val errorMessage: UiText? = null
)
