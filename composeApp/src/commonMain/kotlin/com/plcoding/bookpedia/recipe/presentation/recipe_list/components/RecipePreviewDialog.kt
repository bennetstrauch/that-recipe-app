package com.plcoding.bookpedia.recipe.presentation.recipe_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import org.jetbrains.compose.resources.painterResource


@Composable
fun RecipePreviewDialog(
    url: String,
    isParsing: Boolean,
    onUrlChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onLoad: () -> Unit,
    onParse: () -> Unit
) {
    val webViewState = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()


    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            // --- THIS IS THE CORRECTED LAYOUT ---
            Column(modifier = Modifier.fillMaxSize()) {

                // Top bar with clickable recipe icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top
                ) {
                    recipeWebsites.forEach { website ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // --- CHANGED: Clicking an icon now updates the URL field and loads the page ---
                                    onUrlChange(website.url)
                                    navigator.loadUrl(website.url)
                                }
                                .padding(horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(website.icon),
                                contentDescription = website.label,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = website.label,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                HorizontalDivider()
                // Sub-Top bar for URL input and loading
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = url,
                        onValueChange = onUrlChange,
                        label = { Text("Paste URL") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(onClick = onLoad, enabled = !isParsing) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Load URL")
                    }
                }

                // WebView to display the content
                WebView(
                    state = webViewState,
                    navigator = navigator,
                    modifier = Modifier.weight(1f) // This takes up the remaining space
                )

                // Bottom bar for actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Navigation controls on the left
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { navigator.navigateBack() },
                            enabled = navigator.canGoBack
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
                        }
                        IconButton(
                            onClick = { navigator.navigateForward() },
                            enabled = navigator.canGoForward
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go Forward")
                        }
                    }

                    // Action buttons on the right
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss, enabled = !isParsing) {
                            Text("Cancel")
                        }
//                        ## for later on
//                        Button(onClick = onParse, enabled = !isParsing && webViewState.lastLoadedUrl != null) {
//                            if (isParsing) {
//                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
//                            } else {
//                                Text("Parse this Page")
//                            }
//                        }
                    }
                }
            }
        }
    }
}