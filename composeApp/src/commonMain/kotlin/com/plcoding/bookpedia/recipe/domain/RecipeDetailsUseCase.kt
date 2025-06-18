package com.plcoding.bookpedia.recipe.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


// ## where to put this?
data class RecipeFullDetails(
    val header: RecipeHeader,
    val allVersions: List<RecipeVersion>,
    val selectedVersion: RecipeVersion?
)


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
    ): Flow<Result<RecipeFullDetails?, DataError>> {

        val headerFlow = recipeRepository.getRecipeHeaderById(headerId)
        val versionsFlow = recipeRepository.getVersionsForRecipe(headerId)

        return combine(headerFlow, versionsFlow) { headerResult, versionsResult ->
//  Error handling
            val headerError = (headerResult as? Result.Error)?.error
            if (headerError != null) {
                return@combine Result.Error(headerError)
            }
            val versionsError = (versionsResult as? Result.Error)?.error
            if (versionsError != null) {
                return@combine Result.Error(versionsError)
            }

            // At this point, we know both results are successful.
            val header = (headerResult as Result.Success).data
            val allVersions = (versionsResult as Result.Success).data

            if (header == null) {
                // Recipe not found case
                Result.Success(null)
            } else {
                // Recipe found, assemble the final details object
                val selectedVersion = allVersions.find { it.id == versionId } ?: allVersions.firstOrNull()
                val details = RecipeFullDetails(
                    header = header,
                    allVersions = allVersions,
                    selectedVersion = selectedVersion
                )
                Result.Success(details)
            }
        }
    }
}