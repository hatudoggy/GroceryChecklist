import android.graphics.Color.WHITE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ItemTagComponent(category: ItemCategory){
    Row(
        modifier = Modifier
            .height(20.dp)
            .background(color = category.color, shape = RoundedCornerShape(size = 12.dp))
            .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.text,

            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight(400),
                color = Color(WHITE)
            )
        )
    }
}

@Preview
@Composable
private fun ItemTagPreview() {
    ItemTagComponent(category = ItemCategory.CLEANING) // Passing a valid category for the preview
}