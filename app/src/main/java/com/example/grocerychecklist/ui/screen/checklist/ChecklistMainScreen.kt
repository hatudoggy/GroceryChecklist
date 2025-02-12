package com.example.grocerychecklist.ui.screen.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.ui.component.ButtonCardComponent
import com.example.grocerychecklist.ui.component.ButtonCardComponentVariant
import com.example.grocerychecklist.ui.component.ButtonCardIconComponent
import com.example.grocerychecklist.ui.component.ChecklistCategory
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.theme.PrimaryGreenSurface
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainEvent
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainState
import com.example.grocerychecklist.viewmodel.checklist.ChecklistMainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistMainScreen(
    state: ChecklistMainState,
    viewModel: ChecklistMainViewModel,
    onEvent: (ChecklistMainEvent) -> Unit,
) {
//    val filteredItems by viewModel.filteredItems.collectAsState(emptyList())
//    val searchQuery by viewModel.searchQuery.collectAsState()

    // Bottom sheet content
    CreateChecklist(
        isOpen = state.isDrawerOpen,
        onClose = { onEvent(ChecklistMainEvent.CloseDrawer) },
        onEvent = onEvent,
        state = state
    )

    DialogModal(
        isOpen = state.isIconPickerOpen,
        onClose = { onEvent(ChecklistMainEvent.CloseIconPicker) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ChecklistCategory.entries.size) { index ->
                    val category = ChecklistCategory.entries[index]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onEvent(
                                    ChecklistMainEvent.UpdateChecklistIcon(
                                        IconOption.fromName(category.text)
                                            ?: IconOption.MAIN_GROCERY,
                                        ColorOption.fromColor(category.color)
                                            ?: ColorOption.CopySkyGreen
                                    )
                                )
                                onEvent(ChecklistMainEvent.CloseIconPicker)
                            }
                    ) {
                        ButtonCardIconComponent(
                            backgroundColor = category.color,
                            icon = category.icon,
                            wrapperSize = 56.dp,
                            iconSize = 30.dp
                        )
                    }
                }
            }
//            HorizontalDivider()
//            LazyRow(
//                //modifier = Modifier.weight(1f)
//            ) {
//                items(5) {
//                    Icon(
//                        Icons.Filled.Circle,
//                        contentDescription = "color",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(48.dp)
//                    )
//                }
//            }
        }
    }


    // Main screen content
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    onEvent(ChecklistMainEvent.OpenDrawer)
                },
                containerColor = PrimaryGreenSurface
            ) {
                Icon(Icons.Filled.Add, "Add FAB")
            }
        },
        topBar = { TopBarComponent(title = "Checklist") },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            RoundedTextField(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(percent = 50)
                    ),
                fontSize = 16.sp,
                placeholderText = "Search",
                value = state.searchQuery,
                onValueChange = { e -> onEvent(ChecklistMainEvent.SearchChecklist(e)) }
            )
            Spacer(Modifier.height(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                state.checklists.forEach { item ->
                    ButtonCardComponent(
                        name = item.name,
                        description = item.description,
                        date = item.updatedAt?.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                            .toString(),
                        icon = item.icon.imageVector,
                        iconBackgroundColor = item.iconBackgroundColor.color,
                        variant = ButtonCardComponentVariant.Checklist,
                        onClick = { onEvent(ChecklistMainEvent.NavigateChecklist) }
                    )
                }
            }
        }
    }
}

// Bottom sheet modal drawer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChecklist(
    isOpen: Boolean,
    onClose: () -> Unit,
    onEvent: (ChecklistMainEvent) -> Unit,
    state: ChecklistMainState,
) {
    BottomSheet(
        isOpen = isOpen,
        onClose = onClose,
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp,
                )
        ) {
            Text(
                "Create a Checklist",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onEvent(ChecklistMainEvent.OpenIconPicker)
                        }
                ) {
                    ButtonCardIconComponent(
                        backgroundColor = state.newChecklist.iconBackgroundColor.color,
                        icon = state.newChecklist.icon.imageVector,
                        wrapperSize = 56.dp,
                        iconSize = 30.dp
                    )
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.newChecklist.name,
                    onValueChange = { e ->
                        onEvent(
                            ChecklistMainEvent.UpdateChecklistName(
                                e
                            )
                        )
                    },
                    label = { Text("Name") }
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 5,
                value = state.newChecklist.description,
                onValueChange = { e ->
                    onEvent(
                        ChecklistMainEvent.UpdateChecklistDescription(
                            e
                        )
                    )
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onEvent(ChecklistMainEvent.CloseDrawer) }
                ) { Text("Cancel") }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {
                        onEvent(ChecklistMainEvent.AddChecklist(state.newChecklist))
                        onEvent(ChecklistMainEvent.CloseDrawer)
                    },
                ) { Text("Add") }
            }
        }
    }
}

// Bottom sheet modal drawer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    sheetContent: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onClose,
            sheetState = sheetState,
            containerColor = Color.White,
        ) {
            sheetContent()
        }
    }
}

// Dialog for icon picker
@Composable
fun DialogModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    dialogContent: @Composable () -> Unit
) {
    if (isOpen) {
        Dialog(
            onDismissRequest = onClose,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                dialogContent()
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ChecklistMainScreenPreview() {
//    val state: ChecklistMainState = ChecklistMainState()
//    ChecklistMainScreen(
//        state = state,
//        viewModel = {},
//        onEvent = {}
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    val sheetState = rememberStandardBottomSheetState()
    var text by remember { mutableStateOf("Hello") }

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    start = 24.dp, end = 24.dp, top = 12.dp, bottom = 8.dp,
                )
        ) {
            Text(
                "Create a Checklist",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ButtonCardIconComponent(
                    backgroundColor = ChecklistCategory.MAIN_GROCERY.color,
                    icon = ChecklistCategory.MAIN_GROCERY.icon,
                    wrapperSize = 56.dp,
                    iconSize = 30.dp
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Name") }
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 5,
                value = text,
                onValueChange = { text = it },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {}
                ) { Text("Cancel") }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {},
                ) { Text("Add") }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun IconPickerPreview() {

    Dialog(
        onDismissRequest = {},
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ChecklistCategory.entries.size) { index ->
                        val category = ChecklistCategory.entries[index]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {

                                }
                        ) {
                            ButtonCardIconComponent(
                                backgroundColor = category.color,
                                icon = category.icon,
                                wrapperSize = 56.dp,
                                iconSize = 30.dp
                            )
                        }
                    }
                }
//                HorizontalDivider()
//                LazyRow(
//                    //modifier = Modifier.weight(1f)
//                ) {
//                    items(5) {
//                        Icon(
//                            Icons.Filled.Circle,
//                            contentDescription = "color",
//                            tint = Color.Gray,
//                            modifier = Modifier.size(48.dp)
//                        )
//                    }
//                }
            }
        }
    }
}