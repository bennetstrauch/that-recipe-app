package com.plcoding.bookpedia.recipe.data.repository

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.recipe.data.dummyRecipeHeaders
import com.plcoding.bookpedia.recipe.data.dummyRecipeVersions
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import com.plcoding.bookpedia.recipe.domain.RecipeVersion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DefaultRecipeRepository : RecipeRepository {

    // In-memory storage to simulate a database
    private val _recipeHeaders = MutableStateFlow(dummyRecipeHeaders)
    private val _recipeVersions = MutableStateFlow(dummyRecipeVersions)

    override suspend fun getAllRecipeHeaders(): Result<List<RecipeHeader>, DataError.Local> {
        return try {
            delay(500L) // Simulate network/DB delay
            Result.Success(_recipeHeaders.value)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun searchRecipes(query: String): Result<List<RecipeHeader>, DataError> {
        return try {
            delay(300L) // Simulate search delay
            val results = if (query.isBlank()) {
                _recipeHeaders.value
            } else {
                _recipeHeaders.value.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }
            Result.Success(results)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN) // Assuming local search for dummy repo
        }
    }

    override fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>> {
        // This function returns a Flow and was not changed in the interface.
        // The implementation remains the same.
        return _recipeHeaders.asStateFlow().map { headers ->
            headers.filter { it.isFavorite }
        }
    }

    override suspend fun getRecipeHeaderById(id: String): Result<RecipeHeader?, DataError.Local> {
        return try {
            val header = _recipeHeaders.value.find { it.id == id }
            Result.Success(header)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getVersionsForRecipe(headerId: String): Result<List<RecipeVersion>, DataError.Local> {
        return try {
            delay(200L) // Simulate delay
            val versions = _recipeVersions.value.filter { it.recipeHeaderId == headerId }
                .sortedByDescending { it.createdAt }
            Result.Success(versions)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun isRecipeFavorite(headerId: String): Flow<Boolean> {
        // This function returns a Flow and was not changed in the interface.
        return _recipeHeaders.asStateFlow().map { headers ->
            headers.find { it.id == headerId }?.isFavorite ?: false
        }
    }

    override suspend fun markAsFavorite(headerId: String): EmptyResult<DataError.Local> {
        return try {
            _recipeHeaders.update { currentHeaders ->
                currentHeaders.map { header ->
                    if (header.id == headerId) {
                        header.copy(isFavorite = true)
                    } else {
                        header
                    }
                }
            }
            Result.Success(Unit) // Return Success with Unit for EmptyResult
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun removeFromFavorites(headerId: String): EmptyResult<DataError.Local> {
        return try {
            _recipeHeaders.update { currentHeaders ->
                currentHeaders.map { header ->
                    if (header.id == headerId) {
                        header.copy(isFavorite = false)
                    } else {
                        header
                    }
                }
            }
            Result.Success(Unit) // Return Success with Unit for EmptyResult
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}