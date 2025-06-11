package com.plcoding.bookpedia.recipe.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The main database class for the application.
 *
 * This abstract class extends RoomDatabase and serves as the main access point
 * to the persisted data.
 *
 * @property entities Lists all the data entity classes that are part of this database.
 * Room will create tables for each of these classes.
 * @property version The version of the database. This must be incremented whenever
 * the schema changes, along with providing a migration strategy.
 * @property typeConverters Links the custom TypeConverter class so Room knows how to
 * handle the MeasurementType enum.
 */
@Database(
    entities = [
        // This list must include ALL entity classes for the database.
        RecipeHeaderEntity::class,
        RecipeVersionEntity::class,
        CategoryEntity::class,
        MeasureUnitEntity::class,
        StandardIngredientEntity::class, // The master list of unique ingredients
        IngredientEntity::class,         // An ingredient line item within a recipe version
        InstructionStepEntity::class     // A direction step within a recipe version
    ],
    version = 1
)
@TypeConverters(RecipeTypeConverters::class)
@ConstructedBy(RecipeDatabaseConstructor::class)
abstract class RecipeDatabase : RoomDatabase() {

    /**
     * Provides access to the Data Access Object for Recipe-related operations.
     * Room will generate the implementation for this method.
     */
    abstract val recipeDao: RecipeDao

    /**
     * Provides access to the DAO for Category-related operations.
     * This follows the best practice of separating data access logic by feature/entity.
     * You would create a corresponding CategoryDao interface for this.
     */
    // abstract val categoryDao: CategoryDao // Uncomment when you create CategoryDao

    /**
     * Provides access to the DAO for standard ingredient operations (e.g., searching).
     */
    // abstract val standardIngredientDao: StandardIngredientDao // Uncomment when you create this DAO

    // Companion object can be used to define constants like the DB name if needed.
    companion object {
        const val DB_NAME = "that_recipe.db"
    }
}
