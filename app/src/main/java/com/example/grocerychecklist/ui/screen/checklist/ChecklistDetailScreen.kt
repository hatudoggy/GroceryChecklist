package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.R
import com.example.grocerychecklist.ui.component.ChecklistCategory
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.LightGray
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailEvent

@Composable
fun ChecklistDetailScreen(
    //state: DashboardBreakdownState,
    onEvent: (ChecklistDetailEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBarComponent(
                title = "Checklist",
                onNavigateBackClick = { onEvent(ChecklistDetailEvent.NavigateBack) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CheckListOverview(
                icon = Icons.Default.Fastfood,
                title = stringResource(R.string.checklist_title),
                description = stringResource(R.string.checklist_description),
                iconColor = ChecklistCategory.MAIN_GROCERY.color
            )

            CheckListStats(
                quantity = "20",
                totalPrice = "10,025",
                lastShop = "Aug 20"
            )

            CheckListButtons(
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun CheckListOverview(
    icon: ImageVector,
    title: String,
    description: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CheckListIcon(icon = icon, color = iconColor, size = 80.dp)

        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = description,
            textAlign = TextAlign.Justify,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CheckListStats(
    quantity: String,
    totalPrice: String,
    lastShop: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CheckListStatColumn(quantity, "Items")
        CheckListStatColumn("₱$totalPrice", "Total")
        CheckListStatColumn(lastShop, "Last Shop")
    }
}

@Composable
fun CheckListStatColumn(
    value: String,
    attribute: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = value,
            color = Color(0xFF6FA539),
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp
        )

        Text(
            text = attribute,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 11.sp
        )
    }
}

@Composable
fun CheckListButtons(
    modifier: Modifier = Modifier,
    onEvent: (ChecklistDetailEvent) -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonModifier = Modifier
            .fillMaxWidth()
            .height(35.dp)

        CheckListButton(
            text = "View Checklist",
            icon = Icons.Default.Visibility,
            backgroundColor = LightGray,
            textColor = Color.Black,
            modifier = buttonModifier,
            onClick = { onEvent(ChecklistDetailEvent.NavigateViewMode) }
        )

        CheckListButton(
            text = "Edit Checklist",
            icon = Icons.Default.Edit,
            backgroundColor = LightGray,
            textColor = Color.Black,
            modifier = buttonModifier,
            onClick = { onEvent(ChecklistDetailEvent.NavigateEditMode) }
        )

        CheckListButton(
            text = "Start Shopping",
            icon = Icons.Default.ShoppingCart,
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = Color.White,
            modifier = buttonModifier,
            onClick = { onEvent(ChecklistDetailEvent.NavigateStartMode) }
        )
    }
}

@Composable
fun CheckListButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        )
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun CheckListIcon(
    icon: ImageVector,
    color: Color,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (color.luminance() > 0.65f) Color.Black else Color.White,
            modifier = Modifier.size(size * 0.8f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenPreview() {
    ChecklistDetailScreen(
        onEvent = {}
    )
}