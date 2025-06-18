// In data/local/database/RecipeDao.kt

package com.plcoding.bookpedia.recipe.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.plcoding.bookpedia.recipe.domain.MeasureUnit
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // --- Transactional Write Operation ---
    @Transaction
    suspend fun saveFullVersion(
        header: RecipeHeaderEntity,
        version: RecipeVersionEntity,
        ingredients: List<IngredientEntity>,
        steps: List<InstructionStepEntity>
    ) {
        upsertRecipeHeader(header)
        upsertRecipeVersion(version)
        // #todo really needed?: You might want to delete old ingredients/steps for this version before upserting
        deleteIngredientsForVersion(version.id)
        deleteStepsForVersion(version.id)
        upsertIngredients(ingredients)
        upsertInstructionSteps(steps)
    }

    // --- Helper Deletes for the Transaction ---
    @Query("DELETE FROM IngredientEntity WHERE recipeVersionId = :versionId")
    suspend fun deleteIngredientsForVersion(versionId: String)

    @Query("DELETE FROM InstructionStepEntity WHERE recipeVersionId = :versionId")
    suspend fun deleteStepsForVersion(versionId: String)

    // --- DeleteRecipe Operations ---
    // Deletes a single version and its related ingredients/steps (since they have CASCADE onDelete)
    @Query("DELETE FROM RecipeVersionEntity WHERE id = :versionId")
    suspend fun deleteRecipeVersion(versionId: String)

    // Deletes a recipe header, which will cascade to all its versions and their children
    @Query("DELETE FROM RecipeHeaderEntity WHERE id = :headerId")
    suspend fun deleteRecipeHeader(headerId: String)


    // --- Write Operations for Seeding/Saving ---
    @Upsert
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    @Upsert
    suspend fun upsertMeasureUnits(units: List<MeasureUnitEntity>)

    @Upsert
    suspend fun upsertStandardIngredients(ingredients: List<StandardIngredientEntity>)

    @Upsert
    suspend fun upsertStandardIngredient(ingredient: StandardIngredientEntity)

    @Upsert
    suspend fun upsertRecipeHeader(header: RecipeHeaderEntity)

    @Upsert
    suspend fun upsertRecipeVersion(version: RecipeVersionEntity)

    @Upsert
    suspend fun upsertIngredients(ingredients: List<IngredientEntity>)

    @Upsert
    suspend fun upsertInstructionSteps(steps: List<InstructionStepEntity>)


    // --- Read Operations ---
    @Transaction
    @Query("SELECT * FROM RecipeHeaderEntity ORDER BY title ASC")
    fun getRecipeHeadersWithCategory(): Flow<List<RecipeHeaderTransferEntity>>

    @Transaction
    @Query("SELECT * FROM RecipeHeaderEntity WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchRecipeHeadersWithCategory(query: String): Flow<List<RecipeHeaderTransferEntity>> // New search Flow

    @Transaction
    @Query("SELECT * FROM RecipeHeaderEntity WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteRecipeHeadersWithCategory(): Flow<List<RecipeHeaderTransferEntity>>

    @Transaction
    @Query("SELECT * FROM RecipeHeaderEntity WHERE id = :id")
    fun getRecipeHeaderWithCategoryById(id: String): Flow<RecipeHeaderTransferEntity?>

    @Transaction
    @Query("SELECT * FROM RecipeVersionEntity WHERE recipeHeaderId = :headerId ORDER BY createdAt DESC")
    fun getRecipeVersionsWithDetails(headerId: String): Flow<List<RecipeVersionTransferEntity>>

    @Transaction
    @Query("SELECT * FROM MeasureUnitEntity")
    suspend fun getAllMeasureUnits(): List<MeasureUnitEntity>

    @Query("SELECT * FROM StandardIngredientEntity WHERE name LIKE '%' || :query || '%'")
    suspend fun searchStandardIngredients(query: String): List<StandardIngredientEntity>

    @Transaction
    @Query("SELECT * FROM CategoryEntity ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    // --- Efficient Batch Fetching for Mappers ---

    @Query("SELECT * FROM StandardIngredientEntity WHERE id IN (:ids)")
    suspend fun getStandardIngredientsByIds(ids: List<String>): List<StandardIngredientEntity>

    @Query("SELECT * FROM MeasureUnitEntity WHERE id IN (:ids)")
    suspend fun getMeasureUnitsByIds(ids: List<String>): List<MeasureUnitEntity>


    // --- Favorite Operations ---

    @Query("UPDATE RecipeHeaderEntity SET isFavorite = 1 WHERE id = :headerId")
    suspend fun markAsFavorite(headerId: String)

    @Query("UPDATE RecipeHeaderEntity SET isFavorite = 0 WHERE id = :headerId")
    suspend fun removeFromFavorites(headerId: String)


}