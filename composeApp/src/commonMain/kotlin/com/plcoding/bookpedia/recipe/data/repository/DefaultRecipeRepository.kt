package com.plcoding.bookpedia.recipe.data.repository

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

    override suspend fun getAllRecipeHeaders(): List<RecipeHeader> {
        delay(500L) // Simulate network/DB delay
        return _recipeHeaders.value
    }

    override suspend fun searchRecipes(query: String): List<RecipeHeader> {
        delay(300L) // Simulate search delay
        if (query.isBlank()) {
            return _recipeHeaders.value
        }
        return _recipeHeaders.value.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }

    override fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>> {
        // Return a flow that emits updates whenever the list of headers changes
        return _recipeHeaders.asStateFlow().map { headers ->
            headers.filter { it.isFavorite }
        }
    }

    override suspend fun getRecipeHeaderById(id: String): RecipeHeader? {
        return _recipeHeaders.value.find { it.id == id }
    }

    override suspend fun getVersionsForRecipe(headerId: String): List<RecipeVersion> {
        delay(200L) // Simulate delay
        return _recipeVersions.value.filter { it.recipeHeaderId == headerId }
            .sortedByDescending { it.createdAt }
    }

    override fun isRecipeFavorite(headerId: String): Flow<Boolean> {
        return _recipeHeaders.asStateFlow().map { headers ->
            headers.find { it.id == headerId }?.isFavorite ?: false
        }
    }

    override suspend fun markAsFavorite(headerId: String) {
        _recipeHeaders.update { currentHeaders ->
            currentHeaders.map { header ->
                if (header.id == headerId) {
                    header.copy(isFavorite = true)
                } else {
                    header
                }
            }
        }
    }

    override suspend fun removeFromFavorites(headerId: String) {
        _recipeHeaders.update { currentHeaders ->
            currentHeaders.map { header ->
                if (header.id == headerId) {
                    header.copy(isFavorite = false)
                } else {
                    header
                }
            }
        }
    }
}