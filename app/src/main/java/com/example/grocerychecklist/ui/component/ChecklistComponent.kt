package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.domain.usecase.ConvertNumToCurrency
import com.example.grocerychecklist.domain.usecase.Currency
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class ChecklistComponentVariant {
    Checklist,
    History,
}

@Composable
fun ChecklistComponent(
    name: String,
    icon: ImageVector,
    iconColor: Color,
    variant: ChecklistComponentVariant,
    date: String,
    description: String? = null,
    expense: Double? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        ChecklistIconComponent(
            color = iconColor,
            icon = icon
        )
        Spacer(
            modifier = Modifier.width(8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    name,
                    fontSize = 16.sp,
                )

                when (variant) {
                    ChecklistComponentVariant.Checklist -> {
                        if (description != null) {
                            Text(
                                description,
                                color = Color.Gray
                            )
                        }
                    }
                    ChecklistComponentVariant.History -> {
                        Text(
                            date,
                            color = Color.Gray
                        )
                    }
                }
            }
            Column {

                when (variant) {
                    ChecklistComponentVariant.Checklist -> {
                        Text(
                            date,
                            color = Color.Gray
                        )
                    }
                    ChecklistComponentVariant.History -> {
                        if (expense != null){
                            val converter = ConvertNumToCurrency()
                            Text(
                                converter(Currency.PHP, expense),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistComponentPreview() {
    ChecklistComponent(
        name = "Main Grocery",
        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        icon = Icons.Filled.Android,
        iconColor = MaterialTheme.colorScheme.primary,
        variant = ChecklistComponentVariant.History,
        description = "Description",
        expense = 400.00
    )
}