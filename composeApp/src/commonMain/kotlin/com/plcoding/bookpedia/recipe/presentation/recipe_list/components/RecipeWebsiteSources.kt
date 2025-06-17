package com.plcoding.bookpedia.recipe.presentation.recipe_list.components

import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.all_recipes_logo
import cmp_bookpedia.composeapp.generated.resources.joyful_belly_logo
import cmp_bookpedia.composeapp.generated.resources.medline_plus_logo
import cmp_bookpedia.composeapp.generated.resources.skinny_taste_logo
import org.jetbrains.compose.resources.DrawableResource


data class RecipeWebsite(
    val label: String, val url: String, val icon: DrawableResource
)

val recipeWebsites = listOf(
    RecipeWebsite("Healthy Recipes", "https://www.skinnytaste.com/", Res.drawable.skinny_taste_logo),
    RecipeWebsite("Ayurvedic Recipes", "https://www.joyfulbelly.com/Ayurveda/recipes", Res.drawable.joyful_belly_logo),
    RecipeWebsite("MedlinePlus", "https://medlineplus.gov/recipes/", Res.drawable.medline_plus_logo),
    RecipeWebsite("All Recipes", "https://www.allrecipes.com/", Res.drawable.all_recipes_logo),
)
