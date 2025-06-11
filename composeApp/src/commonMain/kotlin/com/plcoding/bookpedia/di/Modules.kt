package com.plcoding.bookpedia.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.bookpedia.recipe.data.database.DatabaseFactory
import com.plcoding.bookpedia.recipe.data.database.RecipeDatabase
import com.plcoding.bookpedia.recipe.data.repository.DefaultRecipeRepository
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailViewModel
import com.plcoding.bookpedia.recipe.presentation.recipe_list.RecipeListViewModel

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    singleOf(::DefaultRecipeRepository).bind<RecipeRepository>()

    viewModelOf(::RecipeListViewModel)
    viewModelOf(::RecipeDetailViewModel)

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<RecipeDatabase>().recipeDao }
}