package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

enum class ButtonCardComponentVariant {
    Checklist,
    History,
}

@Composable
fun ButtonCardComponent(
    name: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconColor: Color = Color.White,
    variant: ButtonCardComponentVariant,
    date: String,
    description: String? = null,
    expense: Double? = null,
    onClick: () -> Unit = {},
    isClicked: Boolean? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp, 0.dp)
            .heightIn(min = 60.dp, max = 60.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        ButtonCardIconComponent(
            backgroundColor = iconBackgroundColor,
            iconColor = iconColor,
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
                    ButtonCardComponentVariant.Checklist -> {
                        Text(
                            date,
                            color = Color.Gray,
                            style = Typography.labelMedium
                        )
                    }

                    ButtonCardComponentVariant.History -> {
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
                ButtonCardComponentVariant.Checklist -> {
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

                ButtonCardComponentVariant.History -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            date,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(0.78f),
                            maxLines = 2,
                        )
                        if (isClicked != null)
                            RotatingArrowIconComponent(Color.Black, isClicked)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistComponentPreview() {
    ButtonCardComponent(
        name = "Main Grocery",
        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")),
        icon = Icons.Filled.Android,
        iconBackgroundColor = MaterialTheme.colorScheme.primary,
        variant = ButtonCardComponentVariant.History,
        description = "A checklist of the main groceries for the month. All the essentials...",
        expense = 400.00,
        isClicked = true
    )
}