package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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

@Composable
fun ChecklistDetailScreen() {
    Scaffold(topBar = { TopBarComponent(title = "Checklist") }) { innerPadding ->
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

            CheckListButtons()
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
fun CheckListButtons(modifier: Modifier = Modifier) {
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
            backgroundColor = Color(0xFFF0F0F0),
            textColor = Color.Black,
            modifier = buttonModifier
        )

        CheckListButton(
            text = "Edit Checklist",
            icon = Icons.Default.Edit,
            backgroundColor = Color(0xFFF0F0F0),
            textColor = Color.Black,
            modifier = buttonModifier
        )

        CheckListButton(
            text = "Start Shopping",
            icon = Icons.Default.ShoppingCart,
            backgroundColor = Color(0xFF6FA539),
            textColor = Color.White,
            modifier = buttonModifier
        )
    }
}

@Composable
fun CheckListButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier
) {
    Button(
        onClick = {},
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
    ChecklistDetailScreen()
}