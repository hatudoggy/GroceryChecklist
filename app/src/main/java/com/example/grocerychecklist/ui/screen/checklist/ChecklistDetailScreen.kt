package com.example.grocerychecklist.ui.screen.checklist

import ItemCategory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
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
import com.example.grocerychecklist.ui.theme.PrimaryDarkGreen
import com.example.grocerychecklist.ui.theme.PrimaryLightGreen
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailUIState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistDetailViewModel
import java.time.LocalDateTime

data class CategorySummary(
    val itemCategory: ItemCategory,
    val itemCount: Int,
    val totalPrice: Double,
)

@Composable
internal fun ChecklistDetailScreen(
    viewModel: ChecklistDetailViewModel
){
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val checklist = if ( state is ChecklistDetailUIState.Success) (state as ChecklistDetailUIState.Success).checklistDetails else null

    ChecklistDetailScreen(
        state = state,
        onNavigateBackClick = { onEvent(ChecklistDetailEvent.NavigateBack) },
        onStartShoppingClicked = { checklist?.let { onEvent(ChecklistDetailEvent.NavigateStartMode(it.id, checklist.name)) } },
        loadChecklistDetails = { onEvent(ChecklistDetailEvent.LoadData) },
        onViewMoreCategories = { checklist?.let { onEvent(ChecklistDetailEvent.NavigateViewMode(it.id, checklist.name)) } },
        onCategoryClick = { itemCategory -> checklist?.let { onEvent(ChecklistDetailEvent.NavigateViewMode(it.id, it.name, itemCategory)) } },
    )
}

@Composable
internal fun ChecklistDetailScreen(
    state: ChecklistDetailUIState,
    onNavigateBackClick: () -> Unit = {},
    onStartShoppingClicked: () -> Unit = {},
    onViewMoreCategories: () -> Unit = {},
    onCategoryClick: (ItemCategory) -> Unit = {},
    loadChecklistDetails: () -> Unit = {}
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
            is ChecklistDetailUIState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), contentAlignment = Alignment.Center) {
                    LoadingComponent(loadingMessage = R.string.checklist_loading)
                }
            }

            is ChecklistDetailUIState.Success -> {
                val checklist = state.checklistDetails
                val categories = state.checklistDetails.categoriesSummary

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

                    Spacer(modifier = Modifier.height(4.dp))

                    CategoryListSection(
                        categories = categories,
                        modifier = Modifier.fillMaxWidth(),
                        onViewMoreClick = onViewMoreCategories,
                        onCategoryClick = onCategoryClick,
                        checklistIconOption = checklist.icon
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CheckListButton(
                        text = "Start Shopping",
                        icon = Icons.Default.ShoppingCart,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        textColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = { onStartShoppingClicked() }
                    )
                }
            }

            is ChecklistDetailUIState.Error -> {
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
        modifier = modifier.padding(vertical = 16.dp),
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

@Composable
fun CategoryListSection(
    modifier: Modifier = Modifier,
    categories: List<CategorySummary>,
    checklistIconOption: IconOption = IconOption.MAIN_GROCERY,
    onViewMoreClick: () -> Unit = {},
    onCategoryClick: (ItemCategory) -> Unit = {}
) {
    Column(modifier = modifier.defaultMinSize(
        minHeight = 300.dp
    )) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            TextButton(onClick = onViewMoreClick) {
                Text(
                    text = "View More",
                    color = PrimaryLightGreen,
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (categories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "No items found",
                        color = Color.Gray,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    return@Column
                }
            }
            categories.take(3).forEach { category ->
                CategoryCard(
                    itemCategory = category.itemCategory,
                    itemCount = category.itemCount,
                    totalPrice = category.totalPrice,
                    onClick = { onCategoryClick(category.itemCategory) },
                    checklistIconOption = checklistIconOption
                )
            }

            if (categories.size >= 3) {
                Text(
                    text = "+${categories.size - 2} more categories",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    itemCategory: ItemCategory,
    itemCount: Int,
    totalPrice: Double,
    checklistIconOption: IconOption = IconOption.MAIN_GROCERY,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGray
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CheckListIcon(
                    //TODO: replace with category icon
                    icon = checklistIconOption,
                    colorOption = ColorOption.CopyIcyBlue,
                    size = 40.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = if (itemCategory == ItemCategory.OTHER) "Uncategorized" else itemCategory.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$itemCount items",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Text(
                text = "₱${"%.2f".format(totalPrice)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = PrimaryDarkGreen
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenLoadedPreview() {
    val successState = ChecklistDetailUIState.Success(
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
            itemCount = 21,
            categoriesSummary = listOf(
                CategorySummary(
                    itemCategory = ItemCategory.FRUIT,
                    itemCount = 5,
                    totalPrice = 500.00
                ),
                CategorySummary(
                    itemCategory = ItemCategory.CLEANING,
                    itemCount = 10,
                    totalPrice = 1000.00
                ),
                CategorySummary(
                    itemCategory = ItemCategory.DAIRY,
                    itemCount = 3,
                    totalPrice = 300.00
                )
            )
        )
    )

    ChecklistDetailScreen(
        state = successState
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenLoadingPreview() {
    val loadingState = ChecklistDetailUIState.Loading

    ChecklistDetailScreen(
        state = loadingState
    )
}

@Preview(showBackground = true)
@Composable
fun ChecklistDetailScreenErrorPreview() {
    val errorState = ChecklistDetailUIState.Error(
        message = "Error loading checklist"
    )

    ChecklistDetailScreen(
        state = errorState
    )
}
