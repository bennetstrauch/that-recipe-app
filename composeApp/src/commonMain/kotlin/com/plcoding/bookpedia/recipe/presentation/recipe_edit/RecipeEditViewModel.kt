package com.plcoding.bookpedia.recipe.presentation.recipe_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.UiText
import com.plcoding.bookpedia.core.presentation.toUiText
import com.plcoding.bookpedia.recipe.domain.*
import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.error_validation_fields_empty
import com.plcoding.bookpedia.app.RecipeEdit
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RecipeEditViewModel(
    private val recipeRepository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeEditState())
    val state = _state.asStateFlow()

    private var standardIngredientSearchJob: Job? = null

    init {
        val route = savedStateHandle.toRoute<RecipeEdit>()
        val headerId = route.recipeHeaderId
        val versionId = route.recipeVersionId

        if (headerId.isBlank()) {
            initializeNewRecipe()
        } else {
            loadRecipeForEditing(headerId, versionId)
        }
        loadDropdownData()
    }

    fun onAction(action: RecipeEditAction) {
        when (action) {
            // Header
            is RecipeEditAction.OnTitleChanged -> _state.update { it.copy(recipeHeader = it.recipeHeader?.copy(title = action.title)) }
            is RecipeEditAction.OnCategoryChanged -> _state.update { it.copy(recipeHeader = it.recipeHeader?.copy(category = action.category)) }
            is RecipeEditAction.OnPrepTimeChanged -> _state.update { it.copy(recipeHeader = it.recipeHeader?.copy(defaultPrepTimeMinutes = action.time.toIntOrNull())) }

            // Version
            is RecipeEditAction.OnVersionNameChanged -> _state.update { it.copy(selectedVersion = it.selectedVersion?.copy(versionName = action.name)) }
            is RecipeEditAction.OnVersionCommentaryChanged -> _state.update { it.copy(selectedVersion = it.selectedVersion?.copy(versionCommentary = action.commentary)) }

            // Ingredients
            is RecipeEditAction.OnAddNewIngredient -> addIngredient()
            is RecipeEditAction.OnDeleteIngredient -> deleteIngredient(action.index)
            is RecipeEditAction.OnUpdateIngredient -> updateIngredient(action.index, action.ingredient)
            is RecipeEditAction.OnSearchStandardIngredient -> searchStandardIngredients(action.query)
            is RecipeEditAction.OnSelectStandardIngredient -> selectStandardIngredient(action.index, action.standardIngredient)

            // Directions
            is RecipeEditAction.OnAddNewDirection -> addDirection()
            is RecipeEditAction.OnDeleteDirection -> deleteDirection(action.index)
            is RecipeEditAction.OnUpdateDirection -> updateDirection(action.index, action.step)

            // Saving
            is RecipeEditAction.OnOverwriteVersionClick -> saveChanges()
            is RecipeEditAction.OnSaveAsNewVersionClick -> saveAsNewVersion()

            else -> Unit
        }
    }

    private fun loadDropdownData() {
        viewModelScope.launch {
            // ## separate DAO for measureUnits
            val unitsResult = recipeRepository.getAllMeasureUnits()
//             val categoriesResult = recipeRepository.getAllCategories()

            unitsResult.onSuccess { units ->
                _state.update { it.copy(availableMeasureUnits = units) }
            }
        }
    }

    private fun initializeNewRecipe() {
        // Create a blank slate for the user to start with
        val newHeader = RecipeHeader(
            id = Uuid.random().toString(),
            title = "",
            category = Category("new_cat_id", "Uncategorized"), // Provide a default
            imageUrl = null,
            defaultPrepTimeMinutes = null,
            isFavorite = false
        )
        val newVersion = RecipeVersion(
            id = Uuid.random().toString(),
            recipeHeaderId = newHeader.id,
            versionName = "Original",
            versionCommentary = "",
            ingredients = emptyList(),
            directions = emptyList(),
            overridePrepTimeMinutes = null,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        _state.update { it.copy(isLoading = false, recipeHeader = newHeader, selectedVersion = newVersion) }
    }

    private fun loadRecipeForEditing(headerId: String, versionId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // First, get the header information.
            recipeRepository.getRecipeHeaderById(headerId)
                .onSuccess { header ->
                    // Then, get all available versions for this recipe.
                    recipeRepository.getVersionsForRecipe(headerId)
                        .onSuccess { allVersions ->
                            // Find the specific version
                            // If no ID was provided, default to the first (most recent) version.
                            val versionToEdit = allVersions.find { it.id == versionId } ?: allVersions.firstOrNull()

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    recipeHeader = header,
                                    // It's useful to keep all versions in state if you want to
                                    // add a dropdown to switch versions even while editing.
                                    allVersions = allVersions,
                                    selectedVersion = versionToEdit,
                                    isEditing = true,
                                    )
                            }
                        }
                        .onError { error -> _state.update { it.copy(isLoading = false, error = error.toUiText()) } }
                }
                .onError { error -> _state.update { it.copy(isLoading = false, error = error.toUiText()) } }
        }
    }

    // --- INGREDIENT HELPER FUNCTIONS ---

    private fun addIngredient() {
        _state.value.selectedVersion?.let { version ->
            // Create a default new ingredient
            val newIngredient = Ingredient(
                id = Uuid.random().toString(),
                customDisplayName = "",
                standardIngredient = StandardIngredient("new_std_id", "New Ingredient"), // Default
                quantity = 1.0,
                measureUnit = MeasureUnit("new_unit_id", "pcs", "pcs", MeasurementType.PIECE, 1.0, false) // Default
            )
            val updatedIngredients = version.ingredients + newIngredient
            _state.update { it.copy(selectedVersion = version.copy(ingredients = updatedIngredients)) }
        }
    }

    private fun deleteIngredient(index: Int) {
        _state.value.selectedVersion?.let { version ->
            val updatedIngredients = version.ingredients.toMutableList().apply {
                removeAt(index)
            }
            _state.update { it.copy(selectedVersion = version.copy(ingredients = updatedIngredients)) }
        }
    }

    private fun updateIngredient(index: Int, ingredient: Ingredient) {
        _state.value.selectedVersion?.let { version ->
            val updatedIngredients = version.ingredients.toMutableList().apply {
                // Correct syntax to update an item at a specific index
                this[index] = ingredient
            }
            _state.update { it.copy(selectedVersion = version.copy(ingredients = updatedIngredients)) }
        }
    }

    private fun searchStandardIngredients(query: String) {
        standardIngredientSearchJob?.cancel()
        standardIngredientSearchJob = viewModelScope.launch {
            delay(200L) // Debounce to avoid searching on every keystroke
            if (query.isNotBlank()) {
                recipeRepository.searchStandardIngredients(query)
                    .onSuccess { results ->
                        _state.update { it.copy(standardIngredientSearchResults = results) }
                    }
            } else {
                _state.update { it.copy(standardIngredientSearchResults = emptyList()) }
            }
        }
    }

    private fun selectStandardIngredient(ingredientIndex: Int, standardIngredient: StandardIngredient) {
        _state.value.selectedVersion?.let { version ->
            val updatedIngredients = version.ingredients.toMutableList().apply {
                val oldIngredient = this[ingredientIndex]
                this[ingredientIndex] = oldIngredient.copy(
                    standardIngredient = standardIngredient,
                    customDisplayName = standardIngredient.name
                )
            }
            _state.update {
                it.copy(
                    selectedVersion = version.copy(ingredients = updatedIngredients),
                    standardIngredientSearchResults = emptyList() // Clear/hide search results after selection
                )
            }
        }
    }

    // --- DIRECTION HELPER FUNCTIONS ---

    private fun addDirection() {
        _state.value.selectedVersion?.let { version ->
            val newDirection = InstructionStep(
                id = Uuid.random().toString(),
                description = "",
                timerInfo = null
            )
            val updatedDirections = version.directions + newDirection
            _state.update { it.copy(selectedVersion = version.copy(directions = updatedDirections)) }
        }
    }

    private fun deleteDirection(index: Int) {
        _state.value.selectedVersion?.let { version ->
            val updatedDirections = version.directions.toMutableList().apply {
                removeAt(index)
            }
            _state.update { it.copy(selectedVersion = version.copy(directions = updatedDirections)) }
        }
    }

    private fun updateDirection(index: Int, step: InstructionStep) {
        _state.value.selectedVersion?.let { version ->
            val updatedDirections = version.directions.toMutableList().apply {
                // Correct syntax to update an item at a specific index
                this[index] = step
            }
            _state.update { it.copy(selectedVersion = version.copy(directions = updatedDirections)) }
        }
    }

//    --- SAVE & VALIDATION ---

    private fun validateInput(): Boolean {
        val header = state.value.recipeHeader
        val version = state.value.selectedVersion
        if (header == null || version == null || header.title.isBlank() || version.versionName.isBlank()) {
            _state.update {
                it.copy(error = UiText.StringResourceId(Res.string.error_validation_fields_empty))
            }
            return false
        }
        _state.update { it.copy(error = null) } // Clear previous validation errors
        return true
    }

    private fun saveChanges() {
        // Calls the new helper with the correct repository function
        performSave(recipeRepository::saveRecipeChanges)
    }

    private fun saveAsNewVersion() {
        // Calls the new helper with the correct repository function
        performSave(recipeRepository::saveAsNewVersion)
    }

    private fun performSave(
        saveAction: suspend (RecipeHeader, RecipeVersion) -> EmptyResult<DataError>
    ) {
        if (!validateInput()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            var header = state.value.recipeHeader!!
            var version = state.value.selectedVersion!!

//            #should i not check if it is necessary to update ? { wrap the next to line in this check?
            val updatedIngredients = createNewStandardIngredientsIfNecessary(version.ingredients)
            println("updatedIngredients: ${updatedIngredients.toString()}. #")
            version = version.copy(ingredients = updatedIngredients)

            saveAction(header, version).onSuccess {
                _state.update { it.copy(isSaving = false, isFinished = true) }
            }.onError { error ->
                _state.update { it.copy(isSaving = false, error = error.toUiText()) }
            }
        }
    }



    private suspend fun createNewStandardIngredientsIfNecessary(ingredients: List<Ingredient>): List<Ingredient> {
        return ingredients.map { ingredient ->
            if (ingredient.standardIngredient.id == "new_std_id") {
                val newStandard = StandardIngredient(id = Uuid.random().toString(), name = ingredient.customDisplayName, density = null)
                println("newStandard: ${newStandard.toString()}")
                recipeRepository.insertStandardIngredient(newStandard)
                ingredient.copy(standardIngredient = newStandard)
            } else {
                ingredient
            }
        }
    }
}
