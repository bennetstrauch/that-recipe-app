package com.plcoding.bookpedia.recipe.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * A UseCase dedicated to fetching all necessary details for a recipe screen (view or edit).
 * It combines the header and version flows into a single, cohesive state.
 */
class GetRecipeDetailsUseCase(
    private val recipeRepository: RecipeRepository
) {
    operator fun invoke(
        headerId: String,
        versionId: String?
    ): Flow<Result<Pair<RecipeHeader, RecipeVersion?>?, DataError>> {
        val headerFlow = recipeRepository.getRecipeHeaderById(headerId)
        val versionsFlow = recipeRepository.getVersionsForRecipe(headerId)

        return combine(headerFlow, versionsFlow) { headerResult, versionsResult ->
            // Use a let block to proceed only if the header was successful and found
            headerResult.onSuccess { header ->
                if (header == null) {
                    // This is a success case where the item just doesn't exist
                    return@combine Result.Success(null)
                }

                versionsResult.onSuccess { allVersions ->
                    val selectedVersion = allVersions.find { it.id == versionId } ?: allVersions.firstOrNull()
                    // Return a pair of the final header and selected version
                    return@combine Result.Success(header to selectedVersion)
                }
            }
            // If either flow has an error, propagate it.
            // Assuming header error is more critical.
            (headerResult as? Result.Error)
                ?: (versionsResult as? Result.Error)
                ?: Result.Error(DataError.Local.UNKNOWN)
        }
    }
}