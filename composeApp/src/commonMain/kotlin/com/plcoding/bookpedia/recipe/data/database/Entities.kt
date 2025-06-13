package com.plcoding.bookpedia.recipe.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

// --- TABLE ENTITIES ---
// These still represent the flat tables in your database.

@Entity
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity
data class StandardIngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val density: Double?
)

@Entity
data class RecipeHeaderEntity(
    // Foreign key definition moved inside the annotation for clarity
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String?,
    val imageUrl: String?,
    val defaultPrepTimeMinutes: Int?,
    val isFavorite: Boolean
)

@Entity
data class RecipeVersionEntity(
    @PrimaryKey val id: String,
    val recipeHeaderId: String, // This will be a foreign key
    val versionName: String,
    val versionCommentary: String?,
    val overridePrepTimeMinutes: Int?,
    val createdAt: Long
)
// --- NEW ENTITIES FOR INGREDIENTS AND DIRECTIONS ---

@Entity(
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeVersionEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeVersionId"],
            onDelete = ForeignKey.CASCADE // If a version is deleted, its ingredients are deleted
        ),
        // You can also add foreign keys for standardIngredientId and measureUnitId
    ]
)
data class IngredientEntity(
    val id: String,
    val recipeVersionId: String, // Foreign key to link to a specific recipe version
    val customDisplayName: String,

    val standardIngredientId: String,
    val quantity: Double,
    val measureUnitId: String,
    val itemOrder: Int // Crucial for maintaining the list order
)

@Entity(
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeVersionEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeVersionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InstructionStepEntity(
    val id: String,
    val recipeVersionId: String, // Foreign key
    val description: String,
    val timerDurationSeconds: Long?, // The simple TimerInfo is embedded here
    val itemOrder: Int // Crucial for maintaining the list order
)


@Entity
data class MeasureUnitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val abbreviation: String?,
    val measurementType: String, // Stored as String from Enum
    val conversionFactorToSystemBase: Double,
    val isSystemUnit: Boolean
)


// --- NEW RELATIONSHIP (QUERY RESULT) CLASSES ---
// These are NOT tables. They are used to receive the results of queries
// that combine data from multiple tables.

data class RecipeHeaderTransferEntity(
    @Embedded
    val header: RecipeHeaderEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)

data class RecipeVersionTransferEntity(
    @Embedded
    val version: RecipeVersionEntity,

    @Relation(
        parentColumn = "id", // The id of the RecipeVersionEntity
        entityColumn = "recipeVersionId" // The linking column in the child tables
    )
    val ingredients: List<IngredientEntity>, // Room will fetch all matching ingredients

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeVersionId"
    )
    val directions: List<InstructionStepEntity> // Room will fetch all matching steps
)