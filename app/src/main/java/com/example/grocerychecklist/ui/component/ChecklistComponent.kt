package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.example.grocerychecklist.ui.theme.Typography
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
            .padding(8.dp, 0.dp).heightIn(min = 60.dp, max = 60.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        ChecklistIconComponent(
            color = iconColor,
            icon = icon
        )
        Spacer(
            modifier = Modifier.width(8.dp)
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    name,
                    fontSize = 16.sp,
                )

                when (variant) {
                    ChecklistComponentVariant.Checklist -> {
                        Text(
                            date,
                            color = Color.Gray,
                            style = Typography.labelMedium
                        )
                    }

                    ChecklistComponentVariant.History -> {
                        if (expense != null) {
                            val converter = ConvertNumToCurrency()
                            Text(
                                converter(Currency.PHP, expense),
                                fontSize = 16.sp,
                                style = Typography.labelMedium
                            )
                        }
                    }
                }
            }
            when (variant) {
                ChecklistComponentVariant.Checklist -> {
                    if (description != null) {
                        Text(
                            description,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(0.78f),
                            style = Typography.labelMedium,
                            maxLines = 2,
                        )
                    }
                }

                ChecklistComponentVariant.History -> {
                    Text(
                        date,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(0.78f),
                        maxLines = 2,
                    )
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
        variant = ChecklistComponentVariant.Checklist,
        description = "A checklist of the main groceries for the month. All the essentials...",
        expense = 400.00
    )
}