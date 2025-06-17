package com.plcoding.bookpedia.recipe.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

class ParseRecipeFromUrlUseCase(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(url: String): Result<Pair<RecipeHeader, RecipeVersion>, DataError> {
        if (!url.startsWith("http")) {
            // Basic validation
            return Result.Error(DataError.Remote.UNKNOWN) // Or a new validation error type
        }
        return recipeRepository.parseRecipeFromUrl(url)
    }
}