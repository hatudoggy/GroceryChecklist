import androidx.compose.ui.graphics.Color
import com.example.grocerychecklist.ui.theme.Maroon

enum class ItemCategory(val color: Color, val text: String) {
    ALL(Color(0xFF6FA539), "All"),
    POULTRY(Color(0xFFF3884B), "Poultry"),
    MEAT(Color(0xFFF0CD52), "Meat"),
    FRUIT(Color(0xFFAF64EA), "Fruit"),
    MEDICINE(Maroon, "Medicine"),
    VEGETABLE(Color(0xFF5BCBE3), "Vegetable"),
    SANITARY(Color(0xFF8FE35B), "Sanitary"),
    CLEANING(Color(0xFFF255A0), "Cleaning"),
}
