import androidx.compose.ui.graphics.Color
import com.example.grocerychecklist.ui.theme.Maroon

enum class ItemCategory(val color: Color, val text: String) {
    ALL(Color(0xFF6FA539), "All"),
    GRAIN(Color(0xFFF5E1A4), "Grain"),
    POULTRY(Color(0xFFF1A14B), "Poultry"),
    MEAT(Color(0xFFCC7A57), "Meat"),
    DAIRY(Color(0xFFFFE0B2), "Dairy"),
    FRUIT(Color(0xFFEC407A), "Fruit"),
    MEDICINE(Color(0xFFB71C1C), "Medicine"),
    VEGETABLE(Color(0xFF8BC34A), "Vegetable"),
    SANITARY(Color(0xFF4CAF50), "Sanitary"),
    CLEANING(Color(0xFF81D4FA), "Cleaning"),
    COSMETIC(Color(0xFF9575CD), "Cosmetic"),
    OTHER(Color(0xFFB0BEC5), "Other"),
}
