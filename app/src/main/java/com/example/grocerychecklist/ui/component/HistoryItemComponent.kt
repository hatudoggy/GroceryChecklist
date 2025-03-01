import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import com.example.grocerychecklist.ui.component.Measurement
import com.example.grocerychecklist.ui.component.getText
import com.example.grocerychecklist.ui.screen.history.HistoryDataDetails

@Composable
fun HistoryItemComponent(historyItem: HistoryDataDetails) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x20000000)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = historyItem.name,
                style = TextStyle(
                    fontSize = 18.sp, // Increased font size
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )

            ItemTagComponent(historyItem.category)
        }

        // Right Column
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            val converter = ConvertNumToCurrency()
            // Price per unit
            Text(
                text = converter(Currency.PHP, historyItem.price, true) + "/" + historyItem.measurement.singularText,
                style = TextStyle(
                    fontSize = 14.sp, // Increased font size
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFA5A5A5)
                ),
                textAlign = TextAlign.Right
            )

            Text(
                text = "x " + historyItem.quantity + " " + historyItem.measurement.getText(quantity = historyItem.quantity),
                style = TextStyle(
                    fontSize = 14.sp, // Increased font size
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA5A5A5)
                ),
                textAlign = TextAlign.Right
            )

            // Total Price
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = converter(Currency.PHP, historyItem.price * historyItem.quantity, true),
                style = TextStyle(
                    fontSize = 18.sp, // Increased font size
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ),
                textAlign = TextAlign.Right
            )
        }
    }
}

@Preview
@Composable
fun HistoryItemPreview(){
    HistoryItemComponent(historyItem =
        HistoryDataDetails(
            name = "Paracetamol",
            category = ItemCategory.MEDICINE,
            price = 120.0,
            quantity = 1.0,
            measurement = Measurement.PACK
        )
    )
}