package com.plcoding.bookpedia.recipe.data.remote

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.recipe.data.remote.dto.RecipeDto // We will create this next

interface RecipeRemoteDataSource {
    suspend fun parseRecipeFromUrl(url: String): Result<RecipeDto, DataError>
}