package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.repository.ChecklistDetails
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.ui.component.ErrorComponent
import com.example.grocerychecklist.ui.component.LoadingComponent
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.theme.LightGray
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailViewModel
import java.time.LocalDateTime

@Composable
internal fun ChecklistDetailScreen(
    viewModel: ChecklistDetailViewModel
){
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    ChecklistDetailScreen(
        state = state,
        onNavigateBackClick = { onEvent(ChecklistDetailEvent.NavigateBack) },
        onStartShoppingClicked = { checklistId, checklistName -> onEvent(ChecklistDetailEvent.NavigateStartMode(checklistId, checklistName))},
        loadChecklistDetails = { onEvent(ChecklistDetailEvent.LoadData) }
    )
}

@Composable
internal fun ChecklistDetailScreen(
    state: ChecklistDetailState,
    onNavigateBackClick: () -> Unit,
    onStartShoppingClicked: (Long, String) -> Unit,
    loadChecklistDetails: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarComponent(
                title = "Checklist",
                onNavigateBackClick = onNavigateBackClick
            )
        }
    ) { innerPadding ->
        when (state) {
            is ChecklistDetailState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), contentAlignment = Alignment.Center) {
                    LoadingComponent(loadingMessage = R.string.checklist_loading)
                }
            }

            is ChecklistDetailState.Success -> {
                val checklist = state.checklistDetails

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CheckListOverview(
                        icon = checklist.icon,
                        title = checklist.name,
                        description = checklist.description,
                        iconColor = checklist.iconBackgroundColor
                    )

                    CheckListStats(
                        quantity = checklist.itemCount.toString(),
                        totalPrice = checklist.totalPrice.toString(),
                        lastShopAt = checklist.lastShopAt?.let { DateUtility.formatDateWithDay(it) } ?: "N/A"
                    )

                    CheckListButtons(
                        onStartShoppingClicked = { onStartShoppingClicked(checklist.id, checklist.name)}
                    )
                }
            }

            is ChecklistDetailState.Error -> {
                val errorMessage = state.message
                val defaultErrorMessage = stringResource(id = R.string.checklist_error)
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), contentAlignment = Alignment.Center) {
                    ErrorComponent(errorMessage = errorMessage?: defaultErrorMessage, onRetry = loadChecklistDetails)
                }
            }
        }
    }
}

@Composable
fun CheckListOverview(
    icon: IconOption,
    title: String,
    description: String,
    iconColor: ColorOption,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CheckListIcon(icon = icon, colorOption = iconColor, size = 80.dp)

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
    lastShopAt: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckListStatColumn(quantity, "Items", Modifier.weight(1f))
        CheckListStatColumn("₱$totalPrice", "Total", Modifier.weight(1f))
        CheckListStatColumn(lastShopAt, "Last Shop", Modifier.weight(1f))
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
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 0.dp)
        )

        Text(
            text = attribute,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
    }
}

@Composable
fun CheckListButtons(
    modifier: Modifier = Modifier,
    onStartShoppingClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonModifier = Modifier
            .fillMaxWidth()
            .height(35.dp)

        CheckListButton(
            text = "View Checklist",
            icon = Icons.Default.Edit,
            backgroundColor = LightGray,
            textColor = Color.Black,
            modifier = buttonModifier,
            onClick = { onStartShoppingClicked }
        )

        CheckListButton(
            text = "Start Shopping",
            icon = Icons.Default.ShoppingCart,
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = Color.White,
            modifier = buttonModifier,
            onClick = { onStartShoppingClicked }
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
    icon: IconOption,
    colorOption: ColorOption,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorOption.color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = null,
            tint = if (colorOption.color.luminance() > 0.65f) Color.Black else Color.White,
            modifier = Modifier.size(size * 0.8f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenLoadedPreview() {
    val successState = ChecklistDetailState.Success(
        ChecklistDetails(
            id = 1,
            name = "Grocery List",
            description = "Weekly shopping items",
            icon = IconOption.MAIN_GROCERY,
            iconBackgroundColor = ColorOption.CopyIcyBlue,
            lastShopAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            lastOpenedAt = LocalDateTime.now(),
            totalPrice = 5430.24,
            itemCount = 21
        )
    )

    ChecklistDetailScreen(
        state = successState,
        onNavigateBackClick = {},
        onStartShoppingClicked = { _, _ -> },
        loadChecklistDetails = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenLoadingPreview() {
    val loadingState = ChecklistDetailState.Loading

    ChecklistDetailScreen(
        state = loadingState,
        onNavigateBackClick = {},
        onStartShoppingClicked = { _, _ -> },
        loadChecklistDetails = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenErrorPreview() {
    val errorState = ChecklistDetailState.Error(
        message = "Error loading checklist"
    )

    ChecklistDetailScreen(
        state = errorState,
        onNavigateBackClick = {},
        onStartShoppingClicked = { _, _ -> },
        loadChecklistDetails = {}
    )
}
