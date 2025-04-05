package com.example.grocerychecklist.ui.component

import ItemCategory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.grocerychecklist.viewmodel.checklist.ChecklistItemData


/**
 * A composable function that displays a bottom sheet for adding or editing a checklist item.
 *
 * @param selectedItem The currently selected checklist item.  If null, the bottom sheet is in "add" mode.
 *                     If not null, the bottom sheet is in "edit" mode, pre-populated with the item's data.
 * @param isOpen A boolean indicating whether the bottom sheet is open.
 * @param onClose A lambda function to be called when the bottom sheet is closed (canceled).
 * @param onAdd A lambda function to be called when the "Add" or "Edit" button is clicked.  It takes the
 *              item's name, category, price, and quantity as arguments. Note that price and quantity
 *              are converted to Double and Int respectively within the composable.
 * @param onVisible A lambda function that is called when the visibility of the bottom sheet changes. It
 *                  receives a boolean argument indicating whether the bottom sheet is currently visible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetChecklistItem(
    selectedItem: ChecklistItemData? = null,
    isOpen: Boolean,
    onClose: () -> Unit,
    onAdd: (String, ItemCategory, Double, Int) -> Unit,
    onVisible: (Boolean) -> Unit = {}
) {

    var name by remember { mutableStateOf(selectedItem?.name ?: "") }
    var category by remember { mutableStateOf(selectedItem?.category?.let { ItemCategory.valueOf(it.name) } ?: ItemCategory.OTHER) }
    var price by remember { mutableStateOf(selectedItem?.price?.toString() ?: "") }
    var quantity by remember { mutableStateOf(selectedItem?.quantity?.toString() ?: "")  }

    LaunchedEffect(isOpen) {
        if (!isOpen) {
            name = ""
            category = ItemCategory.OTHER
            price = ""
            quantity = ""
        } else {
            name = selectedItem?.name ?: ""
            category = selectedItem?.category?.let { ItemCategory.valueOf(it.name) } ?: ItemCategory.OTHER
            price = selectedItem?.price?.toString() ?: ""
            quantity = selectedItem?.quantity?.toString() ?: ""
        }
    }

    BottomSheet(
        isOpen = isOpen,
        onClose = onClose,
        skipExpand = true,
        onVisibilityChanged = { visible -> onVisible(visible) }
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(
                    start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp,
                )
        ) {
            Text(
                "${if(selectedItem == null) "Add" else "Edit"} Item",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )

            CategoryDropdown(
                selectedCategory = category,
                onCategorySelected = { category = it }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = price,
                onValueChange = { if (it.isDigitsOnly()) price = it },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = quantity,
                onValueChange = { if (it.isDigitsOnly()) quantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onClose() }
                ) { Text("Cancel") }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { onAdd(name, category, price.toDouble(), quantity.toInt()) },
                ) { Text( if(selectedItem == null) "Add" else "Save" ) }
            }
        }
    }
}