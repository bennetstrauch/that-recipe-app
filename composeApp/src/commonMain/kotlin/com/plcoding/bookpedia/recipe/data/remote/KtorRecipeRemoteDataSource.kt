package com.plcoding.bookpedia.recipe.data.remote

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.recipe.data.remote.dto.RecipeDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.Clock.System
import kotlinx.serialization.json.Json

class KtorRecipeRemoteDataSource(
    private val httpClient: HttpClient
    // In a real app, you would also inject a client for your AI service ####
) : RecipeRemoteDataSource {
    override suspend fun parseRecipeFromUrl(url: String): Result<RecipeDto, DataError> {
        return try {
            // 1. Scrape the webpage text
            val webpageText = httpClient.get(url).bodyAsText()

            // 2. Send text to an AI for parsing (this is a placeholder for a real AI call)
            // The prompt engineering is critical here. You instruct the AI to return JSON.
            val aiResponseJson = callAiToParseText(webpageText)

            // 3. Deserialize the AI's JSON response into our DTO
            val parsedDto = Json.decodeFromString<RecipeDto>(aiResponseJson)
            Result.Success(parsedDto)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.SERVICE_UNAVAILABLE)
        }
    }

    private suspend fun callAiToParseText(text: String): String {
        val apiKey = System.getenv("OPENAI_API_KEY")
            ?: throw IllegalStateException("OPENAI_API_KEY not set")


        // In a real app, you would make a Ktor call to the Gemini/OpenAI API here.
        // For now, we return a hardcoded dummy JSON for testing.
        println("--- Sending text to AI for parsing (mocked) ---")
        return """
            {
                "title": "AI Parsed Pancakes",
                "category": "Breakfast",
                "prepTimeMinutes": 20,
                "ingredients": [
                    {"name": "Flour", "quantity": "1.5", "unit": "cups"},
                    {"name": "Sugar", "quantity": "2", "unit": "tbsp"},
                    {"name": "Baking Powder", "quantity": "2", "unit": "tsp"},
                    {"name": "Salt", "quantity": "1", "unit": "pinch"},
                    {"name": "Milk", "quantity": "1.25", "unit": "cups"},
                    {"name": "Egg", "quantity": "1", "unit": ""},
                    {"name": "Melted Butter", "quantity": "3", "unit": "tbsp"}
                ],
                "directions": [
                    {"description": "In a large bowl, mix together flour, sugar, baking powder and salt."},
                    {"description": "Pour in milk, egg and melted butter; whisk until just combined."},
                    {"description": "Heat a lightly oiled griddle or frying pan over medium-high heat."},
                    {"description": "Pour or scoop the batter onto the griddle, using approximately 1/4 cup for each pancake. Brown on both sides and serve hot."}
                ]
            }
        """.trimIndent()
    }
}