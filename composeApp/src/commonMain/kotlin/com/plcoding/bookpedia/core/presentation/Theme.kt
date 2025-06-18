import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.plcoding.bookpedia.core.presentation.MediumGreen

val customColorScheme = lightColorScheme(
    primary = MediumGreen, // Your preferred primary color (e.g., green)
    onPrimary = Color.White,
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFF00906C),
    background = Color.White,
    onBackground = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White
)
