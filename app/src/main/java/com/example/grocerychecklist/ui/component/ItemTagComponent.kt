import android.graphics.Color.WHITE
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .border(1.dp, Color.LightGray, RoundedCornerShape(100))
            .padding(start = 6.dp, top = 2.dp, end = 8.dp, bottom = 2.dp),

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Circle,
            contentDescription = "Color",
            Modifier.size(14.dp),
            tint = category.color
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = category.text,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemTagPreview() {
    ItemTagComponent(category = ItemCategory.CLEANING) // Passing a valid category for the preview
}