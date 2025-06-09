package com.plcoding.recipepedia.recipe.presentation.recipe_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.presentation.recipe_list.components.RecipeListItem

@Composable
fun RecipeList(
    recipes: List<RecipeHeader>,
    onRecipeClick: (RecipeHeader) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeListItem(
                recipe = recipe,
                modifier = Modifier
                    .widthIn(max = 700.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onRecipeClick(recipe)
                }
            )
        }
    }
}